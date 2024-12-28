## Git Actionの活用方法

https://appj.pglikers.com/knowledge/open.knowledge/view/44?offset=0

- [Git Actionの活用方法](#git-actionの活用方法)
  - [実際にワークフローをコーディングしてみる!!](#実際にワークフローをコーディングしてみる)
  - [イベントの基本について](#イベントの基本について)
  - [GitHub Actionsのジョブを実行する仮想環境について](#github-actionsのジョブを実行する仮想環境について)
  - [**1. `runs-on` に指定できるもの**](#1-runs-on-に指定できるもの)
    - [**GitHubホストランナー（プリセット環境）**](#githubホストランナープリセット環境)
    - [セルフホストランナー](#セルフホストランナー)
    - [特定の言語やバージョンを利用する場合、以下のように設定できます。](#特定の言語やバージョンを利用する場合以下のように設定できます)
  - [**2. `ubuntu-latest` で使えるプリインストールツール**](#2-ubuntu-latest-で使えるプリインストールツール)
    - [プリインストールされている主なツール](#プリインストールされている主なツール)
    - [必要に応じてセットアップ](#必要に応じてセットアップ)
  - [Google Cloudを使う方法](#google-cloudを使う方法)
    - [必要な準備](#必要な準備)
  - [時間で指定する場合](#時間で指定する場合)
  - [checkout ステップの動作](#checkout-ステップの動作)
  - [sshの設定方法](#sshの設定方法)
  - [変数の設定](#変数の設定)
    - [GitHub Secretsを使った環境変数の管理](#github-secretsを使った環境変数の管理)
  - [Pythonコードを実行する方法](#pythonコードを実行する方法)
    - [Pythonファイルを実行する](#pythonファイルを実行する)
    - [Pythonスクリプトを直接記述](#pythonスクリプトを直接記述)
  - [メール、Slack等に通知する方法](#メールslack等に通知する方法)
    - [GitHubのデフォルト通知設定を利用する](#githubのデフォルト通知設定を利用する)
    - [特定のメールアドレスに送信](#特定のメールアドレスに送信)
      - [mailxコマンドで直接メール送信](#mailxコマンドで直接メール送信)
      - [Slackや他の通知方法](#slackや他の通知方法)
  - [ローカル環境でGit Actionを実行する](#ローカル環境でgit-actionを実行する)
    - [Actツール](#actツール)
      - [使い方](#使い方)


ワークフローをGUIから作成する方法

1. Githubの画面を開く
2. repositoryを選択し"Actions"を押下する
3. ワークフローの定義をする

リポジトリのソースに記載する場合のフォルダ
```
mkdir -p .github/workflows
```

**(特徴)**
* 1時間ごとの上限:
  * 無料プランでは、リポジトリごとに 1時間に最大20回までのジョブが実行されます。
* 変更後の反映タイミング:
  * cron を変更しても、反映には数分かかる場合があります。
* スケジュール実行の確認:
  * 実行履歴は GitHub Actions のワークフローページから確認可能です。

ローカルで試す方法:actライブラリを利用する

---

### 実際にワークフローをコーディングしてみる!!

developブランチにプッシュがあった場合をトリガーにする

```
name: deploy_dev_hello
on:
  push:
    branches:
      - develop
jobs:
  job1:
    name: git_pull
    runs-on: ubuntu-latest
    steps:
      - name: hello
        run: echo "hello"
```
* name: ワークフロー全体の名前

**onについて**

* on: ワークフローをトリガーする条件を指定します
  * push: リポジトリに対する プッシュ（push）操作 をトリガーとして実行されます
  * branches: `- develop`  特定のブランチに対するプッシュだけをトリガーとする設定


**jobについて**


* job1: ジョブ名で、ユーザーが自由に設定
  * name: ジョブに対する分かりやすい名前（UIに表示される名前）
  * runs-on: このジョブがどの環境で実行されるかを指定します

**stepについて**

* steps: 各ジョブ内で実行される具体的なアクション（ステップ）を定義します。
  * name: ステップに付けたわかりやすい名前
  * run: 実行するシェルコマンドを記述します

---

### イベントの基本について

* push...プッシュがあった場合
* pull...プルがあった場合
* pull_request...プルリクがあった場合
* workflow_dispatch...手動で行う場合


複数のイベントの定義
```
on: [push, fork]
```


---

### GitHub Actionsのジョブを実行する仮想環境について

`runs-on`で指定する

### **1. `runs-on` に指定できるもの**

`runs-on` では、GitHub Actionsのジョブを実行する仮想環境（ホストマシン）を指定します。GitHubが提供するホストランナー、またはセルフホストランナーが使用できます。

---

#### **GitHubホストランナー（プリセット環境）**

| 環境名           | 説明                                  | 特徴                                         |
| ---------------- | ------------------------------------- | -------------------------------------------- |
| `ubuntu-latest`  | 最新のUbuntu LTS (現在は22.04)        | 多くのツールがプリインストール、最も一般的。 |
| `ubuntu-22.04`   | Ubuntu 22.04                          | 最新のUbuntuバージョン。                     |
| `ubuntu-20.04`   | Ubuntu 20.04                          | 安定したLTSバージョン。                      |
| `windows-latest` | 最新のWindows Server 2022             | Windows環境向け。                            |
| `windows-2022`   | Windows Server 2022                   | 最新バージョンのWindowsサーバー。            |
| `macos-latest`   | 最新のmacOS（現在はmacOS 13 Ventura） | macOS向けの開発環境。                        |
| `macos-13`       | macOS 13 Ventura                      | 最新のmacOSバージョン。                      |

- ubuntu-latest:
  - 言語やツールがプリインストールされています。
    - PythonやNode.js、Java、Ruby、Go、PHP、.NET、Rustなどの主要なプログラミング言語
    - DockerおよびDocker Composeが利用可能。
    - Git, Curl, Wget, SSHなど一般的な開発ツールもインストール済み


#### セルフホストランナー

`self-hosted` を指定すると、GitHubが提供する環境ではなく、
自分が用意したサーバーやクラウド環境でジョブを実行できます。

```yaml
runs-on: self-hosted
```

セルフホストランナーを使うと、カスタムソフトウェアやリソースにアクセス可能です。
サーバーのセットアップや管理が必要です。

#### 特定の言語やバージョンを利用する場合、以下のように設定できます。

Pythonの特定バージョン

```yaml
steps:
  - name: Set up Python
    uses: actions/setup-python@v4
    with:
      python-version: '3.11'
```

Node.jsの特定バージョン:

```yaml
steps:
  - name: Set up Node.js
    uses: actions/setup-node@v3
    with:
      node-version: '18'
```

---

### **2. `ubuntu-latest` で使えるプリインストールツール**

`ubuntu-latest` は多くの開発ツールがプリインストールされているため、非常に便利です。

#### プリインストールされている主なツール
- **Python**:
  - Python 2.x と Python 3.x の複数バージョンが利用可能。
  - `python3` や `python3.x` コマンドで直接利用できます。
  - 例:
    ```yaml
    steps:
      - name: Check Python version
        run: python3 --version
    ```
- **Node.js**:
  - 複数バージョンがインストール済み（`nvm`で管理）。
- **Java**:
  - OpenJDKがプリインストールされています。
- **Ruby, Go, PHP, .NET, Rust**:
  - 各言語の主要なバージョンが利用可能。
- **Docker**:
  - DockerおよびDocker Composeが利用可能。
- **Git, Curl, Wget, SSH**:
  - 一般的な開発ツールもインストール済み。

#### 必要に応じてセットアップ
特定の言語やバージョンを利用する場合、以下のように設定できます。

- Pythonの特定バージョン:
  ```yaml
  steps:
    - name: Set up Python
      uses: actions/setup-python@v4
      with:
        python-version: '3.11'
  ```

- Node.jsの特定バージョン:
  ```yaml
  steps:
    - name: Set up Node.js
      uses: actions/setup-node@v3
      with:
        node-version: '18'
  ```

---

### Google Cloudを使う方法

GitHub Actionsでは、Google Cloud（GCP）にデプロイや操作を行うことも可能です。
以下の方法でセットアップします。

#### 必要な準備

1. GCPサービスアカウント
2. GitHub Secretsに登録:ダウンロードしたJSONキーをGitHub Secretsに登録（例: `GCP_CREDENTIALS`）。

以下は、GitHub ActionsでGoogle Cloudにデプロイする例です。

```yaml
name: Deploy to Google Cloud

on:
  push:
    branches:
      - main

jobs:
  deploy:
    runs-on: ubuntu-latest

    steps:
      - name: Checkout code
        uses: actions/checkout@v3

      - name: Authenticate with GCP
        uses: google-github-actions/auth@v1
        with:
          credentials_json: ${{ secrets.GCP_CREDENTIALS }}

      - name: Set up Google Cloud SDK
        uses: google-github-actions/setup-gcloud@v1
        with:
          project_id: your-project-id
          export_default_credentials: true

      - name: Deploy to App Engine
        run: |
          gcloud app deploy app.yaml --quiet
```

* 主要なGoogle Cloud関連のアクション
  * `google-github-actions/auth`
    * GCPに認証するためのアクション。
  * `google-github-actions/setup-gcloud`
    * Google Cloud SDKをセットアップするアクション。



---

### 時間で指定する場合

```yaml
on:
  schedule:
    # Cron形式でスケジュールを設定
    # 毎日午前0時に実行
    - cron: '0 0 * * *'
```


### checkout ステップの動作

on: pushなどの場合は、`uses: actions/checkout@v3` は 
トリガーされたブランチ のコードを自動的にチェックアウト（取得）します。

on: schedule を指定した場合、特定のブランチは対象になりません。
デフォルトブランチ（通常は main または master）の内容を基準に実行されます。


```yaml
    steps:
      - name: Checkout code
        uses: actions/checkout@v3```
```

特定のブランチを対象にするには: 
actions/checkout の ref を使って明示的にブランチを指定する必要があります。

```yaml
      - name: Checkout develop branch
        uses: actions/checkout@v3
        with:
          ref: develop
```


checkout@v4 はセキュリティ・パフォーマンスの向上が含まれているため、@v3 よりも優れています。最新バージョンを使用することを推奨します。


スケジュール実行でコードを取得する場合、actions/checkout がデフォルトブランチのコードをクローンします。
例えば、以下のステップでクローンされるのはデフォルトブランチの内容です：


### sshの設定方法

* SSH秘密鍵をGitHub Secretsに追加

(例)
ローカルで新しいSSHキーを作成（例: id_rsa_github_actions）

```bash
ssh-keygen -t rsa -b 4096 -C "github-actions" -f id_rsa_github_actions
```

* 作成された秘密鍵（id_rsa_github_actions）の内容をGitHubリポジトリのSecretsに追加します

GitHubのプロジェクトページのSettings - Secrets - Add a new secretの順にクリックし、
例えば以下のような名前でそれぞれ登録してください。別の名前でも構いません。

```sh
SSH_KEY - SSH秘密鍵
KNOWN_HOSTS - サーバーの公開鍵＋ホスト名/IPアドレス（.ssh/known_hostsファイルのフォーマト）
```


* 公開鍵（id_rsa_github_actions.pub）をデプロイ対象サーバーの ~/.ssh/authorized_keys に登録する
* GitHub Actionsのワークフロー設定

以下は実際にsshで接続する作業

```bash
      - name: Set up SSH
        run: |
          mkdir -p ~/.ssh
          echo "${{ secrets.SSH_PRIVATE_KEY }}" > ~/.ssh/id_rsa
          chmod 600 ~/.ssh/id_rsa

      - name: Add known hosts
        run: |
          ssh-keyscan -H your-server-ip-or-hostname >> ~/.ssh/known_hosts

     - name: Deploy to server
        run: |
          scp -r ./your-project-folder user@your-server-ip:/path/to/deploy
          ssh user@your-server-ip "cd /path/to/deploy && ./restart-server.sh"
```

### 変数の設定

#### GitHub Secretsを使った環境変数の管理

GitHub Secretsは、デプロイ用のAPIキーやパスワードなどの機密データを安全に保存・管理するための方法です
リポジトリの「Settings → Secrets and variables → Actions」から、
New repository secret をクリックし、変数を登録します。

GitHub ActionsでSecretsを使う Secretsを環境変数として利用できます。

```bash
jobs:
  deploy:
    runs-on: ubuntu-latest

    steps:
      - name: Checkout code
        uses: actions/checkout@v3

      - name: Use Secrets as Environment Variables
        env:
          API_KEY: ${{ secrets.API_KEY }}
          DB_PASSWORD: ${{ secrets.DB_PASSWORD }}
        run: |
          echo "API Key is $API_KEY"
          echo "Database Password is $DB_PASSWORD"
```

環境変数を .env ファイルで管理し、GitHub Actionsで利用することも可能です。
ただし、.env ファイルには機密情報を直接記載しないように注意してください。


```bash
   - name: Load .env file
        run: |
          set -a
          source .env
          set +a
```

### Pythonコードを実行する方法

#### Pythonファイルを実行する

Pythonスクリプト（例: script.py）を用意し、
GitHub Actionsで実行します。

* script.py (リポジトリに用意するファイル)

```yaml
jobs:
  run-python-script:
    runs-on: ubuntu-latest

    steps:
      - name: Checkout code
        uses: actions/checkout@v3

      - name: Set up Python
        uses: actions/setup-python@v4
        with:
          python-version: '3.10'

      - name: Install dependencies
        run: |
          python -m pip install --upgrade pip
          pip install requests

      - name: Run Python script
        run: python script.py
```

./srcディレクトリを基準に実行する場合

```yaml
     # 4. src ディレクトリを基準に Python ファイルを実行
      - name: Run Python script
        working-directory: ./src
        run: python main.py
```
または
```yaml
- name: Set PYTHONPATH
  run: |
    export PYTHONPATH=$PYTHONPATH:$(pwd)/src
    python src/main.py```
```


#### Pythonスクリプトを直接記述

Pythonスクリプトをファイルにせず、YAML内で直接記述することも可能です。

```yaml
    steps:
      - name: Set up Python
        uses: actions/setup-python@v4
        with:
          python-version: '3.10'

      - name: Install dependencies
        run: |
          python -m pip install --upgrade pip
          pip install requests

      - name: Run Python script
        run: |
          python - <<EOF
          import requests

          url = "https://example.com/api/v1/data"
          response = requests.get(url)

          if response.status_code == 200:
              print("API call succeeded:", response.json())
          else:
              print("API call failed with status code:", response.status_code)
              raise Exception("API call failed")
          EOF
```



### メール、Slack等に通知する方法

#### GitHubのデフォルト通知設定を利用する

GitHubではデフォルトで、ワークフローが失敗したときに
リポジトリのウォッチャーや関係者に通知（メールやプッシュ通知）が送信されます。

**手順**

* リポジトリの右上にある「Watch」ボタンを押下する
* 「Custom」から「Actions」の通知を有効にします。
* GitHubのアカウント設定 → 通知設定 で、メール通知が有効になっているか確認します

#### 特定のメールアドレスに送信

mailx や外部サービス（例: SendGridやSlack）を利用します。

##### mailxコマンドで直接メール送信

失敗時に mailx を使ってメールを送る例です。
事前にGitHub Actionsのランナー環境にメール送信設定を整える必要があります。

```
      - name: Run a script
        run: |
          echo "Running some task..."
          exit 1 # 強制的に失敗させる

      - name: Send email on failure
        if: failure() # このステップは失敗時のみ実行
        run: |
          echo "Workflow failed!" | mailx -s "Workflow Failure" you@example.com
```

#####  Slackや他の通知方法

GitHub ActionsにはSlackやWebhook通知のためのアクションが用意されています。

例: Slack通知
SlackのIncoming Webhooks を設定してWebhook URLを取得。
リポジトリの Secrets に SLACK_WEBHOOK_URL を追加。

```
name: Notify on Failure

on:
  push:
  schedule:
    - cron: '0 0 * * *' # 定期実行

jobs:
  notify-failure:
    runs-on: ubuntu-latest

    steps:
      - name: Checkout code
        uses: actions/checkout@v3

      - name: Run a script
        run: |
          echo "Running some task..."
          exit 1 # 強制的に失敗させる

      - name: Notify Slack on failure
        if: failure() # このステップは失敗時のみ実行
        run: |
          curl -X POST -H 'Content-type: application/json' --data '{"text":"Workflow failed!"}' ${{ secrets.SLACK_WEBHOOK_URL }}
```

----

### ローカル環境でGit Actionを実行する

#### Actツール

* GitHub Actionsワークフローをローカルで実行できるツール。
* GitHub Actionsの .github/workflows ファイルをそのまま使用可能。
* Dockerコンテナを使ってローカル環境でステップを再現。
* GitHub Actionsを学習・テストするのに最適

**(用途)**
* ローカルでworkflowをテストする
  * プッシュせずに問題を修正できるため、時間とリソースを節約
* リポジトリのWorkflowをローカルで実行
  * 既存のリポジトリのWorkflowをトリガーイベントを指定してローカルで実行
* GitActionsの学習

**(制約)**
* actは内部的にDockerを使用しているため、Dockerが動作可能な環境が必要
* GitHub Actionsの完全な再現は難しい
  * actに対応していない一部のActionsや機能（プラグインなど）がある
    * actions/cache
* GitHubリソースへの依存
  * プライベートリポジトリやGitHub APIを利用するWorkflowでは、GITHUB_TOKENが必要になる


**(手順)**
1. Dockerファイルを作成する
    * [Dockerfile](../actions/Dockerfile)


**GITHUB_TOKENは必要か？**

* 必要になるケース
  * プライベートリポジトリを操作する場合
    * actを使ってプライベートリポジトリのワークフローをテストするには、GITHUB_TOKENが必要です
    * プライベートリポジトリにアクセスするにはGitHubのPersonal Access Token（PAT）が必要です。
    * PATには少なくとも repo 権限が必要です。
  * APIリクエスト制限を回避したい場合:
    * actはGitHub APIを利用してリソースを取得しますが、未認証のリクエストは1時間あたりの制限（60リクエスト）があります
    * GITHUB_TOKENを渡すと、認証済みとして扱われるため、APIリクエストの制限が緩和（5,000リクエスト/時間）されます


* ワークフローのテスト対象がパブリックリポジトリの場合、GITHUB_TOKENは必須ではありません。
* ローカルでの簡易テスト
* ワークフローが外部リソース（GitHub APIやリポジトリデータなど）に依存していない場合、GITHUB_TOKENなしで動作します。

##### 使い方

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