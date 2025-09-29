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
        
        stage('Test') {
            steps {
                echo 'Running basic test suite...'
                script {
                    if (isUnix()) {
                        sh '''
                            echo "Setting execute permissions for Maven wrapper..."
                            chmod +x mvnw
                            echo "Running unit tests..."
                            if ./mvnw test; then
                                echo "Tests successful with Maven wrapper"
                            else
                                echo "Maven wrapper failed, trying system Maven..."
                                mvn test
                            fi
                        '''
                    } else {
                        bat '''
                            echo "Running basic test suite..."
                            .\\mvnw.cmd test
                        '''
                    }
                }
            }
            post {
                always {
                    // Publish test results using junit step
                    junit testResults: 'target/surefire-reports/*.xml', allowEmptyResults: true
                    
                    // Archive test reports
                    archiveArtifacts artifacts: 'target/surefire-reports/*', allowEmptyArchive: true
                }
                success {
                    echo "✅ All tests passed successfully!"
                }
                failure {
                    echo "❌ Some tests failed. Check the test reports for details."
                }
                unstable {
                    echo "⚠️ Tests are unstable. Some tests may have failed intermittently."
                }
            }
        }
        
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
                    echo "📦 JAR file packaged and archived successfully!"
                }
            }
        }
stage('Docker Build & Push') {
  steps {
    script {
      def repo = "benali.hamza/devops"
      def tag = "${env.BUILD_NUMBER}"

      withCredentials([usernamePassword(credentialsId: 'docker', usernameVariable: 'DOCKERHUB_USER', passwordVariable: 'DOCKERHUB_PASS')]) {
        echo 'Building and pushing Docker image...'
        sh """
          echo "\$DOCKERHUB_PASS" | docker login -u "\$DOCKERHUB_USER" --password-stdin
          docker build -t ${repo}:${tag} .
          docker push ${repo}:${tag}
          docker tag ${repo}:${tag} ${repo}:latest
          docker push ${repo}:latest
        """
      }
    }
  }
}





        
        // stage('Deploy') {
        //     steps {
        //         echo 'Deploying the application...'
        //         script {
        //             if (isUnix()) {
        //                 sh '''
        //                     echo "Stopping existing application (if running)..."
        //                     pkill -f "student-management" || true
                            
        //                     echo "Creating data directory for H2 database..."
        //                     mkdir -p data
                            
        //                     echo "Starting new application in background with production profile..."
        //                     nohup java -jar -Dspring.profiles.active=prod target/student-management-*.jar > app.log 2>&1 &
                            
        //                     echo "Waiting for application to start..."
        //                     sleep 20
                            
        //                     echo "Checking if application is running..."
        //                     if pgrep -f "student-management"; then
        //                         echo "✅ Application started successfully!"
        //                         echo "🌐 Application URL: http://localhost:8089/student"
        //                         echo "🗄️ H2 Console: http://localhost:8089/student/h2-console (if enabled)"
        //                     else
        //                         echo "⚠️ Application may not have started properly. Check app.log"
        //                         tail -30 app.log || echo "No log file found"
        //                     fi
        //                 '''
        //             } else {
        //                 bat '''
        //                     echo "Stopping existing application (if running)..."
        //                     taskkill /F /IM java.exe /FI "COMMANDLINE eq *student-management*" 2>nul || echo "No existing process found"
                            
        //                     echo "Creating data directory for H2 database..."
        //                     if not exist "data" mkdir data
                            
        //                     echo "Starting application in background with production profile..."
        //                     start /B java -jar -Dspring.profiles.active=prod target\\student-management-*.jar
                            
        //                     echo "Waiting for application startup..."
        //                     timeout /t 20 /nobreak
                            
        //                     echo "✅ Application deployment initiated!"
        //                     echo "🌐 Application URL: http://localhost:8089/student"
        //                 '''
        //             }
        //         }
        //     }
        // }
    }

    post {
        success {
            echo "🎉 Pipeline completed successfully!"
            echo "📍 JAR file location: workspace/target/*.jar"
            echo "📋 Build artifacts are available in Jenkins"
            
            // Slack notification for success
            slackSend(
                channel: '#nouveau-canal',  // Change to your Slack channel
                color: 'good',
                message: "✅ *SUCCESS* - Student Management Pipeline\n" +
                        "Job: `${env.JOB_NAME}`\n" +
                        "Build: `#${env.BUILD_NUMBER}`\n" +
                        "Branch: `${env.BRANCH_NAME}`\n" +
                        "Duration: `${BUILD_DURATION_STRING}`\n" +
                        "🌐 Application URL: http://localhost:8089/student"
            )
            
            // Email notification for success
            emailext(
                subject: "✅ SUCCESS - Student Management Pipeline #${env.BUILD_NUMBER}",
                body: """
                <h2>🎉 Pipeline Success!</h2>
                <p><strong>Job:</strong> ${env.JOB_NAME}</p>
                <p><strong>Build Number:</strong> #${env.BUILD_NUMBER}</p>
                <p><strong>Branch:</strong> ${env.BRANCH_NAME}</p>
                <p><strong>Build Duration:</strong> ${BUILD_DURATION_STRING}</p>
                <p><strong>Application URL:</strong> <a href="http://localhost:8089/student">http://localhost:8089/student</a></p>
                <p><strong>Jenkins URL:</strong> <a href="${env.BUILD_URL}">${env.BUILD_URL}</a></p>
                
                <h3>Build Summary:</h3>
                <ul>
                    <li>✅ Code checkout completed</li>
                    <li>✅ Build successful</li>
                    <li>✅ All tests passed</li>
                    <li>✅ Application packaged</li>
                    <li>✅ Deployment completed</li>
                </ul>
                """,
                to: "${env.CHANGE_AUTHOR_EMAIL ?: 'benali.hamza@esprit.tn'}",  // Change to your email
                mimeType: 'text/html'
            )
        }
        
        failure {
            echo "❌ Pipeline failed!"
            echo "🔍 Check console output for error details"
            echo "📝 Review the failed stage logs above"
            
            // Slack notification for failure
            slackSend(
                channel: '#devops',  // Change to your Slack channel
                color: 'danger',
                message: "❌ *FAILED* - Student Management Pipeline\n" +
                        "Job: `${env.JOB_NAME}`\n" +
                        "Build: `#${env.BUILD_NUMBER}`\n" +
                        "Branch: `${env.BRANCH_NAME}`\n" +
                        "Duration: `${BUILD_DURATION_STRING}`\n" +
                        "Error: Check console output for details\n" +
                        "🔗 Build URL: ${env.BUILD_URL}"
            )
            
            // Email notification for failure
            emailext(
                subject: "❌ FAILED - Student Management Pipeline #${env.BUILD_NUMBER}",
                body: """
                <h2>❌ Pipeline Failed!</h2>
                <p><strong>Job:</strong> ${env.JOB_NAME}</p>
                <p><strong>Build Number:</strong> #${env.BUILD_NUMBER}</p>
                <p><strong>Branch:</strong> ${env.BRANCH_NAME}</p>
                <p><strong>Build Duration:</strong> ${BUILD_DURATION_STRING}</p>
                <p><strong>Jenkins URL:</strong> <a href="${env.BUILD_URL}">${env.BUILD_URL}</a></p>
                <p><strong>Console Output:</strong> <a href="${env.BUILD_URL}console">${env.BUILD_URL}console</a></p>
                
                <h3>❌ Failure Details:</h3>
                <p>Please check the console output for detailed error information.</p>
                <p>Common issues to check:</p>
                <ul>
                    <li>Code compilation errors</li>
                    <li>Test failures</li>
                    <li>Dependency issues</li>
                    <li>Configuration problems</li>
                </ul>
                """,
                to: "${env.CHANGE_AUTHOR_EMAIL ?: 'benali.hamza@esprit.tn'}",  // Change to your email
                mimeType: 'text/html'
            )
        }
        
        unstable {
            echo "⚠️ Pipeline is unstable!"
            
            // Slack notification for unstable build
            slackSend(
                channel: '#devops',  // Change to your Slack channel
                color: 'warning',
                message: "⚠️ *UNSTABLE* - Student Management Pipeline\n" +
                        "Job: `${env.JOB_NAME}`\n" +
                        "Build: `#${env.BUILD_NUMBER}`\n" +
                        "Branch: `${env.BRANCH_NAME}`\n" +
                        "Duration: `${BUILD_DURATION_STRING}`\n" +
                        "Warning: Some tests may have failed intermittently\n" +
                        "🔗 Build URL: ${env.BUILD_URL}"
            )
            
            // Email notification for unstable build
            emailext(
                subject: "⚠️ UNSTABLE - Student Management Pipeline #${env.BUILD_NUMBER}",
                body: """
                <h2>⚠️ Pipeline Unstable</h2>
                <p><strong>Job:</strong> ${env.JOB_NAME}</p>
                <p><strong>Build Number:</strong> #${env.BUILD_NUMBER}</p>
                <p><strong>Branch:</strong> ${env.BRANCH_NAME}</p>
                <p><strong>Build Duration:</strong> ${BUILD_DURATION_STRING}</p>
                <p><strong>Jenkins URL:</strong> <a href="${env.BUILD_URL}">${env.BUILD_URL}</a></p>
                
                <h3>⚠️ Unstable Build Notice:</h3>
                <p>The pipeline completed but some tests may have failed intermittently.</p>
                <p>Please review the test results and consider investigating:</p>
                <ul>
                    <li>Flaky tests</li>
                    <li>Timing issues</li>
                    <li>Resource constraints</li>
                </ul>
                """,
                to: "${env.CHANGE_AUTHOR_EMAIL ?: 'benali.hamza@esprit.tn'}",  // Change to your email
                mimeType: 'text/html'
            )
        }
        
        always {
            echo "🧹 Pipeline execution finished"
            echo "📊 Build Number: ${BUILD_NUMBER}"
            echo "⏱️ Build Duration: ${BUILD_DURATION_STRING}"
        }
    }
}