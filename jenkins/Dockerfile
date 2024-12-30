# Jenkinsの公式イメージをベースにする
FROM jenkins/jenkins:lts

USER root
RUN apt-get update 
RUN apt-get install -y vim 
RUN rm -rf /var/lib/apt/lists/*

USER jenkins

# スクリプトをコピー
COPY --chown=jenkins:jenkins scripts /var/jenkins_home/scripts
# CLIをイメージにコピーする
COPY --chown=jenkins:jenkins tools/jenkins-cli.jar /var/jenkins_home/jenkins-cli.jar

# init.groovy.dに初期設定するためのスクリプトをセット
COPY init.groovy.d /usr/share/jenkins/ref/init.groovy.d/

WORKDIR /var/jenkins_home