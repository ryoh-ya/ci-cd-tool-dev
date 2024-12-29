## CI/CD 開発

- [CI/CD 開発](#cicd-開発)
- [Git Actions](#git-actions)
  - [How To Use](#how-to-use)
    - [ローカルで実行する方法](#ローカルで実行する方法)
  - [実行時のactイメージについての選択](#実行時のactイメージについての選択)
- [Jenkins](#jenkins)
  - [How To Use](#how-to-use-1)
    - [ローカルで実行する方法](#ローカルで実行する方法-1)
- [Google Cloud Build](#google-cloud-build)
  - [How To Use](#how-to-use-2)
    - [ローカルで実行する方法](#ローカルで実行する方法-2)


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
```

pushイベントを実行したい場合

```sh
act push
```

デバッグ情報を含める場合
```sh
act v
```

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
docker-compose -f docker-compose.jenkins.yaml up
```


```sh
JENKINSFILE_PATH=$(pwd)/jenkins/pipelines/example2/Jenkinsfile docker-compose -f docker-compose -f docker-compose.jenkins.yaml up 
```

windowsの場合

```bat
set JENKINSFILE_PATH=%cd%\jenkins\pipelines\example2\Jenkinsfile
docker-compose -f docker-compose.jenkins.yaml up 
```


**コンテナを終了する**
```sh
docker-compose -f docker-compose.jenkins.yaml down
```

```sh
docker-compose -f docker-compose.jenkins.yaml run --rm --entrypoint sh jenkinsfile-runner
```


---

## Google Cloud Build

### How To Use

#### ローカルで実行する方法

**コンテナをビルドする**
```sh
docker-compose -f docker-compose.gcloud.yaml build
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


**ローカルでcloud buildをテスト**
```sh
cloud-build-local --config=cloudbuild.yaml --dryrun=false .
```


-dryrun オプションの意味
デフォルト: -dryrun=true
Cloud Build の設定ファイル（cloudbuild.yaml）の構文チェック（Linting）と、
コマンドの実行準備を行いますが、実際にはコマンドを実行しません。

-dryrun=false: コマンドを実際に実行します。

ビルドやコマンドを実際に実行する前に、
cloudbuild.yaml ファイルの構文や手順に問題がないか確認するために使用します。
テスト実行のような使い方ができます。


```log
BUILD
: Pulling image: alpine
: Using default tag: latest
2024/12/28 11:36:57 Error updating token in metadata server: Post "http://localhost:8082/token": dial tcp 127.0.0.1:8082: connect: connection refused
: latest: Pulling from library/alpine
: 38a8310d387e: Already exists
: Digest: sha256:21dc6063fd678b478f57c0e13f47560d0ea4eeba26dfc947b2a4f81f686b9f45
: Status: Downloaded newer image for alpine:latest
: docker.io/library/alpine:latest
: Hello, Cloud Build!
2024/12/28 11:37:10 Step  finished
2024/12/28 11:37:10 status changed to "DONE"
DONE
```

```log
Error updating token in metadata server: Post "http://localhost:8082/token": dial tcp 127.0.0.1:8082: connect: connection refused
```
cloud-build-local がローカル環境で実行される際、Google Cloud Build のような環境をシミュレートするために使用される「偽のメタデータサーバー」との通信が失敗したことを示しています。

**(内容)**
ローカル環境で Google Cloud Build のメタデータエミュレーションが完全に動作しない場合に発生する軽微な問題で、ビルド自体の成功や結果には影響を与えません。

Google Container Registry (GCR) や Artifact Registry にアクセスする必要がある場合は
gcloud の認証トークンが必要ですが、このエラーが発生するとトークンが正しく取得できない可能性があります。

**(対応策)**

方法①:コンテナ内で以下を実行して、明示的に認証情報を設定します

```sh
gcloud auth login
gcloud auth application-default login
```

方法 2: --experimental_local オプションの利用

cloud-build-local の一部の機能を無効化することで、
メタデータサーバーに依存しない構成にできます。
```sh
cloud-build-local --config=cloudbuild.yaml --dryrun=false --experimental_local .
```
