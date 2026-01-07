## App Enginのゼロスケールについて

*  App Engin(Standard Environment)の場合
    * ゼロスケールが可能でトラフィックがない場合には費用をゼロにできます。
* App Engine Flexible Environmentではゼロスケールは不可能です
  * 最低1インスタンスが常駐:
    * トラフィックがない状態でも最小限のリソースが稼働しているため、ゼロスケールには対応していません
    * Flexible EnvironmentはDockerコンテナを利用した環境である
      * 少なくとも1つのインスタンスが常に動作する必要があります。
  * コストが発生する最小リソース使用量
    * 最小インスタンス（例: f1-micro）の稼働に対する料金が発生します
    * f1-microの場合は約$5.5になる
  * AppEngin(Standard Environment)もしくはCloud Runを利用してください



## App Enginのポートの設定

* App Engine(standard environment)では環境変数$PORTが自動的に割り当てられます。
  * 基本的にアプリケーションが使用できるポートは 1 つ
  * スケーラブルでシンプルなホスティングを提供(シングルポート設計)


## 設定について

automatic_scaling:
  target_cpu_utilization: 0.5
  max_instances: 1  # 最大インスタンス数を1に制限