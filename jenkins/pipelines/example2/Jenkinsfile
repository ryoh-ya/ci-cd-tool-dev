pipeline {
    agent {
        docker {
            image 'node:18-alpine' // 使用する Docker イメージ
        }
    }
    stages {
        stage('Build') {
            steps {
                sh 'node -v' // Node.js のバージョン確認
            }
        }
    }
}