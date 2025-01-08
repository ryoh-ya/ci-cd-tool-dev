
## Bucketの作成方法

```sh
gcloud storage buckets create gs://[BUCKET_NAME] --location=[REGION]
# 例
gcloud storage buckets create gs://my-private-site --location=asia-northeast1
```

## アクセス権について

### 非公開

デフォルトではバケットは非公開ですが、
公開アクセスをさらに明示的にブロックすることができます

```sh
gcloud storage buckets update gs://[BUCKET_NAME] --no-public-access
```

### 認証済みユーザーグループにアクセス権を付与

プロジェクト内の認証済みユーザー全員にアクセスを許可するには、
roles/storage.objectViewer または roles/storage.objectAdmin を付与します。

* projectViewer:[PROJECT_ID]：プロジェクト内のビューアーロールを持つ認証済みユーザー
* roles/storage.objectViewer：バケット内のオブジェクトを閲覧可能にするロール

```sh
gcloud storage buckets add-iam-policy-binding gs://[BUCKET_NAME] \
  --member="projectViewer:[PROJECT_ID]" \
  --role="roles/storage.objectViewer"
```

### 必要に応じて管理者権限を設定

バケットやオブジェクトを管理（アップロードや削除など）する権限を
プロジェクト内のユーザーに与えたい場合は、以下のように roles/storage.objectAdmin を追加します。

```sh
gcloud storage buckets add-iam-policy-binding gs://[BUCKET_NAME] \
  --member="projectEditor:[PROJECT_ID]" \
  --role="roles/storage.objectAdmin"
```

## ファイルをアップロードするコマンド

cpコマンド

```sh
gcloud storage cp [LOCAL_FILE_PATH] gs://[BUCKET_NAME]/```
# 例
gcloud storage cp index.html gs://my-private-site/
```

### 差分をアップロードする

`gcloud storage rsync`はrsyncと同様に、
差分同期が可能です。ローカルと GCS バケット間で変更されたファイルのみを転送します。

```sh
gcloud storage rsync -r ./updated-files gs://my-private-site
```
