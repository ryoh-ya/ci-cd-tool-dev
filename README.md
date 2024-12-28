## CI/CD 開発

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


**コンテナを終了するする**
```sh
docker-compose -f docker-compose.jenkins.yaml down
```


