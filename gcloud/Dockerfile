# ベースイメージとして Google Cloud SDK を使用
FROM google/cloud-sdk:latest

# 必要なパッケージをインストール
RUN apt-get update 
RUN apt-get install -y docker.io 
# aptによりcloud-build-localをインストールする
RUN apt-get install -y google-cloud-cli-cloud-build-local 
RUN apt-get clean

# Cloud Build Local をインストール
# Google Cloud CLI が apt-get または yum などのパッケージマネージャーを使用してインストールされており、
# gcloud components コマンドでコンポーネントをインストールできない場合にエラーが発生します。なので以下をコメントアウトする
# RUN gcloud components install cloud-build-local

# 作業ディレクトリを設定
WORKDIR /workspace

# コンテナのデフォルトエントリポイント
CMD ["/bin/bash"]