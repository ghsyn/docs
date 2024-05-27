pipeline {
    agent any

    stages {
        
        stage('Prepare') {
            steps {
                script {
                    env.ymd = sh (returnStdout: true, script: ''' echo `date '+%Y%m%d-%H%M%S'` ''')
                }
                echo("params : ${env.ymd} " + params.tag)
            }
        }
        
        stage('checkout') {
            steps {
                echo "-=- CheckOut project Start -=-"
                withCredentials([[$class: 'UsernamePasswordMultiBinding',
                credentialsId: 'c887e4f9-b6b0-4127-9c6a-0bbef0547203',
                usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD']]) {
                    sh "pwd"
			        sh "svn co svn://192.168.0.4/SMD3100 --username $USERNAME --password $PASSWORD ./"

                }
                echo "-=- CheckOut project  End -=-"
            }
        }
        

        // stage('Sonar-Qube') {
        //      steps {
        //          script {
        //              echo "-=- Sonar Qube Analysis project Start -=-"
        //              sh 'pwd'
        //              sh 'ls -al'
        //              sh "mvn clean -DskipTests verify sonar:sonar -Dsonar.projectKey=OpenADR  -Dsonar.projectName='OpenADR ' -Dsonar.host.url=http://192.168.0.126:9093 -Dsonar.token=sqp_806a3ff7517f7c7c64b2e1ace70c7c40d2049a45 -Dsonar.svn.username=ybwhyb -Dsonar.svn.password.secured=itman1234"
        //              echo "-=- Sonar Qube Analysis project End -=-"
        //          }
        //     }
        // }
        
        stage('Compile') {
             steps {
                 script {
                        echo "-=- Compiling project Start -=-"
                        sh 'cd /var/jenkins_home/workspace/'
                        sh 'pwd'
                        sh 'mvn clean install -P prod'
                        echo "-=- Compiling project End -=-"
                }
            }
        }
         
        stage("deploy") {
            steps {
                script {
                    echo "-=- Deploy Process Start -=-"
                    //sh 'mv ./target/SMD3100.jar ./Oadr.jar'
                    //sh 'scp ./Oadr.jar jysim@192.168.0.8:/home/'
                    //sh 'java -jar Oadr.jar'
                    sh 'scp /var/jenkins_home/jobs/Smd3100/workspace/target/SMD3100.jar itman@192.168.0.7:/home/'
                    echo "-=여기까지는 옴=-"
                    echo "-=- Deploy Process End -=-"
                }
            }
        }
    }
}
