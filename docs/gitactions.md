## Git Actionの活用方法

https://appj.pglikers.com/knowledge/open.knowledge/view/44?offset=0

- [Git Actionの活用方法](#git-actionの活用方法)
  - [ワークフローのコーディングの基本!!](#ワークフローのコーディングの基本)
  - [イベントの基本について](#イベントの基本について)
  - [GitHub Actionsのジョブを実行する仮想環境について](#github-actionsのジョブを実行する仮想環境について)
  - [**1. `runs-on` に指定できるもの**](#1-runs-on-に指定できるもの)
    - [**GitHubホストランナー（プリセット環境）**](#githubホストランナープリセット環境)
    - [セルフホストランナー](#セルフホストランナー)
  - [**2. `ubuntu-latest` で使えるプリインストールツール**](#2-ubuntu-latest-で使えるプリインストールツール)
    - [プリインストールされている主なツール](#プリインストールされている主なツール)
  - [ワークフロー作成のTIPS](#ワークフロー作成のtips)
  - [ssh接続で他サーバーを制御する](#ssh接続で他サーバーを制御する)
  - [環境変数・GitHub Secretの管理](#環境変数github-secretの管理)
  - [Pythonコードを実行する方法](#pythonコードを実行する方法)
  - [メール、Slack等に通知する方法](#メールslack等に通知する方法)
  - [ローカル環境でGit Actionを実行する](#ローカル環境でgit-actionを実行する)
  - [Pytestを実行する方法](#pytestを実行する方法)


**ワークフローをGUIから作成する方法**

1. Githubの画面を開く
2. repositoryを選択し"Actions"を押下する
3. ワークフローの定義をする

**リポジトリのソースに記載する場合のフォルダ**

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

### ワークフローのコーディングの基本!!

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


### ワークフロー作成のTIPS

https://appj.pglikers.com/knowledge/open.knowledge/view/459

* 必要に応じてアプリをセットアップする
* トリガーを時間で指定する場合(スケジューラ
* checkoutステップの動作(ソース取得)
* Google Cloudを使う方法

### ssh接続で他サーバーを制御する

https://appj.pglikers.com/knowledge/open.knowledge/view/458

### 環境変数・GitHub Secretの管理

https://appj.pglikers.com/knowledge/open.knowledge/view/457

### Pythonコードを実行する方法

https://appj.pglikers.com/knowledge/open.knowledge/view/439

### メール、Slack等に通知する方法

https://appj.pglikers.com/knowledge/open.knowledge/view/441

### ローカル環境でGit Actionを実行する

https://appj.pglikers.com/knowledge/open.knowledge/view/440

### Pytestを実行する方法

* https://appj.pglikers.com/knowledge/open.knowledge/view/443
  