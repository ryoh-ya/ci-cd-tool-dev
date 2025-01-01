# Jenkinsの活用方法

https://appj.pglikers.com/knowledge/open.knowledge/view/438


- [Jenkinsの活用方法](#jenkinsの活用方法)
  - [Jenkinsの全体像について](#jenkinsの全体像について)
  - [Jenkinsfileの記載方法](#jenkinsfileの記載方法)
    - [ScriptedでできてDeclarativeでできないもの](#scriptedでできてdeclarativeでできないもの)
    - [Declarative（宣言型）パイプライン](#declarative宣言型パイプライン)
      - [**agentについて**](#agentについて)
      - [**stagesについて**](#stagesについて)
    - [環境変数を設定する場合](#環境変数を設定する場合)
    - [stepsで処理の書き方](#stepsで処理の書き方)
    - [Pythonコードを実行する方法](#pythonコードを実行する方法)
    - [ローカル環境でJenkinsを実行する](#ローカル環境でjenkinsを実行する)
    - [ソースを取得する方法](#ソースを取得する方法)
    - [pipelineの制御について](#pipelineの制御について)
    - [データの保存方法について](#データの保存方法について)

Jenkinsとは
  * オープンソースの継続的インテグレーション（CI）および継続的デリバリー（CD）ツール
  * ソフトウェア開発プロセスを自動化し、ビルド、テスト、デプロイを効率的に行うツール

**(特徴)**
* ソースコードのビルド、テスト、デプロイを自動化
* プラグイン（Git、Docker、Kubernetes、Slackなど）
* ジョブのカスタマイズ性（パイプラインやフリースタイルジョブなど）
* クロスプラットフォーム: Linux、Windows、MacOS上で動作可能
* 他オープンソースとの連携がしやすい

ローカルで試す方法:Jenkinsfile Runnerを利用する

---
## Jenkinsの全体像について

* **パイプライン(Pipeline)**
  * Jenkinsで定義されたCI/CDプロセス全体を指す
  * パイプラインは、1つのJenkinsfile
  * Jenkinsではパイプラインは「ジョブ」とも呼ばれます
* **ノード(Node)**
  * Jenkinsでジョブが実行される環境を指します
  * GitHub Actionsでいう runs-on: ubuntu-latest のようなもの
  * Masterノード(管理サーバー)
    * Jenkins自体が動作するサーバー（コントローラー）
  * エージェントノード(ビルドサーバー)
    * エージェントノード: 実際にジョブ（ビルドやテスト）が実行されるサーバー
  * 固定ノード: あらかじめセットアップされたサーバー
  * 動的ノード: 必要に応じて生成・破棄される一時的な環境
* **ステージ(Stage)**
  * パイプラインの中で定義される処理の単位です
  * ステージは「処理の段階」を表し、視覚的に区分されます
* **ステップ(Step)**
  * ステージ内で実行する具体的な処理内容を記述します。


## Jenkinsfileの記載方法

Jenkinsで使用するパイプライン（CI/CDプロセス）をコードとして定義したファイル
Jenkinsfileの基本的な書き方には2種類ある

* Declarative（宣言型）パイプライン: 読みやすく、推奨される書き方
* Scripted（スクリプト型）パイプライン: より柔軟で詳細な制御が可能

### ScriptedでできてDeclarativeでできないもの

**DeclarativeとScriptedの比較**

| **機能**                 | **Declarative** | **Scripted**   |
|--------------------------|----------------|----------------|
| 簡単な分岐（`if`構文）   | 可能（限定的）  | 可能（柔軟）   |
| 複雑なループ（`while`など）| 難しい（非推奨）| 簡単に記述可能 |
| 動的なノード割り当て       | 難しい           | 可能（柔軟）   |
| 計算処理や状態管理         | 難しい           | 可能           |
| 柔軟なプラグイン操作       | 限定的           | 自由度高い     |

### Declarative（宣言型）パイプライン

```groovy
pipeline {
    agent any  // 実行するエージェント（ノード）を指定。 "any" はどのノードでも可。
    
    stages {
        stage('Build') {  // ステージ名（例: ビルド）
            steps {
                echo 'Building the application...'  // シェルコマンドやスクリプトを記述
            }
        }
        stage('Test') {  // ステージ名（例: テスト）
            steps {
                echo 'Running tests...'
            }
        }
        stage('Deploy') {  // ステージ名（例: デプロイ）
            steps {
                echo 'Deploying the application...'
            }
        }
    }
}
```

* pipeline: パイプライン全体を定義するトップレベルブロック。

#### **agentについて**

* ジョブをどのノードで実行するか指定
* agent any（すべてのノード）や、特定のノードラベルを指定可能

```groovy
agent { label 'docker' } // Dockerノードで実行する
```

Dockerのイメージで実行する場合は
HostマシンにDockerがインストールされている必要があります。



```groovy
agent { 
    docker {
        image 'node:18-alpine' // 使用する Docker イメージ
    }
  } // Dockerノードで実行する
```

Scripted Pipelineの時の書き方

```groovy
node {
    // 最初のコンテナ
    docker.image('alpine:3.16').inside {
        sh 'echo "Running in Alpine" > shared_file.txt'
        sh 'ls -la'
    }
    // 次のコンテナ
    docker.image('ubuntu:22.04').inside {
        sh 'cat shared_file.txt'
        sh 'echo "Running in Ubuntu" >> shared_file.txt'
        sh 'ls -la'
    }
}
```


ユーザーで実行するときにpipがキャッシュディレクトリや
システムレベルのパッケージディレクトリに書き込めずエラーになる

対策1:ユーザーをrootで指定する

```groovy
docker {
    image 'python:3.11-slim' // gcloud コマンド用の公式イメージ
    args '-u root' // Dockerコンテナをrootユーザーで実行                    
}
```
対策2:ユーザーレベルでインストールする

```sh
python -m pip install --upgrade --user pip
python -m pip install --user requests
```

`pip install`などは同じDockerコンテナ内 であれば有効ですが、
別のステージやステップ で再び異なるエージェント（Dockerコンテナ）が使われる場合、
無効になります。



#### **stagesについて**

CI/CDプロセスを段階的に定義するセクション

* stages 
  * stage("stage_name") // ステージ名を指定する
    * steps: シェルコマンドやスクリプトを記述

----

### 環境変数を設定する場合

```groovy
environment {
    APP_ENV = 'production'
    JAVA_HOME = '/usr/lib/jvm/java-11-openjdk'
}
```

### stepsで処理の書き方

https://appj.pglikers.com/knowledge/open.knowledge/view/454

### Pythonコードを実行する方法

https://appj.pglikers.com/knowledge/open.knowledge/view/451

### ローカル環境でJenkinsを実行する

https://appj.pglikers.com/knowledge/open.knowledge/view/452

### ソースを取得する方法

https://appj.pglikers.com/knowledge/open.knowledge/view/453


### pipelineの制御について

* Jenkinsのジョブは並行ビルド（Parallel Buildをサポートしています。

```groovy
pipeline {
options {
    disableConcurrentBuilds() // 並行ビルドを無効化
}
```

### データの保存方法について

https://appj.pglikers.com/knowledge/open.knowledge/view/455
