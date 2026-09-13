from datetime import datetime, timedelta
from airflow import DAG
from airflow.operators.bash import BashOperator
from airflow.operators.empty import EmptyOperator

# 預設的 DAG 參數設定
default_args = {
    'owner': 'ecommerce_data_team',
    'depends_on_past': False,
    'email_on_failure': False,
    'email_on_retry': False,
    'retries': 1,
    'retry_delay': timedelta(minutes=5),
}

# 宣告一個 DAG (Directed Acyclic Graph)
with DAG(
    dag_id='ecommerce_spark_recommender_pipeline',
    default_args=default_args,
    description='展示用的電商推薦系統 Airflow 排程腳本 (Demonstration)',
    schedule_interval='@daily', # 每天執行一次
    start_date=datetime(2023, 1, 1),
    catchup=False, # 不回補過去未執行的排程
    tags=['ecommerce', 'spark', 'recommender'],
) as dag:

    # 1. 開始節點
    start = EmptyOperator(task_id='start_pipeline')

    # 2. 模擬觸發資料攝取作業 (Kafka to Iceberg)
    # 實務上這裡可以使用 SparkSubmitOperator，或是透過 BashOperator 呼叫外部的 spark-submit 腳本
    trigger_ingestion_job = BashOperator(
        task_id='trigger_kafka_to_iceberg_ingestion',
        bash_command='echo "Starting Spark Ingestion Job (Kafka -> Iceberg)..." && sleep 5 && echo "Ingestion completed successfully!"',
    )

    # 3. 模擬觸發 ALS 推薦模型訓練作業
    trigger_als_training = BashOperator(
        task_id='trigger_als_recommender_training',
        bash_command='echo "Starting Spark ALS Recommender Job..." && sleep 10 && echo "Model trained and saved to MinIO!"',
    )

    # 4. 結束節點
    end = EmptyOperator(task_id='end_pipeline')

    # 定義任務執行的先後順序 (相依性)
    start >> trigger_ingestion_job >> trigger_als_training >> end
