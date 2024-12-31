# ベースイメージを選択 (Debianの軽量版を使用)
FROM debian:bullseye-slim

# 必要なパッケージをインストール
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
    curl \
    git \
    ca-certificates \
    docker.io \
    && apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# actのバイナリをインストール
RUN curl -L -o /tmp/act.tar.gz https://github.com/nektos/act/releases/download/v0.2.70/act_Linux_x86_64.tar.gz && \
    tar -xzf /tmp/act.tar.gz -C /usr/local/bin && \
    chmod +x /usr/local/bin/act && \
    rm /tmp/act.tar.gz

# デフォルトの作業ディレクトリを設定
WORKDIR /github/workspace
RUN git init
RUN touch README.md
RUN git add .
RUN git config --global user.email "you@example.com"
RUN git config --global user.name "Your Name"
RUN git commit -m "Initial commit for act testing"

# ENTRYPOINTを設定 (actをデフォルトで実行可能にする)
# ENTRYPOINT ["act"]