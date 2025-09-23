pipeline {
    agent any
    
    tools {
        maven 'Maven'  
        jdk 'JDK'
    }
    
    environment {
        MAVEN_OPTS = '-Dmaven.repo.local=.m2/repository' // Use a local Maven repository in the workspace
    }

    stages {
        stage('Checkout') {
            steps {
                echo 'Checking out source code...'
                checkout scm
            }
        }
        
        stage('Build') {
            steps {
                echo 'Building the application...'
                script {
                    if (isUnix()) {
                        sh 'mvn clean compile'
                    } else {
                        bat 'mvn clean compile'
                    }
                }
            }
        }
        
        stage('Test') {
            steps {
                echo 'Running tests...'
                script {
                    if (isUnix()) {
                        sh 'mvn test'
                    } else {
                        bat 'mvn test'
                    }
                }
            }
            post {
                always {
                    // Publish test results
                    publishTestResults testResultsPattern: 'target/surefire-reports/*.xml'
                    
                    // Archive test reports
                    archiveArtifacts artifacts: 'target/surefire-reports/*', allowEmptyArchive: true
                }
            }
        }
        
        stage('Package') {
            steps {
                echo 'Packaging the application...'
                script {
                    if (isUnix()) {
                        sh 'mvn package -DskipTests'
                    } else {
                        bat 'mvn package -DskipTests'
                    }
                }
            }
            post {
                success {
                    // Archive the built artifacts
                    archiveArtifacts artifacts: 'target/*.jar', allowEmptyArchive: false
                }
            }
        }
        
        stage('Deploy') {
            steps {
                echo 'Deploying the application...'
                script {
                    // Example deployment steps - customize based on your deployment strategy
                    if (isUnix()) {
                        sh '''
                            echo "Stopping existing application (if running)..."
                            pkill -f "student-management" || true
                            
                            echo "Starting new application..."
                            nohup java -jar target/student-management-*.jar > app.log 2>&1 &
                            
                            echo "Waiting for application to start..."
                            sleep 10
                            
                            echo "Checking application health..."
                            curl -f http://localhost:8089/student/health/check || exit 1
                        '''
                    } else {
                        bat '''
                            echo "Deploying Spring Boot application..."
                            taskkill /F /IM java.exe /FI "WINDOWTITLE eq student-management*" 2>nul || echo "No existing process found"
                            
                            echo "Starting application..."
                            start /B java -jar target\\student-management-*.jar
                            
                            echo "Waiting for application startup..."
                            timeout /t 15 /nobreak
                            
                            echo "Application deployed successfully"
                        '''
                    }
                }
            }
        }
    }

    post {
        success {
            echo "Congratulations! The pipeline completed successfully. 🎊 "
            emailext (
                subject: "✅ SUCCESS: Student Management Pipeline - Build #${BUILD_NUMBER}",
                body: """
                <html>
                <body>
                    <h2 style="color: green;">🎉 Pipeline Execution Successful!</h2>
                    
                    <h3>Build Information:</h3>
                    <ul>
                        <li><strong>Project:</strong> Student Management Application</li>
                        <li><strong>Build Number:</strong> ${BUILD_NUMBER}</li>
                        <li><strong>Build Duration:</strong> ${BUILD_DURATION_STRING}</li>
                        <li><strong>Build URL:</strong> <a href="${BUILD_URL}">${BUILD_URL}</a></li>
                        <li><strong>Git Branch:</strong> ${BRANCH_NAME}</li>
                        <li><strong>Git Commit:</strong> ${GIT_COMMIT}</li>
                    </ul>
                    
                    <h3>Stages Completed:</h3>
                    <ul>
                        <li>✅ Checkout</li>
                        <li>✅ Build</li>
                        <li>✅ Test</li>
                        <li>✅ Package</li>
                        <li>✅ Deploy</li>
                    </ul>
                    
                    <h3>Next Steps:</h3>
                    <p>The Student Management application has been successfully deployed and is ready for use.</p>
                    
                    <p><em>This is an automated message from Jenkins CI/CD Pipeline.</em></p>
                </body>
                </html>
                """,
                mimeType: 'text/html',
                to: "${env.CHANGE_AUTHOR_EMAIL ?: 'benali.hamza@esprit.tn'}",
                attachLog: true
            )
        }
        
        failure {
            echo "Oops! The pipeline failed. ❌"
            emailext (
                subject: "❌ FAILURE: Student Management Pipeline - Build #${BUILD_NUMBER}",
                body: """
                <html>
                <body>
                    <h2 style="color: red;">🚨 Pipeline Execution Failed!</h2>
                    
                    <h3>Build Information:</h3>
                    <ul>
                        <li><strong>Project:</strong> Student Management Application</li>
                        <li><strong>Build Number:</strong> ${BUILD_NUMBER}</li>
                        <li><strong>Build Duration:</strong> ${BUILD_DURATION_STRING}</li>
                        <li><strong>Build URL:</strong> <a href="${BUILD_URL}">${BUILD_URL}</a></li>
                        <li><strong>Console Output:</strong> <a href="${BUILD_URL}console">${BUILD_URL}console</a></li>
                        <li><strong>Git Branch:</strong> ${BRANCH_NAME}</li>
                        <li><strong>Git Commit:</strong> ${GIT_COMMIT}</li>
                    </ul>
                    
                    <h3>Failure Details:</h3>
                    <p><strong>Failed Stage:</strong> Check the console output for detailed error information.</p>
                    <p><strong>Error Summary:</strong> ${BUILD_LOG_EXCERPT}</p>
                    
                    <h3>Recommended Actions:</h3>
                    <ul>
                        <li>🔍 Check the console output for detailed error messages</li>
                        <li>🧪 Run tests locally to reproduce the issue</li>
                        <li>📋 Review recent commits for potential breaking changes</li>
                        <li>🔄 Fix the issues and trigger a new build</li>
                    </ul>
                    
                    <h3>Support:</h3>
                    <p>If you need assistance, please contact the DevOps team or check the project documentation.</p>
                    
                    <p><em>This is an automated message from Jenkins CI/CD Pipeline.</em></p>
                </body>
                </html>
                """,
                mimeType: 'text/html',
                to: "${env.CHANGE_AUTHOR_EMAIL ?: 'benali.hamza@esprit.tn'}",
                attachLog: true
            )
        }
    }
}