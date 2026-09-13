Write-Host "=========================================" -ForegroundColor Cyan
Write-Host " Omni Recommender E2E Integration Test " -ForegroundColor Cyan
Write-Host "=========================================" -ForegroundColor Cyan

# 1. Ensure MinIO Bucket exists
Write-Host "[1/5] Checking and creating MinIO bucket (ecommerce-data-lake)..." -ForegroundColor Yellow
docker run --rm --network ecommerce_ecommerce-network minio/mc alias set myminio http://ecommerce-minio:9000 minioadmin minioadmin | Out-Null
docker run --rm --network ecommerce_ecommerce-network minio/mc mb myminio/ecommerce-data-lake --ignore-existing | Out-Null
Write-Host "[1/5] MinIO bucket ready." -ForegroundColor Green

# 2. Send Behavior Logs
Write-Host "[2/5] Sending mock behavior events to behavior-service (Port 8084)..." -ForegroundColor Yellow
$headers = @{
    "Content-Type" = "application/json"
    "X-Tenant-ID" = "TTRAVEL"
}

$events = @(
    @{ tenantId = "TTRAVEL"; userId = "e2e-user-999"; itemId = "P-1001"; behaviorType = "VIEW" },
    @{ tenantId = "TTRAVEL"; userId = "e2e-user-999"; itemId = "P-1001"; behaviorType = "ADD_TO_CART" },
    @{ tenantId = "TTRAVEL"; userId = "e2e-user-999"; itemId = "P-1001"; behaviorType = "PURCHASE" },
    @{ tenantId = "TTRAVEL"; userId = "e2e-user-999"; itemId = "P-1002"; behaviorType = "VIEW" },
    @{ tenantId = "TTRAVEL"; userId = "e2e-user-888"; itemId = "P-1001"; behaviorType = "VIEW" }
)

foreach ($evt in $events) {
    # Add a mock sessionId to bypass validation if required by the service
    $evt.sessionId = "session-e2e-123"
    $body = $evt | ConvertTo-Json
    try {
        $response = Invoke-RestMethod -Uri "http://localhost:8084/api/v1/behaviors/log" -Method Post -Headers $headers -Body $body
        Write-Host "  Sent: $($evt.behaviorType) on $($evt.itemId) by $($evt.userId) -> success"
    } catch {
        Write-Host "  [ERROR] Failed to send event. Details: $_" -ForegroundColor Red
        if ($_.ErrorDetails) {
            Write-Host "  [ERROR BODY] $($_.ErrorDetails.Message)" -ForegroundColor Red
        }
        exit 1
    }
}
Write-Host "[2/5] Behavior events sent." -ForegroundColor Green

# 3. Run Kafka to Iceberg Ingestion via Docker
Write-Host "[3/5] Starting KafkaToIcebergIngestionJob (Stream) via Docker Maven..." -ForegroundColor Yellow
try {
    docker run --rm -v "$((Get-Item .).FullName):/usr/src/app" -v "$env:USERPROFILE\.m2:/root/.m2" -w /usr/src/app/spark-recommender --network ecommerce_ecommerce-network -e TEST_MODE=true -e KAFKA_BOOTSTRAP_SERVERS=ecommerce-kafka:29092 -e MINIO_ENDPOINT=http://ecommerce-minio:9000 -e REDIS_HOST=ecommerce-redis -e MAVEN_OPTS="--add-opens=java.base/sun.nio.ch=ALL-UNNAMED --add-opens=java.base/java.lang=ALL-UNNAMED --add-opens=java.base/java.lang.invoke=ALL-UNNAMED --add-opens=java.base/java.util=ALL-UNNAMED --add-opens=java.base/java.nio=ALL-UNNAMED" maven:3.9.6-eclipse-temurin-21 mvn compile exec:java "-Dexec.mainClass=com.omni.recommender.spark.KafkaToIcebergIngestionJob"
    if ($LASTEXITCODE -ne 0) { throw "Docker Maven execution failed" }
} catch {
    Write-Host "[ERROR] Failed to run Ingestion Job." -ForegroundColor Red
    exit 1
}
Write-Host "[3/5] Ingestion complete." -ForegroundColor Green

# 4. Run ALS Recommender Job via Docker
Write-Host "[4/5] Starting RecommenderBatchJob (ALS) via Docker Maven..." -ForegroundColor Yellow
try {
    docker run --rm -v "$((Get-Item .).FullName):/usr/src/app" -v "$env:USERPROFILE\.m2:/root/.m2" -w /usr/src/app/spark-recommender --network ecommerce_ecommerce-network -e MINIO_ENDPOINT=http://ecommerce-minio:9000 -e REDIS_HOST=ecommerce-redis -e MAVEN_OPTS="--add-opens=java.base/sun.nio.ch=ALL-UNNAMED --add-opens=java.base/java.lang=ALL-UNNAMED --add-opens=java.base/java.lang.invoke=ALL-UNNAMED --add-opens=java.base/java.util=ALL-UNNAMED --add-opens=java.base/java.nio=ALL-UNNAMED" maven:3.9.6-eclipse-temurin-21 mvn compile exec:java "-Dexec.mainClass=com.omni.recommender.spark.RecommenderBatchJob"
    if ($LASTEXITCODE -ne 0) { throw "Docker Maven execution failed" }
} catch {
    Write-Host "[ERROR] Failed to run Recommender Batch Job." -ForegroundColor Red
    exit 1
}
Write-Host "[4/5] Recommender Batch Job complete." -ForegroundColor Green

# 5. Verify Recommendations
Write-Host "[5/5] Verifying recommendations from recommendation-service (Port 8085)..." -ForegroundColor Yellow
try {
    $recs = Invoke-RestMethod -Uri "http://localhost:8085/api/v1/recommendations/e2e-user-999" -Method Get -Headers $headers
    
    if ($recs.items.Length -gt 0) {
        Write-Host "  Found $($recs.items.Length) recommended items for user e2e-user-999!" -ForegroundColor Green
        foreach ($item in $recs.items) {
            Write-Host "    - Item: $($item.itemId), Score: $($item.score)"
        }
        Write-Host ""
        Write-Host "=========================================" -ForegroundColor Cyan
        Write-Host " [SUCCESS] End-to-End Pipeline test passed! " -ForegroundColor Green
        Write-Host "=========================================" -ForegroundColor Cyan
    } else {
        Write-Host "  [WARNING] Recommendation list is empty (fallback or no data)." -ForegroundColor Yellow
    }
} catch {
    Write-Host "  [ERROR] Failed to fetch recommendations. Make sure recommendation-service is running on port 8085." -ForegroundColor Red
    exit 1
}
