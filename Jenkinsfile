pipeline {
    agent any

    environment {
        
        DOCKER_IMAGE = 'jeromejay09/demo-app:pos-1.0'
        SONARQUBE_TOKEN = credentials('sonarqube-token')  // SonarQube token for static analysis
        CHECKSTYLE_CONFIG = 'checkstyle.xml'  // Path to Checkstyle config file
        GPG_KEY = credentials('gpg-private-key')  // GPG private key for signing
        GPG_PASSPHRASE = credentials('gpg-passphrase')  // GPG passphrase
        AWS_SECRETS_MANAGER = 'docker-registry-credentials'  // Name of the AWS Secrets Manager secret
        DOCKER_CONTENT_TRUST = '1'  // Enable Docker Content Trust (DCT)
    }

    stages {
        stage('Run Ansible') {
            steps {
                sh '''
                    cd Ansible
                    ansible-playbook -i localhost, playbook.yaml --connection=local
                '''
            }
        }
        
        
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/jeromejay09/POS_RDS.git'
            }
        }

        stage('Scan for Sensitive Data with git-secrets') {
            steps {
                script {
                    // Run git-secrets to scan for any secrets
                    sh 'git secrets --scan'
                }
            }
        }

        stage('Retrieve AWS Secrets') {
            steps {
                script {
                    withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', credentialsId: 'aws-credentials']]) {
                        // Retrieve the Docker Hub credentials from AWS Secrets Manager
                        def dockerCredentials = sh(script: '''
                            aws secretsmanager get-secret-value --secret-id docker-registry-credentials --query 'SecretString' --region ap-southeast-2 --output text
                        ''', returnStdout: true).trim()
        
                        // Parse the credentials from the JSON response
                        def username = sh(script: "echo '${dockerCredentials}' | jq -r .username", returnStdout: true).trim()
                        def password = sh(script: "echo '${dockerCredentials}' | jq -r .password", returnStdout: true).trim()
        
                        // Set the environment variables for use in later stages
                        env.DOCKER_USERNAME = username
                        env.DOCKER_PASSWORD = password
                    }
                }
            }
        }

        stage('Retrieve Docker Signing Key') {
            steps {
                script {
                    withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', credentialsId: 'aws-credentials']]) {
                        // Retrieve the private key from AWS Secrets Manager
                        def privateKey = sh(script: '''
                            aws secretsmanager get-secret-value --secret-id docker-signing-private-key2 --query 'SecretString' --region ap-southeast-2 --output text
                        ''', returnStdout: true).trim()
        
                        // You can then store this in a file for Docker to use, if needed
                        writeFile(file: 'docker-signing-private.key', text: privateKey)
                        // Set permissions on the private key file to be readable only by the owner
                        sh 'chmod 600 docker-signing-private.key'
                        
                        // Set an environment variable to reference the key if needed
                        env.DOCKER_SIGNING_PRIVATE_KEY_PATH = "${WORKSPACE}/docker-signing-private.key"
                    }
                }
            }
        }



        stage('Static Code Analysis with SonarQube') {
            steps {
                script {
                    sh '''
                        mvn clean install
                        mvn sonar:sonar \
                            -Dsonar.host.url=http://54.252.200.26:9000 \
                            -Dsonar.login=$SONARQUBE_TOKEN \
                            -Dsonar.java.binaries=target/classes
                    '''
                }
            }
        }


        stage('Code Quality with Checkstyle') {
            steps {
                script {
                    // Run Checkstyle for Java code to enforce coding standards
                    sh 'mvn checkstyle:checkstyle'
                }
            }
        }

        stage('Dependency Scanning with Trivy') {
            steps {
                script {
                    
                    sh 'trivy fs --exit-code 0 --severity HIGH,CRITICAL .'
                }
            }
        }

        stage('Build Java App') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Setup GPG Key and Sign') {
            steps {
                // Use the Secret File credential containing the GPG private key
                withCredentials([file(credentialsId: 'gpg-private-key', variable: 'GPG_PRIVATE_KEY'),
                         string(credentialsId: 'gpg-passphrase', variable: 'GPG_PASSPHRASE')]) {
                    script {
                        // The GPG key will be temporarily available at $GPG_PRIVATE_KEY
                        // If you need to use it directly, you can pass it to the gpg command
                        sh '''
                            mkdir -p $WORKSPACE/gpg
                            sudo chown -R jenkins:jenkins $WORKSPACE/gpg
                            sudo chmod -R 755 $WORKSPACE/gpg
                            cp $GPG_PRIVATE_KEY $WORKSPACE/gpg/private.key
                            # Start GPG agent and configure environment
                            gpgconf --kill all
                            gpgconf --launch gpg-agent
                            export GPG_TTY=$(tty)
                            export GPG_KEY_PATH=$WORKSPACE/gpg/private.key
                            export GPG_PASSPHRASE="$GPG_PASSPHRASE"
                           gpg --batch --yes --pinentry-mode loopback --passphrase "$GPG_PASSPHRASE" --import $WORKSPACE/gpg/private.key
                    
                            # Now use the GPG key to sign your file
                            gpg --batch --yes --pinentry-mode loopback --passphrase "$GPG_PASSPHRASE" -o target/pos-system-1.0.0.jar -a --detach-sig target/pos-system-1.0.0.jar
                        '''
                    }
                }
            }
        }


        

        stage('Build Docker Image') {
            steps {
                sh """
                sudo chmod 777 /var/run/docker.sock
                docker build -t $DOCKER_IMAGE .
                """
            }
        }

        stage('Trivy Security Check') {
            steps {
                // Run Trivy security scan on the built Docker image
                sh """
                    trivy image --exit-code 0 --severity HIGH,CRITICAL ${env.DOCKER_IMAGE} --username ${env.DOCKER_USERNAME} --password ${env.DOCKER_PASSWORD}
                """
            }
        }

       stage('Sign Docker Image') {
            steps {
                withCredentials([file(credentialsId: 'gpg-private-key', variable: 'GPG_PRIVATE_KEY'),
                                 usernamePassword(credentialsId: 'docker-hub', usernameVariable: 'DOCKER_USERNAME', passwordVariable: 'DOCKER_PASSWORD')]) {
                    script {
                        // Import the GPG key
                        sh "gpg --import $GPG_PRIVATE_KEY"
                
                        // Optionally, export the GPG private key to ensure it is used by Docker
                        sh '''
                            export GPG_KEY=$(gpg --list-secret-keys --keyid-format LONG | grep '^sec' | awk '{print \\$2}' | sed 's/\\/.*//')
                            echo "GPG key ID: \${GPG_KEY}"
                        '''
                
                        // r Hub login
                        sh "echo \$DOCKER_PASSWORD | docker login --username \$DOCKER_USERNAME --password-stdin"
                    }
                
                    // Save, sign, and push the image
                    sh """
                        docker save $DOCKER_IMAGE -o image.tar
                        echo "$GPG_PASSPHRASE" | gpg --batch --yes --pinentry-mode loopback --passphrase-fd 0 --detach-sign --output image.tar.sig image.tar
                        docker load < image.tar
                        docker trust sign $DOCKER_IMAGE --key $GPG_KEY
                        docker push $DOCKER_IMAGE
                        aws s3 cp image.tar.sig s3://pos-system-bucket2/image.tar.sig
                    """
                }
            }
        }




//        stage('Sign Docker Image') {
//            steps {
//                // This step is needed to sign the image using Docker Content Trust (DCT)
//                sh """
//                    export DOCKER_CONTENT_TRUST=1
//                    docker trust sign $DOCKER_IMAGE
//                """
//            }
//        }

//        stage('Push Docker Image') {
//            steps {
//                withDockerRegistry([credentialsId: 'docker-hub', url: 'https://index.docker.io/v1/']) {
//                    // Enable Docker Content Trust (DCT) to sign the image before pushing
//                    sh """
 //                   export DOCKER_CONTENT_TRUST=0
//                    docker push $DOCKER_IMAGE
 //                   """
     //           }
 ///           }
 //       }

    //    stage('Create Pods and Services from the manifest file') {
    //        steps {
     //           sh 'aws eks update-kubeconfig --name my-cluster-eks --region ap-southeast-1'
    //            sh 'kubectl create -f pos-k8s.yaml'
                               
    //        }
    //    }


        
    }

    post {
        success {
            echo 'Pipeline completed successfully!'
        }
        failure {
            echo 'Pipeline failed!'
        }
    }
}
