#!/bin/bash
SECRET_JSON_FILE="./.secrets.json"
SECRET_NAME="SSH_CREDENTIALS"

# ファイルが存在するか確認
if [ ! -f "$SECRET_JSON_FILE" ]; then
    echo "Error: $SECRET_JSON_FILE does not exist."
    exit 1
fi

echo "$SECRET_JSON_FILE found. Checking if the secret already exists..."

# シークレットが存在するか確認
if gcloud secrets describe "$SECRET_NAME" >/dev/null 2>&1; then
    echo "Secret $SECRET_NAME already exists. Adding a new version..."
else
    echo "Secret $SECRET_NAME does not exist. Creating the secret..."
    gcloud secrets create "$SECRET_NAME" \
        --replication-policy="automatic"
fi

# JSONデータの値を登録する
echo "Adding a new version to the secret $SECRET_NAME..."
gcloud secrets versions add "$SECRET_NAME" --data-file="$SECRET_JSON_FILE"

echo "Secret $SECRET_NAME updated successfully."
