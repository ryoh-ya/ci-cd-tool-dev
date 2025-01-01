#!/bin/bash

# .secretファイルのパス
SECRET_FILE="./.secrets"

# JSONファイルの出力先
OUTPUT_FILE="./.secrets.json"

# ファイルが存在するかチェック
if [ ! -f "$SECRET_FILE" ]; then
  echo "Error: $SECRET_FILE does not exist."
  exit 1
fi

# JSON構造の準備
echo "{" > $OUTPUT_FILE

# .secretファイルを1行ずつ読み込む
while IFS='=' read -r key value; do
  # 空行やコメント行をスキップ
  if [ -z "$key" ] || [[ "$key" == \#* ]]; then
    continue
  fi

  # キーをクリーンアップ
  key=$(echo "$key" | xargs)

  # 値をクリーンアップし、囲まれたダブルクォートを削除
  value=$(echo "$value" | sed 's/^"//' | sed 's/"$//')

  # 改行コードをエスケープ（\nをJSON用に\\nに変換）
  value=$(echo "$value" | sed 's/\\n/\\\\n/g')

  # ダブルクォートをエスケープ（JSONでのエスケープ）
  value=$(echo "$value" | sed 's/"/\\"/g')

  # JSONの形式で書き込み
  echo "  \"$key\": \"$value\"," >> $OUTPUT_FILE
done < "$SECRET_FILE"

# 最後のカンマを削除してJSONを閉じる
sed -i '$ s/,$//' $OUTPUT_FILE
echo "}" >> $OUTPUT_FILE

echo "JSON file created at $OUTPUT_FILE"
