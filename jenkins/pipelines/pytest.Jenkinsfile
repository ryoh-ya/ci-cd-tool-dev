pipeline {
    agent {
        docker {
            image 'python:3.11-slim' // gcloud コマンド用の公式イメージ
            args '-u root' // Dockerコンテナをrootユーザーで実行                    
        }
    }
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
                sh 'ls -l'                
            }
        }
        stage('Python Setup') {
            steps {
                script {
                    // 必要な依存関係をインストール
                    sh '''
                        pwd
                        pip install -r requirements.txt
                    '''
                }                    
            }
        }
        stage('Python Test') {
            steps {
                script {
                    sh '''
                        pwd
                        pytest --junitxml=pytest.xml --cov-report term-missing --cov=src tests/ | tee pytest-coverage.txt
                        # 既存のcoverage.svgを削除
                        if [ -f coverage.svg ]; then
                            rm coverage.svg
                        fi                        
                        coverage-badge -o coverage.svg
                    '''

                    // Pythonスクリプトを実行
                    sh '''
python - <<EOF
from src.lib.generate_coverage import GenerateCoverage
generate_coverage = GenerateCoverage()
generate_coverage.save_table()
EOF
                    '''
                }                    
            }
        }
        stage('Update Readme') {
            steps {
                script {
                    sh '''
                        echo "# Pytest Report" > README.md
                        echo "" >> README.md
                        echo "![test](coverage.svg)" >> README.md
                        echo "" >> README.md
                        cat tests/table.md >> README.md
                        cat README.md
                    '''
                }                    
            }
        }
        stage('Archive Artifacts') {
            steps {
                archiveArtifacts artifacts: 'README.md, coverage.svg', fingerprint: true
            }
        }

    }
}
