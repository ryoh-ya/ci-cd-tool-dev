# CI/CDツールについて

* [Git Actions](./gitactions.md)
  * yamlベース
  * Dockerを中心にしたアプローチ
  * Github専用の設計
* [GitLab/CI]()
  * yamlベース
  * GitLabに統合されているCI/CD機能
  * SCM（ソースコード管理）とCI/CDが一体化しているため使いやすいです。
* [Google Cloud Build](./cloudbuild.md)
  * yamlベース
  * Dockerを中心にしたアプローチ
  * GitHubやGitLabを公式にサポート
  * ステップの実装でやりにくいところ
    * if制御、ステップの順番制御がやりにくい
* [Jenkins](./jenkins.md)
  * nkinsfileというスクリプト形式(GitOpsベース)
  * Dockerコンテナの使用は任意であり、ジョブやステップごとに分離されない場合がある
  * カスタマイズ性が高い
  * プラグインを利用することで非常に幅広いソース管理ツールと連携可能
    * GitHub、GitLab、Bitbucket、Subversion
    * Jenkins用のGitプラグインを使用して連携可能
* [Drone]()
  * yamlベース
  * Dockerを中心にしたアプローチ
  * GitHub、GitLab、Bitbucket(クラウド版)、Gitea、Gogsを公式サポート
  * Dockerベースで軽量、カスタマイズ性が高い
  * SCMなしではビルドできなかった(検証なし)
  * Giteaとの組み合わせがおすすめ
  * Go言語


**(体験してみた感想)**

| 項目           | Git Actions | Cloud Build | Jenkins    |
| -------------- | ----------- | ----------- | ---------- |
| SCM対応数      | ★★        | ★★★★    | ★★★★   |
| 初期構築       | ★★★★★  | ★★★★    | ★         |
| pipeline製造   | ★★★★★  | ★★        | ★★★★★ |
| 通知機能       | ★★★★    | ★★        | ★★★★   |
| カスタマイズ性 | ★★★      | ★          | ★★★★★ |

* 今のところあまりCloud Buildの良さがでていない
  * GCPの他のサービスと組み合わせることにより効果が発揮しそう
    * Cloud Storageとの連携(アーティファクトの保存)
      *  Dockerイメージ、ログ、デプロイ用ファイルの格納
      * コストが低く、長期保存に適している
    * Container Registry/Artifact Registry
      * ビルドしたDockerイメージをArtifact RegistryやContainer Registryに自動でプッシュ可能
      * GKE（Google Kubernetes Engine）やCloud Runへのデプロイがスムーズ
    *  GKE（Google Kubernetes Engine）との連携
      *  KubernetesクラスタへのCI/CDパイプラインが簡単に構築可能
      *  kubectlコマンドがCloud Build内でそのまま実行できるため、インフラ管理が効率的
    * Cloud Run
      * Cloud BuildでビルドしたアプリケーションをCloud Run（サーバーレス）に直接デプロイ可能



### GitBucketとの連携について

公式ではどれもサポートされていない
Webhook、SSHリポジトリなどを利用して連携が必要
同じJavaのJenkinsが相性がよさそう。

### CI/CDツールとGCP接続時の認証の整理

推奨される設定方法

* Jenkins
  * サービスアカウント
* Git Action
  * Workload Identity
* Google Cloud Build
  * 特にサービスアカウントなしでアクセス可能

#### Workload Identityについて

  * サービスアカウントを使用せずにワークロードからGoogle Cloudにアクセスできます
  * サービスアカウントキーの漏洩リスクをなくし、OIDCトークンなどを利用して安全にリソースへアクセス
  * GitHub ActionsやAWSなど外部サービスからGoogle Cloudにアクセスする際に非常に有用です。
    * プールを作成する(Workload Identity Poolは外部IDを認識するための基盤)
      * ODICプロバインダを使用する(OIDCは、GCPが外部ワークロードを認証するために利用する標準プロトコル)
      * プールIDがGoogle Cloudのリソースにアクセスできるようにする
      * GCPリソースにアクセス可能なサービスアカウントを関連付けます
  * Workload Identity Federationでは、外部のワークロードがGoogleサービスアカウントとして動作することを許可します。
  * 外部ワークロードとは、GCPの外部で動作しているアプリケーションやプロセスのことを指します
