package com.example.demo.infra.adapter;

import com.example.demo.application.port.out.ProductImageStoragePort;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;
import jakarta.annotation.PostConstruct;

/**
 * MinioProductImageStorageAdapter - MinIO 商品圖片儲存適配器 (Infrastructure Layer - Outbound Adapter)。
 * <p>
 * 實作 {@link ProductImageStoragePort}，負責處理底層物件儲存空間 (MinIO) 的具體操作。
 * 依據架構規範宣告為 package-private 以隱藏基礎設施實作細節。
 * </p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
class MinioProductImageStorageAdapter implements ProductImageStoragePort {

    private final MinioClient minioClient;

    @Value("${minio.bucket.name:products}")
    private String bucketName;

    @Value("${minio.url:http://localhost:9000}")
    private String minioUrl;

    @PostConstruct
    public void initBucket() {
        try {
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!found) {
                log.info("Bucket {} 不存在，建立中...", bucketName);
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }
            // 確保 Bucket 權限為公開讀取
            String policy = "{\"Version\":\"2012-10-17\",\"Statement\":[{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:GetObject\"],\"Resource\":[\"arn:aws:s3:::" + bucketName + "/*\"]}]}";
            minioClient.setBucketPolicy(io.minio.SetBucketPolicyArgs.builder().bucket(bucketName).config(policy).build());
            log.info("Bucket {} 已設定為公開讀取", bucketName);
        } catch (Exception e) {
            log.error("初始化 MinIO Bucket 失敗", e);
        }
    }

    @Override
    public String uploadImage(MultipartFile file) {
        try {

            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String objectName = UUID.randomUUID().toString() + extension;

            try (InputStream inputStream = file.getInputStream()) {
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(bucketName)
                                .object(objectName)
                                .stream(inputStream, file.getSize(), -1)
                                .contentType(file.getContentType())
                                .build()
                );
            }

            return minioUrl + "/" + bucketName + "/" + objectName;

        } catch (Exception e) {
            log.error("圖片上傳至 MinIO 失敗", e);
            throw new RuntimeException("圖片上傳至 MinIO 失敗: " + e.getMessage(), e);
        }
    }
}
