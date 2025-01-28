## Cloud SQLについて

### 料金表

| スペック | 月20時間稼働 | 常時起動（744時間/月） |
|----------|--------------|------------------------|
| db-f1-micro (1 vCPU & 0.614 GB RAM) | 約 $0.31 | 約 $11.61 |
| 10 GB ストレージ | 約 $2.00 | 約 $2.00 |
| 合計 | 約 $2.31 | 約 $13.61 |



## 現在のCloud SQLを取得する

プロジェクト内にあるすべてのCloud SQLインスタンスを一覧表示します。

```sh
gcloud sql instances list
```

* ステータス(STATUS)一部
  * PENDING_CREATE: インスタンス作成中。プロビジョニングや初期設定が進行中
  * RUNNABLE: 正常稼働中。リクエスト受付可能
  * STOPPING:停止中。リクエストを受け付けていない
  * STOPPED: 停止済み。必要に応じて再起動が可能
  * MAINTENANCE: 定期メンテナンス中 利用が一時的に制限される場合がある
  * SUSPENDED: 利用停止状態 課金問題やポリシー違反などでアクセスが制限されている
  

プロジェクトを指定する

```sh
gcloud sql instances list --project=[PROJECT_ID]
```


特定のインスタンスの情報を取得

```sh
gcloud sql instances describe [INSTANCE_NAME]
```

## Cloud SQLインスタンスを生成する

```
gcloud sql instances create [INSTANCE_NAME] \
    --database-version=[DATABASE_VERSION] \
    --region=[REGION] \
    --tier=[TIER]
```

* `--database-version`: 対応するデータベース (MySQL, PostgreSQL, SQL Server) のバージョンを指定。
  * デフォルトではMySQLになります
  * 設定例(MYSQL_8_0,POSTGRES_15,SQLSERVER_2019_STANDARD)
* `--region`: インスタンスを配置するリージョンを指定。
* `--tier`: CPU、メモリを指定 (例: db-f1-micro, db-n1-standard-1)
* `--availability-type`:
  * ZONAL
    * インスタンスが1つのゾーン(コストは安い)
    * 開発やテスト環境、小規模な非ミッションクリティカルなプロジェクト
    * ゾーン障害が発生した場合、インスタンスが停止し、手動復旧が必要
  * REGIONAL
    * 複数のゾーン(同一リージョン内)にレプリカを作成します
    * 高可用性(HA: High Availability)構成で、ゾーン障害時でも別のゾーンに自動フェイルオーバーします
    * コストは高めですがプロダクション環境向け

### サンプル*

#### よくある設計パターン

(本番環境)

```sh
gcloud sql instances create [インスタンス名(prod-mysql-instance)] \
--database-version=MYSQL_8_0 \
--tier=db-custom-1-3840（1 vCPU, 3.75 GB RAM）以上を選択
--region=asia-northeast1 \
--availability-type=REGIONAL \
--storage-size=100 \
--storage-type=SSD \
--enable-backup \
--auto-storage-increase
```

* CPUの指定についてその他サンプル
  * `--tier=db-custom-2-7680 \  # 2 vCPU, 7.5GB RAM`


(開発環境)

```sh
gcloud sql instances create [インスタンス名(dev-postgres-instance)] \
--database-version=POSTGRES_13 \
--tier=db-f1-micro \
--region=asia-northeast1 \
--storage-size=10GB \
--availability-type=ZONAL
```


#### 低コストのインスタンス

```sh
gcloud sql instances create my-sql-instance \
--tier=db-f1-micro \
--region=us-central1 \
--storage-type=HDD \
--storage-size=10 \
--no-backup \
--availability-type=ZONAL \
--database-version=MYSQL_8_0
```

* MySQLまたはPostgreSQLを選ぶと、SQL Serverよりもコストが低くなります
* db-f1-micro: 最安プラン。負荷が高い場合は性能不足の可能性あり。
  * db-g1-small: f1-microより性能は良いが少し高め
* リージョンによって料金が異なるため、最も安いリージョン（例: us-central1）
* ストレージ設定
  * ストレージ容量: 最小の10GB
  * 標準HDDを選択(SSDより安い)
