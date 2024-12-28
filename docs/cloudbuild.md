## Goole Cloud Buildの活用方法

https://appj.pglikers.com/knowledge/open.knowledge/view/44?offset=0

- [Goole Cloud Buildの活用方法](#goole-cloud-buildの活用方法)
  - [実際にワークフローをコーディングしてみる!!](#実際にワークフローをコーディングしてみる)
- [スクリプトを実行する](#スクリプトを実行する)
  - [直接スクリプトを実行する](#直接スクリプトを実行する)
    - [DockerイメージをビルドしてContainer Registryにプッシュする](#dockerイメージをビルドしてcontainer-registryにプッシュする)
- [ローカルで検証する方法](#ローカルで検証する方法)
  - [cloud-build-localを実行する](#cloud-build-localを実行する)
  - [Skaffold](#skaffold)


CI/CDの実行ファイル

```
touch cloudbuild.yaml
```

* Google Cloud Buildには無料枠（120分/月）があります。
* 無料枠を超えた場合、使用したビルド時間に応じて課金されます（$0.003/秒）

### 実際にワークフローをコーディングしてみる!!

Google Cloud Build の設定ファイルで、ビルドステップやプロセスを定義します。

```sh
steps:
  # Build ステージ
  - name: 'ubuntu'
    args: ['echo', 'Building...']
    id: 'Build'

   # Test ステージ (スクリプトを実行)
  - name: 'ubuntu'
    entrypoint: 'bash'
    args:
      - '-c'
      - |
        echo "Testing..."
        echo "Running test script..."
        # 任意のスクリプトコマンドをここに追加
        echo "Test completed successfully."

  # Deploy ステージ
  - name: 'ubuntu'
    args: ['echo', 'Deploying...']
    id: 'Deploy'
```

* steps: Cloud Build の実行ステップを定義します。各ステップは一つの操作を定義します
* entrypoint: 明示的に指定することも可能です。
  * dockerイメージのデフォルトエントリポイント（通常は /bin/sh や /bin/bash）を上書きできます
* args: コンテナ内で実行するコマンドと引数をリスト形式で指定します。
  * 最初の要素が実行するコマンド、それ以降がそのコマンドへの引数です。
  * `args: ['echo', 'Hello, Cloud Build!']`
* script:
  * スクリプトをそのまま記述して実行したい場合に使用します。
  * args を使用する方が一般的です。
* id: 各ステップに一意の ID を付けることができます。
  * 他のステップで依存関係を設定するために使用します。
* name: 
  * alpine: イメージは軽量で高速
  * ubuntu: 一般的によく使用されています
* waitFor:
  * 特定のステップの実行が終了するのを待ってから次のステップを実行する場合に使用します。
  * `waitFor: ['build-step-1']`
* env
  * 環境変数を設定できます。
  * `- MY_VAR=Hello`
* dir
  * ステップが実行されるワーキングディレクトリを指定します
  * `dir: './subdirectory'`



```sh
gcloud builds submit --config=cloudbuild.yaml .
```


## スクリプトを実行する


```yaml
steps:
  # Test ステージでスクリプトを実行
  - name: 'ubuntu'
    entrypoint: 'bash'
    args:
      - './test.sh'
```

### 直接スクリプトを実行する

https://cloud.google.com/build/docs/configuring-builds/run-bash-scripts?hl=ja

```yaml
steps:
- name: 'bash'
  script: |
    #!/usr/bin/env bash
    echo "Hello World"
- name: 'ubuntu'
  script: echo hello
- name: 'python'
  script: |
    #!/usr/bin/env python
    print('hello from python')
```


#### DockerイメージをビルドしてContainer Registryにプッシュする

```yaml
steps:
- name: 'gcr.io/cloud-builders/git'
  args: ['clone', 'https://github.com/your-repo.git']

- name: 'gcr.io/cloud-builders/npm'
  args: ['install']

- name: 'gcr.io/cloud-builders/npm'
  args: ['test']

- name: 'gcr.io/cloud-builders/docker'
  args: ['build', '-t', 'gcr.io/your-project-id/your-image', '.']

images:
- 'gcr.io/your-project-id/your-image'
```


## ローカルで検証する方法

* ~~ローカルのみでCloud Buildを動かせるものは存在しない(公式なし)~~
* GCP の cloudbuild を ローカルで実行するcloud-build-localというツールがある
  * 公式のドキュメント: https://cloud.google.com/cloud-build/docs/build-debug-locally
* Skaffoldを使用してCloud BuildのYAMLをローカルで検証できます(GCPと連携が必要)

### cloud-build-localを実行する

(前提条件)
* Google Cloud CLI をインストール済みであること
* Dockerfileを準備されていること


```sh
gcloud components install cloud-build-local
```

**(手順)**
1. Dockerファイルを作成する
    * [Dockerfile](../gcloud/Dockerfile)

ローカルでローカルで実行する

```
cloud-build-local --config=cloudbuild.yaml --dryrun=false .
```

### Skaffold

SkaffoldはGoogle Cloud Buildと公式に統合されており、
Google Cloud Buildをビルドエンジンとして利用できます。
そのため、Google Cloud Buildのビルドステップをローカルで再現可能です。

ただしSkaffoldは、ローカル環境からGoogle Cloud Build APIを呼び出して、
Cloud Buildをビルドエンジンとして使用します。
実際にはビルドやデプロイ処理はGoogle Cloud上で実行されます。


(前提条件)
* Skaffoldをインストール済みであること
* Google Cloud SDKをインストールし、gcloud authで認証
* GCPプロジェクトが設定されていること

```sh
curl -Lo skaffold https://storage.googleapis.com/skaffold/releases/latest/skaffold-linux-amd64
chmod +x skaffold
sudo mv skaffold /usr/local/bin

skaffold version
```

Skaffoldの設定ファイルを作成

Skaffold用の設定ファイル（skaffold.yaml）をプロジェクトディレクトリに作成します。
以下はGoogle Cloud Buildをビルドエンジンとして指定する例です。

```yaml
apiVersion: skaffold/v2beta27
kind: Config
build:
  artifacts:
    - image: gcr.io/your-project-id/your-image
      context: .  # Dockerfileのあるディレクトリ
  googleCloudBuild:
    projectId: your-project-id  # Google CloudのプロジェクトIDを指定
    diskSizeGb: 100  # 必要に応じて変更
    machineType: "N1_HIGHCPU_8"  # オプション: マシンタイプを指定
```


