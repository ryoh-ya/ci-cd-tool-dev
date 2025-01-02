pipeline {
    agent any
    stages {
        stage('Build') {
            steps {
                echo 'Building...'
            }
        }
        stage('Test') {
            steps {
                script {
                    sh 'exit 1' // テスト用にエラーを発生させるコマンド
                }
            }
        }
        stage('Deploy') {
            steps {
                echo 'Deploying...'
            }
        }
    }
    post {
        failure {
            // ジョブ全体が失敗した場合の通知
            emailext(
                to: 'your_email@example.com',
                subject: "Pipeline Failed: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                body: "Pipeline failed. Check details at: ${env.BUILD_URL}"
            )
        }
    }
}