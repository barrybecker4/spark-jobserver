#!groovy

/**
 * PATH requirements: sbt, curl, jdk8  (configure in jenkins node)
 */
pipeline {
    agent {
        label 'linux-node'
    }
    options {
        buildDiscarder(logRotator(numToKeepStr: '7')) // keep only recent builds
    }
    triggers {
        pollSCM('H/15 * * * *')
        //upstream(upstreamProjects: "<comma delimited list>", threshold: hudson.model.Result.SUCCESS)
    }

    stages {
        stage('Checkout sources') {
            steps {
                checkout scm
            }
        }

        stage ('build') {
            steps {
                echo "PATH is: $PATH"
                cmd("sbt assembly")
            }
        }

        stage ('test') {
            steps {
                echo "PATH is: $PATH"
                cmd("sbt test")
            }
        }
        stage('documentation') {
            steps {
                cmd("sbt doc")
            }
        }

        stage('deploy') {
            steps {
                cmd("sbt publish")
                script {
                    currentBuild.result = 'SUCCESS'
                }
            }
        }
    }

    post {
        always {
            step([$class: 'Mailer',
                  notifyEveryUnstableBuild: true,
                  recipients: env.EMAIL_NOTIFICATION_LIST,
                  sendToIndividuals: true])
            step([$class: 'JavadocArchiver', javadocDir: 'target/scala-2.11/api', keepAll: true])
            junit "target/test-reports/*.xml"
        }
        failure {
            echo 'This build FAILED!'
            script {
                currentBuild.result = 'FAILURE'
            }
        }
        unstable {
            echo 'This build is unstable.'
        }
    }
}

def cmd(cmd) {
    if (isUnix()) {
        sh "${cmd}"
    } else {
        bat "${cmd}"
    }
}