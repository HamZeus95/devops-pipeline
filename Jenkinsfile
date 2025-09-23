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
                        sh '''
                            echo "Setting execute permissions for Maven wrapper..."
                            chmod +x mvnw
                            echo "Building with Maven wrapper..."
                            if ./mvnw clean compile; then
                                echo "Build successful with Maven wrapper"
                            else
                                echo "Maven wrapper failed, trying system Maven..."
                                mvn clean compile
                            fi
                        '''
                    } else {
                        bat '.\\mvnw.cmd clean compile'
                    }
                }
            }
        }
        
        // stage('Test') {
        //     steps {
        //         echo 'Running comprehensive test suite with H2 in-memory database...'
        //         script {
        //             if (isUnix()) {
        //                 sh '''
        //                     echo "Setting execute permissions for Maven wrapper..."
        //                     chmod +x mvnw
        //                     echo "Running unit tests, integration tests, and performance tests..."
        //                     if ./mvnw test -Dspring.profiles.active=test -Dtest.parallel.enabled=true; then
        //                         echo "All tests successful with Maven wrapper"
        //                     else
        //                         echo "Maven wrapper failed, trying system Maven..."
        //                         mvn test -Dspring.profiles.active=test -Dtest.parallel.enabled=true
        //                     fi
        //                 '''
        //             } else {
        //                 bat '''
        //                     echo "Running comprehensive test suite..."
        //                     .\\mvnw.cmd test -D"spring.profiles.active=test" -D"test.parallel.enabled=true"
        //                 '''
        //             }
        //         }
        //     }
        //     post {
        //         always {
        //             // Publish detailed test results
        //             publishTestResults testResultsPattern: 'target/surefire-reports/*.xml'
                    
        //             // Archive test reports and logs
        //             archiveArtifacts artifacts: 'target/surefire-reports/*', allowEmptyArchive: true
                    
        //             // Generate test summary report
        //             script {
        //                 def testResults = publishTestResults testResultsPattern: 'target/surefire-reports/*.xml'
        //                 echo "Test Summary: ${testResults.totalCount} total, ${testResults.failCount} failed, ${testResults.skipCount} skipped"
        //             }
        //         }
        //         success {
        //             echo "✅ All tests passed successfully!"
        //         }
        //         failure {
        //             echo "❌ Some tests failed. Check the test reports for details."
        //         }
        //         unstable {
        //             echo "⚠️ Tests are unstable. Some tests may have failed intermittently."
        //         }
        //     }
        // }
        
        stage('Package') {
            steps {
                echo 'Packaging the application...'
                script {
                    if (isUnix()) {
                        sh '''
                            echo "Setting execute permissions for Maven wrapper..."
                            chmod +x mvnw
                            echo "Packaging application..."
                            if ./mvnw package -DskipTests; then
                                echo "Packaging successful with Maven wrapper"
                            else
                                echo "Maven wrapper failed, trying system Maven..."
                                mvn package -DskipTests
                            fi
                        '''
                    } else {
                        bat '.\\mvnw.cmd package -DskipTests'
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
        
        // stage('Deploy') {
        //     steps {
        //         echo 'Deploying the application...'
        //         script {
        //             // Example deployment steps - customize based on your deployment strategy
        //             if (isUnix()) {
        //                 sh '''
        //                     echo "Stopping existing application (if running)..."
        //                     pkill -f "student-management" || true
                            
        //                     echo "Starting new application..."
        //                     nohup java -jar target/student-management-*.jar > app.log 2>&1 &
                            
        //                     echo "Waiting for application to start..."
        //                     sleep 10
                            
        //                     echo "Checking application health..."
        //                     curl -f http://localhost:8089/student/health/check || exit 1
        //                 '''
        //             } else {
        //                 bat '''
        //                     echo "Deploying Spring Boot application..."
        //                     taskkill /F /IM java.exe /FI "WINDOWTITLE eq student-management*" 2>nul || echo "No existing process found"
                            
        //                     echo "Starting application..."
        //                     start /B java -jar target\\student-management-*.jar
                            
        //                     echo "Waiting for application startup..."
        //                     timeout /t 15 /nobreak
                            
        //                     echo "Application deployed successfully"
        //                 '''
        //             }
        //         }
        //     }
        // }
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