* シングルインスタンス構成
  * --availability-type=ZONALを選択
* バックアップを無効にする

## Cloud SQLインスタンスを制御する

### インスタンスの開始

```sh
gcloud sql instances patch [INSTANCE_NAME] --activation-policy ALWAYS
```

#### インスタンスを必要に応じてオン・オフ

インスタンスはアクセスがあるときに自動的に起動し、アイドル状態が続くと停止します。

```sh
gcloud sql instances patch [INSTANCE_NAME] --activation-policy ON_DEMAND
```

### インスタンスの停止

```sh
gcloud sql instances patch [INSTANCE_NAME] --activation-policy NEVER
```


#### インスタンスの情報の確認

```sh
gcloud sql instances describe [INSTANCE_NAME]
```

ステータスのみ確認する


```sh
gcloud sql instances describe [INSTANCE_NAME] --format="value(state)"
```



## Cloud SQLを運用する方法

### ユーザーを作成する 

管理者ユーザーを作成する場合

```sh
gcloud sql users set-password [ADMIN_USER(root)] \
    --instance=[インスタンス名] \
    --password=[PASSWORD]
```
* MySQLなら`root`
* Postgresqlなら`postgres`


DBユーザーを作成する場合

```sh
gcloud sql users create [DB_USER(db_user)] \
    --instance=[インスタンス名] \
    --password=[PASSWORD]
```

リモート用のホストを設定する場合

```sh
gcloud sql users create [USERNAME] \
    --host=% \
    --instance=[インスタンス名] \
    --password=[PASSWORD]
```

データベース内での権限の設定は必要です


* `--host`:
  * デフォルトではlocalhost(127.0.0.1)として設定される


ユーザ一覧を確認する

```sh
gcloud sql users list --instance=[インスタンス名]
```


### DBを制御する

#### データベース一覧の確認

```sh
gcloud sql databases list --instance=[インスタンス名]
```

#### データベースを作成する場合

```sh
gcloud sql databases create [DB_NAME] \
    --instance=[インスタンス名] 
```

```
gcloud sql databases create devel \
    --instance=dev-mysql-instance \
    --charset=utf8mb4 \
    --collation=utf8mb4_general_ci
```
* `--charset`: データベースの文字セットを指定します
  * 例: utf8mb4, latin1 など
* `--collation`: データベースの照合順序を指定します
  * 例: utf8mb4_general_ci, utf8mb4_unicode_ci




#### データベースを削除する場合

```sh
gcloud sql databases delete [DATABASE_NAME] \
 --instance=[INSTANCE_NAME]
```

#### データベースに接続する

**gcloudで接続する方法**

```sh
gcloud sql connect [インスタンス名] --user=[ユーザー名]
# DBを指定する
gcloud sql connect [インスタンス名] --user=[ユーザー名]  --database=[DATABASE名]
```


**Cloud SQL Auth Proxyを使用して接続**

https://cloud.google.com/sql/docs/mysql/connect-auth-proxy?hl=ja

1. Cloud SQL Auth Proxyをインストールする(公式サイトからインストール可能)
2. Cloud SQL Auth Proxyを起動
   * `./cloud-sql-proxy [INSTANCE_CONNECTION_NAME]`
   * サンプル: `./cloud-sql-proxy my-project:us-central1:my-instance`
3. ローカルクライアントから接続
   * MySQL: `mysql -u [USER] -p -h 127.0.0.1`
   * PostgreSQL: `psql -U [USER] -h 127.0.0.1`




#### DDLを適用する方法

**DDL実行方法**

前提条件:
  * DBに関するアクセス権が存在すること

方法1: gcloudコマンド

```sh
gcloud sql connect [インスタンス名] --user=[ユーザー名] --database=[DATABASE名]
# 接続後にDDLを適用する方法
# MySQL
source schema.sql
# PostgreSQL
\i schema.sql
```
パスワード入力を自動化したい場合は`gcloud sql connect`はサポートがなく、
データベースクライアント(psqlやmysql)を直接使用する方が適しています
`gcloud sql connect`はインタラクティブな使用を前提としており、CI/CD の自動化には向いてい


方法2: Cloud SQL Auth Proxyによる接続

