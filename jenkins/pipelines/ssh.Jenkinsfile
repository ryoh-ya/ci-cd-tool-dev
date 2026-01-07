pipeline {
    agent any
    stages {
        stage('setup') {
            steps {
                script {
                    withCredentials([string(credentialsId: 'ssh-host-id', variable: 'REMOTE_HOST')]) {
                        // SSH認証情報からユーザー名を取得して環境変数に設定
                        echo "Connecting to host: $REMOTE_HOST"
                        withCredentials([sshUserPrivateKey(credentialsId: 'ssl-cert-id', keyFileVariable: 'SSH_KEY', usernameVariable: 'SSH_USER')]) {
                            // 環境変数として設定
                            env.SSH_COMMAND = "ssh -o StrictHostKeyChecking=no -i $SSH_KEY $SSH_USER@$REMOTE_HOST"

                            // SSH接続を実行
                            sh '''
$SSH_COMMAND <<EOF
if ! sudo systemctl is-active --quiet rocketchat; then
    echo "Rocket.Chat is not running. Restarting..."
    sudo systemctl restart rocketchat
    if sudo systemctl is-active --quiet rocketchat; then
    echo "Rocket.Chat restarted successfully."
    else
    echo "Failed to restart Rocket.Chat."
    exit 1
    fi
else
    echo "Rocket.Chat is running fine."
fi
EOF
                            '''                        
                        }
                    }
                }
            }
        }
    }
}
