#!/bin/bash
# 現在は使用していない


# JenkinsのURLを指定
JENKINS_URL=http://localhost:8080

# Jenkinsの起動を待機
echo "Waiting for Jenkins to be ready..."
while ! curl -s ${JENKINS_URL}/login > /dev/null; do
  sleep 5
done

# Jenkins CLIをダウンロード
echo "Downloading Jenkins CLI..."
curl -o /usr/share/jenkins/jenkins-cli.jar ${JENKINS_URL}/jnlpJars/jenkins-cli.jar
echo "Jenkins CLI downloaded."

# Jenkinsを通常通り起動
# $exec /usr/bin/jenkins.sh