```sh
curl -o cloud-sql-proxy https://dl.google.com/cloudsql/cloud-sql-proxy.linux.amd64
chmod +x cloud-sql-proxy
./cloud-sql-proxy [INSTANCE_CONNECTION_NAME]
# MYSQL
export MYSQL_PWD="your_password"
mysql -u [USER] -p -h 127.0.0.1 [DATABASE_NAME] < schema.sql
# PostgreSQL
export PGPASSWORD="your_password"
psql -U [USER] -h 127.0.0.1 -d [DATABASE_NAME] -f schema.sql
```

**適用後の確認補法**

```sh
# MYSQL
SHOW TABLES;
SHOW CREATE TABLE [TABLE_NAME];
# PostgreSQL
\dt
\d [TABLE_NAME]
```

#### データベースの権限の設定

データベースユーザーに特定のデータベースへの権限を付与するには、
SQLクエリを使用して手動で行う必要があります。

**PostgreSQLの例**

```sh
GRANT CONNECT ON DATABASE my_database TO my_user;
GRANT USAGE ON SCHEMA public TO my_user;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO my_user;
```


**MYSQLの例**

```sh
GRANT ALL PRIVILEGES ON my_database.* TO 'my_user'@'%';
FLUSH PRIVILEGES;
```

#### データベース生成とユーザー設定の自動化スクリプト例

```sh
#!/bin/bash

INSTANCE_NAME="my-instance"
DB_NAME="my_database"
USERNAME="my_user"
PASSWORD="my_secure_password"

# データベース作成
gcloud sql databases create $DB_NAME --instance=$INSTANCE_NAME

# ユーザー作成
gcloud sql users create $USERNAME \
    --host=% \
    --instance=$INSTANCE_NAME \
    --password=$PASSWORD

# Cloud SQL Auth Proxy で接続し、権限を付与 (PostgreSQLの例)
psql -h 127.0.0.1 -U postgres <<EOF
GRANT ALL PRIVILEGES ON DATABASE $DB_NAME TO $USERNAME;
EOF
```

### モニタニング・ログ

インスタンスの状態を確認する

```sh
gcloud sql instances describe [INSTANCE_NAME]
```
操作ログの確認

```sh
gcloud sql operations list --instance=[INSTANCE_NAME]
```

## 接続・認証に関する設定

用途に合わせて以下の接続方法が想定されます

