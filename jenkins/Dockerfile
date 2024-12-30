# Jenkinsの公式イメージをベースにする
FROM jenkins/jenkins:lts

USER root
RUN apt-get update 
RUN apt-get install -y vim wget
RUN rm -rf /var/lib/apt/lists/*


## install docker
RUN wget -O /tmp/docker.tgz https://download.docker.com/linux/static/stable/x86_64/docker-19.03.8.tgz
RUN tar -xzf /tmp/docker.tgz -C /tmp
RUN mv /tmp/docker/* /usr/bin/

## grant docker
ENV DOCKER_GROUP_GID 1001
RUN groupadd -o -g $DOCKER_GROUP_GID docker

RUN usermod -g docker jenkins

# ホストのdockerグループID (例: 1001) に合わせてグループを作成/変更
RUN groupadd -g 1001 docker || groupmod -g 1001 docker

# Jenkinsユーザーをdockerグループに追加
RUN usermod -aG docker jenkins

USER jenkins

# Jenkinsユーザーでシェルセッションを再起動
# RUN newgrp docker && echo "Jenkins user added to docker group"

# プライグインをインストールする
COPY plugins.txt /usr/share/jenkins/ref/plugins.txt
RUN jenkins-plugin-cli --plugin-file /usr/share/jenkins/ref/plugins.txt


# スクリプトをコピー
COPY --chown=jenkins:jenkins scripts /var/jenkins_home/scripts
COPY --chown=jenkins:jenkins pipelines /var/jenkins_home/pipelines
# CLIをイメージにコピーする
COPY --chown=jenkins:jenkins tools/jenkins-cli.jar /var/jenkins_home/jenkins-cli.jar

# init.groovy.dに初期設定するためのスクリプトをセット
COPY init.groovy.d /usr/share/jenkins/ref/init.groovy.d/

WORKDIR /var/jenkins_home

