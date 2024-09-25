## 1. Jenkins Credential 등록
1. Jenkins 에서 SVN에 연동될 계정정보를 등록한다.
2. 플러그인을 설치하는 방법도 있지만 docker 상에 접속해서 subversion을 설치해도 됨.
3. maven 명령어를 실행할 수 있도록 전역 plugin을 설정한다.
    - 이것 또한, docker에 접근해서 apt-getinstall maven 명령어를 통해서 설치할 수 있음


## 2. 스크립트 작성 예시
```groovy
[ Groovy Script Sample.groovy ]

pipeline {  // 파이프라인
    agent any   // 어떤 Jenkins에 일을 시킬 것인가

    stages {    // 파이프라인의 작업들
        
        stage('Prepare'){   // 첫번째 준비 작업
            steps{
                script{
                    env.ymd = sh (returnStdout: true, script: ''' echo `date '+%Y%m%d-%H%M%S'` ''')
                }
                echo("params : ${env.ymd} " + params.tag)
            }
                
        }
        
        stage('checkout') {
            steps {
                echo "-=- CheckOut project Start -=-"
                withCredentials([[$class: 'UsernamePasswordMultiBinding',
                //전역으로 설정된 credential 아이디
                credentialsId: 'c887e4f9-b6b0-4127-9c6a-0bbef0547203',
                usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD']]) {
                    sh "pwd"
			        sh "svn co svn://192.168.0.4/SMD3100 --username $USERNAME --password $PASSWORD ./"

                }
                echo "-=- CheckOut project  End -=-"
            }
        }

		// SonarQube와 연동된 상태일 경우        
        stage('Sonar-Qube'){
             steps{
                 script{
                     echo "-=- Sonar Qube Analysis project Start -=-"
                     sh 'pwd'
                     sh 'ls -al'
                     sh "mvn clean -DskipTests verify sonar:sonar -Dsonar.projectKey=OpenADR  -Dsonar.projectName='OpenADR ' -Dsonar.host.url=http://192.168.0.126:9093 -Dsonar.token=sqp_806a3ff7517f7c7c64b2e1ace70c7c40d2049a45 -Dsonar.svn.username=ybwhyb -Dsonar.svn.password.secured=itman1234"
                     echo "-=- Sonar Qube Analysis project End -=-"
                 }
            }

        }

        stage('Compile'){
             steps{
                 script{
                        echo "-=- Compiling project Start -=-"
                        // docker 접속해서 checkout한 프로젝트의 위치
                        sh 'cd /var/jenkins_home/workspace/'
                        sh 'pwd'
                        sh 'mvn clean install' 
                        echo "-=- Compiling project End -=-"
                }
            }
        }

        // deploy 원격지에 소스를 배포
        stage("deploy"){
            steps{
                script{
                    echo "-=- Deploy Process Start -=-"
                    // sh 'mv ./target/SMD3100.jar ./Oadr.jar'
                    // sh 'scp ./Oadr.jar jysim@192.168.0.8:/home/'
                    // sh 'java -jar Oadr.jar'
                    // 원격지에 배포하는 코드 ssh설정이 필요하다.
                    sh 'scp /var/jenkins_home/jobs/Smd3100/workspace/target/SMD3100.jar itman@192.168.0.7:/home/'
                    echo "-=- Deploy Process End -=-"
                }
            }
        }
        
        
    }
    
}
```
