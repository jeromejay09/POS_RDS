pipeline {
    agent any

    environment {
        DOCKER_IMAGE = 'jeromejay09/demo-app:pos-1.0'
        SONARQUBE_TOKEN = credentials('sonarqube-token')  // SonarQube token for static analysis
        CHECKSTYLE_CONFIG = 'checkstyle.xml'  // Path to Checkstyle config file
        GPG_KEY = credentials('gpg-private-key')  // GPG private key for signing
        GPG_PASSPHRASE = credentials('gpg-passphrase')  // GPG passphrase
        AWS_SECRETS_MANAGER = 'dockerhub-credentials'  // Name of the AWS Secrets Manager secret
        DOCKER_CONTENT_TRUST = '1'  // Enable Docker Content Trust (DCT)
    }

    stages {
        stage('Run Ansible') {
            steps {
                sh 'cd Ansible && ansible-playbook -i localhost playbook.yaml, --connection=local'
            }
        }
        
        
        stage('Checkout') {
            steps {
                git 'https://github.com/jeromejay09/POS_RDS.git'
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
                    // Retrieve secrets from AWS Secrets Manager (e.g., Docker Hub credentials)
                    withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', credentialsId: 'aws-credentials']]) {
                        sh '''
                            # Retrieve the Docker Hub credentials from AWS Secrets Manager
                            DOCKER_USERNAME=$(aws secretsmanager get-secret-value --secret-id $AWS_SECRETS_MANAGER --query 'SecretString' --output text | jq -r .username)
                            DOCKER_PASSWORD=$(aws secretsmanager get-secret-value --secret-id $AWS_SECRETS_MANAGER --query 'SecretString' --output text | jq -r .password)
                        '''
                    }
                }
            }
        }

        stage('Static Code Analysis with SonarQube') {
            steps {
                script {
                    sh 'mvn sonar:sonar -Dsonar.host.url=3.27.135.42:9000 -Dsonar.login=$SONARQUBE_TOKEN'
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
                    
                    sh 'trivy fs --exit-code 1 --severity HIGH,CRITICAL .'
                }
            }
        }

        stage('Build Java App') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('GPG Sign the JAR File') {
            steps {
                script {
                    // Sign the JAR file using GPG
                    sh '''
                        export GPG_KEY_PATH="${WORKSPACE}/gpg/private.key"
                        echo "$GPG_KEY" > $GPG_KEY_PATH
                        gpg --import $GPG_KEY_PATH
                        gpg --batch --yes --passphrase "$GPG_PASSPHRASE" -o target/pos-system-1.0.0.jar -a --detach-sig target/pos-system-1.0.0.jar
                    '''
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                sh 'docker build -t $DOCKER_IMAGE .'
            }
        }

        stage('Trivy Security Check') {
            steps {
                // Run Trivy security scan on the built Docker image
                sh 'trivy image --exit-code 1 --severity HIGH,CRITICAL $DOCKER_IMAGE --username $DOCKER_USERNAME --password $DOCKER_PASSWORD'
            }
        }

        stage('Sign Docker Image') {
            steps {
                // This step is needed to sign the image using Docker Content Trust (DCT)
                sh """
                    docker trust sign $DOCKER_IMAGE
                """
            }
        }

        stage('Push Docker Image') {
            steps {
                withDockerRegistry([credentialsId: 'docker-hub', url: 'https://index.docker.io/v1/']) {
                    // Enable Docker Content Trust (DCT) to sign the image before pushing
                    sh 'DOCKER_CONTENT_TRUST=1 docker push $DOCKER_IMAGE'
                }
            }
        }

        stage('Create Pods and Services from the manifest file') {
            steps {
                sh 'aws eks update-kubeconfig --name my-cluster-eks --region ap-southeast-1'
                sh 'kubectl create -f pos-k8s.yaml'
                               
            }
        }


        
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
