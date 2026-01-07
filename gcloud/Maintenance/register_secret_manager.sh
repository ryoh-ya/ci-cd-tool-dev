#!/bin/bash
SECRET_JSON_FILE="./.secrets.json"

# ファイルが存在するか確認してgcloudコマンドを実行
if [ -f "$SECRET_JSON_FILE" ]; then
    echo "$SECRET_JSON_FILE found. Proceeding to add the secret..."

    # JSONデータをシークレットに登録
    gcloud secrets create SSH_CREDENTIALS \
        --replication-policy="automatic"

    # JSONデータの値を登録する
    gcloud secrets versions add SSH_CREDENTIALS --data-file="$SECRET_JSON_FILE"

else
    echo "Error: $SECRET_JSON_FILE does not exist."
    exit 1
fi