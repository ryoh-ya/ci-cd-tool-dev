# CI/CD 開発用リポジトリ

- [CI/CD 開発用リポジトリ](#cicd-開発用リポジトリ)
  - [Git Actions](#git-actions)
    - [How To Use](#how-to-use)
      - [ローカルで実行する方法](#ローカルで実行する方法)
    - [実行時のactイメージについての選択](#実行時のactイメージについての選択)
  - [Jenkins](#jenkins)
    - [How To Use](#how-to-use-1)
      - [ローカルで実行する方法](#ローカルで実行する方法-1)
    - [コンテナ後の環境設定](#コンテナ後の環境設定)
    - [環境変数の設定を行う](#環境変数の設定を行う)
    - [CLIコマンド](#cliコマンド)
  - [Google Cloud Build](#google-cloud-build)
    - [How To Use](#how-to-use-2)
      - [ローカルで実行する方法](#ローカルで実行する方法-2)
        - [セットアップ](#セットアップ)


## Git Actions

### How To Use

#### ローカルで実行する方法

**コンテナをビルドする**
```sh
docker-compose -f docker-compose.act.yaml build
```

**コンテナを起動する**
```sh
docker-compose -f docker-compose.act.yaml up -d
```

**コンテナの中に入る**
```sh
docker exec -it act-container /bin/bash
```

**actでワークフローを実行**
```sh
act
# ymlを指定する方法
act -j python-debug
```

pushイベントを実行したい場合

```sh
act push
```

デバッグ情報を含める場合
```sh
act v
```

act -j ssh-debug --secret-file .secrets


### 実行時のactイメージについての選択

* **Large (約17GB)**
  * GitHub Hosted Runners（GitHubの公式ランナー）のスナップショットに近い環境を提供。
  * ほぼすべてのGitHub Actionsが動作する。
  * 多くのツールや依存関係が含まれている。
  * すべてのワークフローや複雑なアクション（例: .NET、Java、Pythonなど多数の言語やツールを必要とするアクション）をテストしたい場合
  * 本番環境に最も近い状態をローカルで再現したい場合
  * (デメリット)
    * イメージサイズが非常に大きい（約17GBのダウンロード、53.1GBのストレージ）
    * ダウンロードに時間がかかる。
* **Medium (約500MB)**
  * 必要最低限のツールを含む軽量イメージ。
  * 大半のGitHub Actionsが動作可能。
  * サイズが小さいため、ダウンロードが速い。
  * 標準的なワークフローやアクション（例: Node.js、Python、Shellなど）のテスト。
  * 開発の初期段階や軽量なCI/CDパイプラインのテスト。
  * (デメリット)
    * 一部の特殊なアクション（特定の依存関係を必要とするもの）が動作しない可能性がある。
* **Micro (<200MB)**
  * Node.jsベースの極限まで軽量化されたイメージ。
  * Node.jsだけを必要とするワークフローで動作。
  * JavaScript/Node.jsに限定した軽量なアクションをテスト。
  * シンプルなワークフロー（例えば、単純なLintチェックやフォーマット）を実行。
  * (デメリット)
    * Node.js以外の言語やツールを使用するアクションは動作しない。
    * 機能が非常に制限されている。

## Jenkins

### How To Use

#### ローカルで実行する方法

**コンテナをビルドする**
```sh
docker-compose -f docker-compose.jenkins.yaml build
```

**コンテナを起動する**
```sh
docker-compose -f docker-compose.jenkins.yaml up -d
```

**コンテナの中にはいる**
```sh
docker-compose -f docker-compose.jenkins.yaml exec -it jenkins-dev bash
```


### コンテナ後の環境設定

**APIトークンを取得する**

```sh
java -jar /var/jenkins_home/jenkins-cli.jar -s http://localhost:8080/ \
-auth admin:admin123 \
```

### 環境変数の設定を行う

```sh
source .env.setup 
```

上記を実行することにより
`java -jar /var/jenkins_home/jenkins-cli.jar -s $JENKINS_URL -auth $JENKINS_AUTH`のエリアスが設定されて`jenkins-cli`が使えるようになる


### CLIコマンド

**ヘルプを表示する**
```sh
jenkins-cli help
```

**ジョブ一覧を取得する**
```sh
jenkins-cli list-jobs
```

**ジョブの詳細情報を取得する**
```sh
jenkins-cli get-job <JOB_NAME>
# jenkins-cli get-job example-job
```

**ジョブをビルド(実行)する**
```sh
jenkins-cli build <JOB_NAME>
```

**ビルド結果を取得する**
* ジョブの最新のビルドログを表示します。
```sh
jenkins-cli console <JOB_NAME>
# or
jenkins-cli console <JOB_NAME> <ビルド番号>
```

**プラグインをインストールする**
```sh
jenkins-cli build install-plugin <プラグイン名>
```

**ジョブの作成方法**
(Jenkinsfileから実行/groovyスクリプトを自作)

```sh
jenkins-cli groovy = < scripts/create-pipeline.groovy example2-pipeline /var/jenkins_home/pipelines/Jenkinsfile
```

---

## Google Cloud Build

### How To Use

#### ローカルで実行する方法

**コンテナをビルドする**
```sh
docker-compose -f docker-compose.gcloud.yaml build
```

```sh
docker-compose -f docker-compose.gcloud.yaml up -d
```

```sh
docker-compose -f docker-compose.gcloud.yaml exec -it gcloud bash
```

**コンテナを起動して中に入る**
```sh
docker-compose -f docker-compose.gcloud.yaml run gcloud
```

**インストールが正しくできているか確認する**
```sh
cloud-build-local --help
```

**アカウントを設定する**

ホストマシンで認証を実行する ホストで認証情報を取得し、
それをコンテナ内で利用する方法。
```sh
gcloud auth login
```

もしホストで認証できない場合、コンテナ内で以下を実行して認証します。
```sh
gcloud auth login
# デフォルトの認証情報を設定
gcloud auth application-default login
```


プロジェクトを確認
```sh
gcloud projects list
```

プロジェクトを設定
```sh
gcloud config set project [PROJECT_ID]
```

##### セットアップ

.secrets.jsonを生成する

```sh
bash ./Maintenance/generate_secret_json.sh
```

.secrets.jsonを登録する

```sh
bash ./Maintenance/register_secret_manager.sh 
```

```sh
bash ./Maintenance/update_secret_manager.sh 
```

**ローカルでCloud Buildをテスト**

```sh
cloud-build-local --config=cloudbuild.yaml --dryrun=false .
# cloud-build-local --config=gcloud/debug.cloudbuild.yaml --dryrun=false .
```

* -dryrunオプション
  * デフォルト: -dryrun=true

Cloud Buildの設定ファイルの構文チェック（Linting）と、コマンドの実行準備を行いますが、実際にはコマンドを実行しない


**本番でCloud Buildを動かす**

```sh
gcloud builds submit --config=gcloud/notification.cloudbuild.yaml .
```
