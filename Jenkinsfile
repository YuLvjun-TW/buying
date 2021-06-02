stages {
        stage('Build') {
            steps {
                script {
                    result = sh (script: "git log -1|grep 'Release'", returnStatus: true)
                    echo "result: ${result}"
                }
            }
        }
    }