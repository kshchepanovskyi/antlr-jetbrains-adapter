pipeline {
    agent any
    tools {
        jdk 'JDK 8'
    }
    options {
        ansiColor('xterm')
        timestamps()
        timeout(time: 30, unit: 'MINUTES')
    }
    stages {
        stage('Set Version') {
            steps {
                sh '''                    
                    if [[ "$BRANCH_NAME" =~ ^(release/).*$ ]]; then
                      VERSION=$(cat release.txt).${BUILD_ID}
                    elif [[ "$BRANCH_NAME" =~ ^(master).*$ ]]; then
                      VERSION=$(cat release.txt).alpha.${BUILD_ID}                                                              
                    else
                      VERSION=$(cat release.txt).snapshot.$(git rev-parse HEAD)
                    fi
                    sed -i "s/^version.*/version=${VERSION}/" gradle.properties
                    echo ${VERSION} > version.txt                 
                '''
            }
        }
        stage('Build') {
            steps {
                withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: 'BINTRAY_API_KEY',
                                  usernameVariable: 'BINTRAY_USER', passwordVariable: 'BINTRAY_API_KEY']]) {
                    sshagent(['GITHUB_SSH_KEY']) {
                        sh '''
                            sh ./gradlew clean build bintrayUpload                                                           
                        '''
                    }
                    sh '''
                        curl -X POST -u${BINTRAY_USER}:${BINTRAY_API_KEY} https://api.bintray.com/content/antlr/maven/antlr4-jetbrains-adapter/$(cat version.txt)/publish
                    '''
                }
            }
            post {
                always {
                    junit '**/build/test-results/**/*.xml'
                }
            }
        }
    }
    post {
        failure {
            emailext(
                    mimeType: 'text/html',
                    body: '${JELLY_SCRIPT,template="html"}',
                    recipientProviders: [
                            [$class: 'CulpritsRecipientProvider'],
                            [$class: 'DevelopersRecipientProvider'],
                            [$class: 'RequesterRecipientProvider']
                    ],
                    subject: 'Build failed - ${JOB_NAME}'
            )
        }
        unstable {
            emailext(
                    mimeType: 'text/html',
                    body: '${JELLY_SCRIPT,template="html"}',
                    recipientProviders: [
                            [$class: 'CulpritsRecipientProvider'],
                            [$class: 'DevelopersRecipientProvider'],
                            [$class: 'RequesterRecipientProvider']
                    ],
                    subject: 'Build unstable - ${JOB_NAME}'
            )
        }
    }
}