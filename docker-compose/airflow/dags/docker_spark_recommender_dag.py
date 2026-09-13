from datetime import datetime, timedelta
from airflow import DAG
from airflow.providers.docker.operators.docker import DockerOperator
from docker.types import Mount

default_args = {
    'owner': 'omni-store',
    'depends_on_past': False,
    'email_on_failure': False,
    'email_on_retry': False,
    'retries': 1,
    'retry_delay': timedelta(minutes=5),
}

with DAG(
    'spark_recommender_batch',
    default_args=default_args,
    description='Run Spark ALS Recommendation Batch Job using Maven',
    schedule_interval='@daily',  # 每天執行一次
    start_date=datetime(2023, 1, 1),
    catchup=False,
    tags=['spark', 'recommendation'],
) as dag:

    # 由於我們掛載了 docker.sock，DockerOperator 會在您的宿主機上啟動新容器
    run_spark_batch = DockerOperator(
        task_id='run_als_batch_job',
        # 對齊我們專案使用的 Java 21 版本
        image='maven:3.9.6-eclipse-temurin-21',
        api_version='auto',
        auto_remove='force',  # 執行完畢後自動刪除暫時的 Maven 容器
        command='bash -c "mvn compile exec:java -Dexec.mainClass=com.omni.recommender.spark.RecommenderBatchJob"',
        docker_url='unix://var/run/docker.sock',
        network_mode='ecommerce_ecommerce-network',  # 讓新容器能夠存取 Redis 和 MinIO
        environment={
            'MINIO_ENDPOINT': 'http://ecommerce-minio:9000',
            'REDIS_HOST': 'ecommerce-redis',
            # 使用 MAVEN_OPTS 確保 Maven 套用 Java 21 需要的模組解鎖參數
            'MAVEN_OPTS': '--add-opens=java.base/sun.nio.ch=ALL-UNNAMED --add-opens=java.base/java.nio=ALL-UNNAMED --add-opens=java.base/java.util=ALL-UNNAMED --add-opens=java.base/java.lang=ALL-UNNAMED --add-opens=java.base/java.lang.invoke=ALL-UNNAMED'
        },
        mounts=[
            Mount(
                source='D:/桌面/MonoRepository/e-commerce-platform',
                target='/workspace',
                type='bind'
            )
            # 💡 建議：若未來執行太慢，可取消下方註解，把本機的 .m2 資料夾掛進去加速 Maven 載入
            # , Mount(source='C:/Users/nick5/.m2', target='/root/.m2', type='bind')
        ],
        working_dir='/workspace/spark-recommender',
        mount_tmp_dir=False
    )

    run_spark_batch
