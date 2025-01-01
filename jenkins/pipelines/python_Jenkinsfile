pipeline {
    options {
        disableConcurrentBuilds() // 並行ビルドを無効化
    }
    agent any
    stages {
        stage('Copy Local Source') {
            // 現在のワークスペースのソースを取得する
            steps {
                script {                
                    // ローカルディレクトリからJenkinsのワークスペースにコピー
                    def localSourcePath = '/var/jenkins_home/source'
                    def workspacePath = pwd()
                    sh "cp -r ${localSourcePath}/* ${workspacePath}/"
                }
                // 現在のディレクトリのパスを表示
                sh 'pwd'
                // 現在のディレクトリの内容をリスト表示
                sh 'ls -l'                
            }
        }
        stage('Python Script') {
            agent {
                docker {
                    image 'python:3.11-slim' // gcloud コマンド用の公式イメージ
                    args '-u root' // Dockerコンテナをrootユーザーで実行                    
                }
            }
            steps {
                script {
                    // 必要な依存関係をインストール
                    sh '''
                        python -m pip install --upgrade pip
                        pip install requests
                    '''
                    
                    // Pythonスクリプトを実行
                    sh '''
python - <<EOF
import requests

url = "https://example.com"
response = requests.get(url)

if response.status_code == 200:
    print("API call succeeded")
else:
    print(f"API call failed with status code: {response.status_code}")
    raise Exception("API call failed")
EOF
                    '''
                }                
            }            
        }
        stage('Python File Start') {
            agent {
                docker {
                    image 'python:3.11-slim' // gcloud コマンド用の公式イメージ
                    args '-u root' // Dockerコンテナをrootユーザーで実行                    
                }
            }
            steps {
                sh '''
                cd /var/jenkins_home/workspace/debug-pipeline/src
                python main.py
                '''
            }
        }
    }
}
