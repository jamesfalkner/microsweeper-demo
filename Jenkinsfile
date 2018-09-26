pipeline {
    agent {
        label 'maven'
    }
    stages {
        stage('Run Tests') {
            steps {
                sh "cp .settings.xml ~/.m2/settings.xml"
                sh "echo done"
            }
        }
        stage('Build App') {
            steps {
                sh "cp .settings.xml ~/.m2/settings.xml"
                sh "mvn clean package -Popenshift -DskipTests"
            }
        }
        stage('Build Container') {
            steps {
                script {
                    openshift.withCluster() {
                        openshift.startBuild("microsweeper", "--from-file=target/demo-thorntail.jar", "--wait")
                    }
                }
            }
        }
        stage('Deploy to Prod') {
            steps {
                script {
                    openshift.withCluster() {
                        def result, dc = openshift.selector("dc", "microsweeper")
                        dc.rollout().latest()
                        timeout(10) {
                            result = dc.rollout().status("-w")
                        }
                        if (result.status != 0) {
                            error(result.err)
                        }
                    }
                }
            }
        }
    }
}