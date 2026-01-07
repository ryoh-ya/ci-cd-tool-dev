pipeline {    
    stages {
        stage('Checkout') {
            steps {
                // ワークスペースを完全に削除
                deleteDir()    
                git(
                    branch: 'master',
                    url: 'https://appj.pglikers.com/gitbucket/git/oss/keycloak-on-fly.git',
                    credentialsId: 'gitbucket-id' 
                )
                // チェックアウト結果を確認
                sh '''
                echo "Checking workspace content:"
                ls -la
                if [ -f "README.md" ]; then
                    echo "README.md exists."
                    docker build -t gcr.io/gcp-devel-project/flyctl:latest .
                else
                    echo "README.md not found!"
                    exit 1
                fi
                '''
            }
        }
        stage('Push Docker Image to GCR') {
            agent {
                docker {
                    image 'google/cloud-sdk:latest' // gcloud コマンド用の公式イメージ
                    args '-v /var/run/docker.sock:/var/run/docker.sock'
                }
            }
            environment {
                GOOGLE_APPLICATION_CREDENTIALS = credentials('gcloud-key') // GCP サービスアカウント JSONキー
            }
            steps {
                sh '''
                # GCP 認証
                gcloud auth activate-service-account --key-file=$GOOGLE_APPLICATION_CREDENTIALS
                gcloud config set project gcp-devel-project
                gcloud artifacts repositories list --location=us


                # Docker 認証情報を構成
                # gcloud auth configure-docker

                # Docker イメージを GCR にプッシュ
                # docker push gcr.io/gcp-devel-project/flyctl:latest
                '''
            }
        }        
    }
}
