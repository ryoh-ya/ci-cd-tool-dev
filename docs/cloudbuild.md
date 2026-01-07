## Goole Cloud Buildの活用方法

https://appj.pglikers.com/knowledge/open.knowledge/view/447

- [Goole Cloud Buildの活用方法](#goole-cloud-buildの活用方法)
  - [システム構成について](#システム構成について)
  - [ワークフローを作成する](#ワークフローを作成する)
  - [ワークフローを実行する](#ワークフローを実行する)
  - [環境変数・Secret Managerの管理](#環境変数secret-managerの管理)
  - [Python/shellスクリプトを実行する](#pythonshellスクリプトを実行する)
  - [DockerイメージをビルドしてContainer Registryにプッシュする](#dockerイメージをビルドしてcontainer-registryにプッシュする)
  - [ローカルで実行する](#ローカルで実行する)
  - [環境変数・Secret Managerの管理](#環境変数secret-managerの管理-1)
  - [メール、Slack等に通知する方法](#メールslack等に通知する方法)


CI/CDの実行ファイル

```
touch cloudbuild.yaml
```

* Google Cloud Buildには無料枠（120分/月）があります。
* 無料枠を超えた場合、使用したビルド時間に応じて課金されます（$0.003/秒）

主な用途について

* コンテナイメージのビルドとデプロイ
  * Google Container Registry (GCR) や Artifact Registry にビルドしたイメージをプッシュ
  * Kubernetes クラスタ（GKE など）へのデプロイを自動化
* アプリケーションビルドと成果物の保存
  * アプリケーションのビルドプロセスを自動化（Node.js, Python, Go, Java など）。
  * ビルド成果物（バイナリやアーティファクト）を Google Cloud Storage に保存
* プルリクエストやコード変更時にユニットテスト、統合テストを自動実行
**他のCI/CDツールとの強み**

* Google Cloudとの統合性
* サーバーレスのため、自身でビルド環境をホストする必要がなく、スケーラビリティも高い
* カスタムビルドステップ（Docker イメージとしての独自ツール）を作成して利用できる
(不向きなもの)
* GCEを使わないSSHによる接続の場合は向いていない。

### システム構成について

**構成パターン1**

* Webアプリ: 
  * フロントエンドまたはバックエンドからREST APIリクエストを送信
* Google Cloud Functions
  * Webhookを受け取り、Cloud Build APIを呼び出すロジックを実装（オプション）。
* Cloud Build: ビルドプロセスを実行。

---

### ワークフローを作成する

https://appj.pglikers.com/knowledge/open.knowledge/view/450

* ローカル版ではscriptセクションが使えない

### ワークフローを実行する

```sh
gcloud builds submit --config=cloudbuild.yaml .
```

リモートリポジトリから直接ビルドを実行 
GitHubやGitLabなどのリポジトリを指定してCloud Buildを実行する

```sh
gcloud builds submit https://source.developers.google.com/projects/PROJECT_ID/repos/REPO_NAME/moveable-aliases/BRANCH_NAME --config=cloudbuild.yaml
```

トリガーを登録する

```sh
gcloud beta builds triggers create github \
    --name="trigger-name" \
    --repo-name="repository-name" \
    --repo-owner="repository-owner" \
    --branch-pattern="^main$" \
    --build-config="cloudbuild.yaml"
```

トリガーの確認

```sh
gcloud beta builds triggers list
```

トリガーを手動で実行する

```sh
gcloud beta builds triggers run TRIGGER_ID \
    --branch=BRANCH_NAME
```

ジョブ一覧を確認する

```sh
gcloud builds list
```

特定のジョブの詳細を確認

```sh
gcloud builds describe BUILD_ID
```

リアルタイムログを確認する

```sh
gcloud builds log BUILD_ID
```
### 環境変数・Secret Managerの管理

* ローカル版ではSecret Manager、環境変数が上手く動作しない
  * すべてがサブスティチューションと認識される(env、secretgが使えない)
  * --substitutions で明示的に設定されていないとエラーになる


### Python/shellスクリプトを実行する 

https://appj.pglikers.com/knowledge/open.knowledge/view/449


### DockerイメージをビルドしてContainer Registryにプッシュする

https://appj.pglikers.com/knowledge/open.knowledge/view/448

### ローカルで実行する

https://appj.pglikers.com/knowledge/open.knowledge/view/446?offset=0

cloud-build-local を使ってローカルでビルドを実行する場合、
scriptフィールドはサポートされていない可能性が高いです


### 環境変数・Secret Managerの管理

https://appj.pglikers.com/knowledge/open.knowledge/view/461

* ローカル版ではSecret Manager、環境変数が上手く動作しない
  * すべてがサブスティチューションと認識される
  * --substitutions で明示的に設定されていないとエラーになる

secretマネージャの制御方法
  * https://appj.pglikers.com/knowledge/open.knowledge/view/460

### メール、Slack等に通知する方法

https://appj.pglikers.com/knowledge/open.knowledge/view/466

