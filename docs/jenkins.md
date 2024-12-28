# Jenkinsの活用方法

https://appj.pglikers.com/knowledge/open.knowledge/view/438


- [Jenkinsの活用方法](#jenkinsの活用方法)
  - [Jenkinsの全体像について](#jenkinsの全体像について)
  - [Jenkinsfileを書いてみる](#jenkinsfileを書いてみる)
    - [ScriptedでできてDeclarativeでできないもの](#scriptedでできてdeclarativeでできないもの)
    - [Declarative（宣言型）パイプライン](#declarative宣言型パイプライン)
      - [**agentについて**](#agentについて)
      - [**stagesについて**](#stagesについて)
    - [環境変数を設定する場合](#環境変数を設定する場合)
    - [Pythonコードを実行する方法](#pythonコードを実行する方法)
      - [Host側にPython環境を使用する場合](#host側にpython環境を使用する場合)
      - [Dockerで一時的なPython環境を作成する方法](#dockerで一時的なpython環境を作成する方法)
    - [ローカル環境でJenkinsを実行する](#ローカル環境でjenkinsを実行する)
      - [Jenkinsfile Runner](#jenkinsfile-runner)
      - [jpi拡張子のWarningが表示される場合の対処方法](#jpi拡張子のwarningが表示される場合の対処方法)

* Jenkins
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


## Jenkinsfileを書いてみる

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

Node内でDockerを使用する場合、
HostマシンにDockerがインストールされている必要があります。



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

### Pythonコードを実行する方法

#### Host側にPython環境を使用する場合

* (メリット)
  * 設定が簡単（Pythonを一度インストールすれば、すぐに利用可能）
  * オーバーヘッドが少ない。
* (デメリット)
  * 余分なソフトウェアをインストールする必要があるため、環境が汚れる。
  * Pythonバージョンの切り替えや依存関係の管理が複雑
  
```groovy
pipeline {
    agent any
    stages {
        stage('Run Python Script') {
            steps {
                sh 'python3 --version'
                sh 'python3 script.py'
            }
        }
    }
}
```

#### Dockerで一時的なPython環境を作成する方法

* (メリット)
  * 環境がクリーン
  * 異なるPythonバージョンや依存関係を簡単に管理できる
  * コンテナ終了時に全てのデータが削除される
* (デメリット)
  * DockerがHostにインストールされている必要がある
  * コンテナの起動と終了のオーバーヘッドがある（軽微

```groovy
pipeline {
    agent {
        docker {
            image 'python:3.10' // 使用するDockerイメージを指定
            // image 'python:3.10-slim' 
        }
    }
    stages {
        stage('Run Python Script') {
            steps {
                sh '''
                    python3 - <<EOF
                    print("Hello, Jenkins!")
                    for i in range(5):
                        print(f"Number: {i}")
                    EOF
                '''
            }
        }
    }
}
```

### ローカル環境でJenkinsを実行する

* Jenkinsの公式イメージがありますのでローカルで確認できます
* Jenkinsfile Runnerにより軽微にpipelineの検証ができます

#### Jenkinsfile Runner

* Jenkinsのパイプラインスクリプト（Jenkinsfile）をローカル環境で実行できるツール。
* Jenkinsをフルにセットアップせずに、Jenkinsfileをテスト可能。
* Jenkinsfileのステージやステップを、軽量なコンテナ環境で再現。
* Jenkinsパイプラインの学習・デバッグに最適。


**(用途)**
* ローカルでJenkinsfileをテストする
  * Jenkinsのサーバーをセットアップせず、ローカルで素早く問題を発見・修正できる。
  * プラグインや設定を確認しながら、スクリプトの正確性を検証可能。
* 既存のJenkinsfileの動作確認
  * チームやプロジェクト内のJenkinsfileをトリガーイベントに基づいてローカルで実行。
  * プッシュやコミット前に、スクリプトの妥当性を検証。
* Jenkinsの学習
  * Jenkinsfileを記述・理解し、各ステップの動作を検証する学習プロセスに役立つ。

**(制約)**
* Jenkinsfile Runnerは内部的にコンテナを使用するため、Dockerが動作可能な環境が必要
* plugInに対応できていない
* Jenkinsサーバー依存の設定やリソースはRunner環境に完全移行できない
  * 認証情報やシークレット管理は追加設定が必要。
* Jenkinsの本番環境と動作が異なるケースがある
  * 特定の外部リソースやカスタムエージェントを必要とするパイプラインでは注意が必要

**(手順)**
1. Dockerファイルを作成する
    * [Dockerfile](../jenkins/Dockerfile)

公式イメージは存在する
```sh
docker pull jenkins/jenkinsfile-runner
```


#### jpi拡張子のWarningが表示される場合の対処方法

```
WARNING hudson.ClassicPluginStrategy#createPluginWrapper: encountered /usr/share/jenkins/ref/plugins/cloudbees-folder.hpi under a nonstandard name; expected cloudbees-folder.jpi
```

このエラーメッセージは、Jenkinsfile Runnerがプラグインファイルの拡張子を
予期している形式（.jpi）と異なる形式（.hpi）で検出したことを警告しています。
.hpiと.jpiは基本的に同じ形式のファイルですが、
Jenkinsの新しいバージョンでは.jpiを標準として採用しています。

このエラーが発生しても機能的には問題なく動作する場合がありますが、以下の解決策で警告を修正できます。

**解決方法**

動作に問題なければ無視でも大丈夫です

