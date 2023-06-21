def call() {
    pipeline {

        agent {
            node {
                label 'workstation'
            }
        }
        options{
            ansiColor('xterm')
        }



        stages {
            stage('Code Quality') {
                steps {
                    //sh 'sonar-scanner -Dsonar.projectKey=${component} -Dsonar.host.url=http://172.31.94.107:9000 -Dsonar.login=admin -Dsonar.password=admin123 -Dsonar.qualitygate.wait=true'
                    sh 'echo code quality'
                }
            }

            stage('Unit Test Cases') {
                steps {
                    sh 'echo Unit tests'
                    //sh 'npm test'

                }
            }

            stage('CheckMarx SAST Scan') {
                steps {
                    sh 'echo Checkmarx Scan'
                }
            }

            stage('CheckMarx SCA Scan') {
                steps {
                    sh 'echo Checkmarx SCA Scan'
                }
            }
            stage('release application') {
                when {
                    expression {
                        env.TAG_NAME ==~ ".*"
                    }
                }
                steps {
                    sh 'npm install'
                    //create file VERSION
                    sh 'echo $TAG_NAME >VERSION'
                    //create zip with 3 files(node_modules,server.js,version)
                    sh 'zip -r ${component}-${TAG_NAME}.zip node_modules server.js VERSION'
                    sh 'curl -v -u admin:admin123 --upload-file ${component}-${TAG_NAME}.zip http://172.31.80.175:8081/repository/${component}/${component}-${TAG_NAME}.zip'
                }
            }

            }






        post {
            always {
                cleanWs()
            }
        }

    }
}