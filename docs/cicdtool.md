# CI/CDツールについて

* Git Actions
  * yamlベース
  * Dockerを中心にしたアプローチ
  * Github専用の設計
* GitLab/CI
* Google Cloud Build
  * yamlベース
  * Dockerを中心にしたアプローチ
  * GitHubやGitLabを公式にサポート
* Drone
  * yamlベース
  * Dockerを中心にしたアプローチ
  * GitHub、GitLab、Bitbucket(クラウド版)、Gitea、Gogsを公式サポート
* Jenkins
  * nkinsfileというスクリプト形式(GitOpsベース)
  * Dockerコンテナの使用は任意であり、ジョブやステップごとに分離されない場合がある
  * カスタマイズ性が高い
  * プラグインを利用することで非常に幅広いソース管理ツールと連携可能
    * GitHub、GitLab、Bitbucket、Subversion
    * Jenkins用のGitプラグインを使用して連携可能

### GitBucketとの連携について

公式ではどれもサポートされていない
Webhook、SSHリポジトリなどを利用して連携が必要




