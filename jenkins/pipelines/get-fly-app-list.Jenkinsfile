pipeline {
    agent {
        docker {
            image 'gcr.io/gcp-devel-project/flyctl' 
        }
    }
    stages {
        stage('Build') {
            steps {
                script {
                    withCredentials([string(credentialsId: 'fly-access-token', variable: 'FLY_ACCESS_TOKEN')]) {
                        echo 'Building...'
                        sh "flyctl apps list -t $FLY_ACCESS_TOKEN"
                    }
                }
            }
        }
    }
}