* Cloud SQL Auth Proxy を使用(推奨)
* gcloudコマンドによる接続(インタラクティブの場合
* パブリックIPのFWを許可して接続する
* 同じプロジェクトのGCP同士ならプライベートIPを利用する

### PRIMARY_ADDRESS(パブリックIPアドレス)

* インスタンスの外部接続用のアドレス
* Cloud SQLインスタンスをインターネット経由で接続するために使用されます
* gcloud sql connect や直接の MySQL/PostgreSQL クライアント接続で使用します
* 許可するネットワーク範囲などを設定できます

### PRIVATE_ADDRESS(プライベートIP)

* プライベートネットワーク内でのみ使用されるIPアドレス
* インターネットからは直接アクセスできません
* Google Cloud VPC内のリソースにのみ使用
* 内部ネットワーク内のサービス間での接続に使用します
  * 例えば、Cloud RunやGKEからCloud SQLに安全に接続したい場合に適しています。

  
### 許可するネットワーク範囲を設定する(パブリックIP)

以下はすべてのネットワークから接続を可能にしていますが
セキュリティ観点では非推奨です

![clip-20240811202242.png](/knowledge/open.file/download?fileNo=97)

### プライベートIPの設定

Cloud Runや他のサービスがCloud SQLに接続する際には適切な権限を持つサービスアカウントを使用します
このサービスアカウントには、Cloud SQLインスタンスへの接続権限が必要です

**手順**

* プライベートIPの設定
  * Cloud SQLインスタンスでプライベートIPを有効にします。
* サービスアカウントの設定
  * Cloud Runや他のサービスがCloud SQLに接続する際には適切な権限を持つサービスアカウントを使用します
* サービスからプライベートIP経由で接続
* インターネット経由の通信ではなく内部ネットワーク通信となるため、外部IPの通信料金が発生しません。

### 接続文字列による接続

接続文字列で指定するホスト名はCloud SQLの内部アドレス（Unixソケットパス）を使用します
プライベートIP設定が有効でなければこのUnixソケットパスを使用してCloud SQLに接続することはできません。

---

### GitHub ActionsでCloud SQLに接続する方法

Cloud SQLに接続するため、GitHub ActionsワークフローでCloud SQL Auth Proxyを使用する

1. 事前の準備
   1. **GCPの設定**
      1. サービスアカウントを作成
         1. `Cloud SQL Client`または`Cloud SQL Admin`の権限を作成
      2. `Workflow Identityの設定`または`サービスアカウントのキー`のいずれかを準備する
   2. GitHub Secretsの設定
      1. GCP_SA_KEY: サービスアカウントのJSONキーの内容（Workflow Identityの設定の場合は環境にあわせて設定する）
      2. DB_PASSWORD: データベースのユーザーパスワード
      3. DB_USER: データベースユーザー名（例: root）
      4. DB_NAME: データベース名
      5. INSTANCE_CONNECTION_NAME: Cloud SQLの接続名（例:project-id:region:instance-name）


**サンプル:サービスアカウントでの設定方法**

```yaml
name: Apply DDL to Cloud SQL

on:
  push:
    branches:
      - main

jobs:
  apply-schema:
    runs-on: ubuntu-latest

    steps:
      # 1. リポジトリをチェックアウト
      - name: Checkout code
        uses: actions/checkout@v3

      # 2. Google Cloud CLI をセットアップ
      - name: Set up Google Cloud CLI
        uses: google-github-actions/setup-gcloud@v1
        with:
          service_account_key: ${{ secrets.GCP_SA_KEY }}
          project_id: your-gcp-project-id

      # 3. Cloud SQL Auth Proxy をセットアップ
      - name: Start Cloud SQL Auth Proxy
        run: |
          curl -o cloud-sql-proxy https://dl.google.com/cloudsql/cloud-sql-proxy.linux.amd64
          chmod +x cloud-sql-proxy
          ./cloud-sql-proxy ${{ secrets.INSTANCE_CONNECTION_NAME }} &
        env:
          GOOGLE_APPLICATION_CREDENTIALS: /github/workspace/gcp-key.json

      # 4. SQL スクリプトを MySQL/PostgreSQL に適用
      - name: Apply schema to MySQL
        run: |
          export MYSQL_PWD=${{ secrets.DB_PASSWORD }}
          mysql -u ${{ secrets.DB_USER }} -h 127.0.0.1 -P 3306 ${{ secrets.DB_NAME }} < schema.sql

     # 5. DDL 適用結果を確認
      - name: Verify DDL Application (MySQL)
        run: |
          export MYSQL_PWD=${{ secrets.DB_PASSWORD }}
          mysql -u ${{ secrets.DB_USER }} -h 127.0.0.1 -P 3306 -e "SHOW TABLES;" ${{ secrets.DB_NAME }}
```


**サンプル: Workflow Identityでの設定方法**

```yaml
name: Apply DDL to Cloud SQL with OIDC

on:
  push:
    branches:
      - main

jobs:
  apply-schema:
    runs-on: ubuntu-latest

    permissions:
      contents: read
      id-token: write # OIDC トークンの要求

    steps:
      # 1. リポジトリをチェックアウト
      - name: Checkout code
        uses: actions/checkout@v2

      # 2. Google Cloud CLI をセットアップ
      - name: Authenticate to Google Cloud
        uses: google-github-actions/auth@v0.4.0
        with:
          workload_identity_provider: ${{ secrets.GCP_WORKLOAD_IDENTITY_PROVIDER }}
          service_account: ${{ secrets.GCP_SERVICE_ACCOUNT }}
          create_credentials_file: true


      # 3. Cloud SQL Auth Proxy をセットアップ
      # 以降はサービスアカウントを同様です
```

フォルダ毎にSQLを実行する場合

```sh
for file in /path/to/sql/files/*.sql; do
  echo "Executing $file..."
  mysql -u $DB_USER -p$DB_PASS -h $DB_HOST $DB_NAME < "$file"
done
```