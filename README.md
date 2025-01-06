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

jenkins-cli groovy = < scripts/creat
jenkins-cli delete-credentials system::system::jenkins ssh-host-id
jenkins-cli groovy = < scripts/create-pipelines

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


1. persist-credentials: false
このオプションは、チェックアウト時の認証情報の保持を制御します。

デフォルト値: true

認証情報（GITHUB_TOKEN）が保持され、以降のGit操作（プッシュやフェッチなど）で自動的に使用されます。
false に設定した場合:

チェックアウト後に、認証情報が削除されるため、以降のGit操作で認証エラーが発生する可能性があります。
この場合、後続のGit操作で手動で認証情報を設定する必要があります。
結論: この場合の影響
persist-credentials: false を指定すると、git push や git fetch を実行する際にエラーが発生する可能性があります。通常、persist-credentials: true（デフォルト）にしておいたほうが簡単で安全です。


はい、uses: actions/checkout@v3 の withオプションはGitHub Actionsの挙動に大きく影響します。以下、それぞれのオプションについて説明します：

1. persist-credentials: false
このオプションは、チェックアウト時の認証情報の保持を制御します。

デフォルト値: true

認証情報（GITHUB_TOKEN）が保持され、以降のGit操作（プッシュやフェッチなど）で自動的に使用されます。
false に設定した場合:

チェックアウト後に、認証情報が削除されるため、以降のGit操作で認証エラーが発生する可能性があります。
この場合、後続のGit操作で手動で認証情報を設定する必要があります。
結論: この場合の影響
persist-credentials: false を指定すると、git push や git fetch を実行する際にエラーが発生する可能性があります。通常、persist-credentials: true（デフォルト）にしておいたほうが簡単で安全です。

解決策（もし認証情報を削除したい場合）
手動で認証情報を設定します。たとえば：

yaml
Copy code
- name: Configure Git authentication
  run: |
    git config user.name "github-actions[bot]"
    git config user.email "github-actions[bot]@users.noreply.github.com"
または、GITHUB_TOKEN を明示的に使用します：

yaml
Copy code
- name: Set up Git authentication
  env:
    GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
  run: |
    git remote set-url origin https://x-access-token:${GITHUB_TOKEN}@github.com/${{ github.repository }}
2. fetch-depth: 0
このオプションは、リポジトリをクローンする際に取得する履歴の深さを制御します。

デフォルト値: 1

最新の1コミットだけを取得します（浅いクローン）。
0 に設定した場合:

リポジトリ全体の履歴（すべてのコミット）が取得されます。
結論: この場合の影響
fetch-depth: 0 の利点:

全履歴が必要な操作（例: 古いブランチの操作や、git log の使用）に対応できます。
git branch -a や git ls-remote のようなコマンドが正確に動作します。
fetch-depth: 1（デフォルト）の場合の制約:

浅いクローンだと、一部のGit操作（リモートブランチのチェックやフルログの取得）が制限される可能性があります。
リモートの artifacts ブランチが正しく認識されない場合があります。
推奨設定
fetch-depth: 0 を使用することで、リモートブランチ操作（git fetch, git checkout）やリポジトリ全体を扱う操作が確実に動作します。今回のようにブランチの確認や作成を行う場合には、fetch-depth: 0 を使うべきです。


          # すべてのリモートブランチをフェッチ
          git fetch origin --prune

          # artifacts ブランチがリモートに存在するか確認
          if git ls-remote --exit-code --heads origin artifacts; then
            echo "Branch 'artifacts' exists. Checking it out..."
            git checkout artifacts
          else
            echo "Branch 'artifacts' does not exist. Creating it..."
            git checkout --orphan artifacts
            git rm -rf --cached .
          fi


git config --local user.email