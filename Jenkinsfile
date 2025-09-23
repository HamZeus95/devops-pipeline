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
        
        stage('Deploy') {
            steps {
                echo 'Deploying the application...'
                script {
                    if (isUnix()) {
                        sh '''
                            echo "Stopping existing application (if running)..."
                            pkill -f "student-management" || true
                            
                            echo "Creating data directory for H2 database..."
                            mkdir -p data
                            
                            echo "Starting new application in background with production profile..."
                            nohup java -jar -Dspring.profiles.active=prod target/student-management-*.jar > app.log 2>&1 &
                            
                            echo "Waiting for application to start..."
                            sleep 20
                            
                            echo "Checking if application is running..."
                            if pgrep -f "student-management"; then
                                echo "✅ Application started successfully!"
                                echo "🌐 Application URL: http://localhost:8089/student"
                                echo "🗄️ H2 Console: http://localhost:8089/student/h2-console (if enabled)"
                            else
                                echo "⚠️ Application may not have started properly. Check app.log"
                                tail -30 app.log || echo "No log file found"
                            fi
                        '''
                    } else {
                        bat '''
                            echo "Stopping existing application (if running)..."
                            taskkill /F /IM java.exe /FI "COMMANDLINE eq *student-management*" 2>nul || echo "No existing process found"
                            
                            echo "Creating data directory for H2 database..."
                            if not exist "data" mkdir data
                            
                            echo "Starting application in background with production profile..."
                            start /B java -jar -Dspring.profiles.active=prod target\\student-management-*.jar
                            
                            echo "Waiting for application startup..."
                            timeout /t 20 /nobreak
                            
                            echo "✅ Application deployment initiated!"
                            echo "🌐 Application URL: http://localhost:8089/student"
                        '''
                    }
                }
            }
        }
    }

    post {
        success {
            echo "🎉 Pipeline completed successfully!"
            echo "📍 JAR file location: workspace/target/*.jar"
            echo "📋 Build artifacts are available in Jenkins"
        }
        
        failure {
            echo "❌ Pipeline failed!"
            echo "🔍 Check console output for error details"
            echo "📝 Review the failed stage logs above"
        }
        
        always {
            echo "🧹 Pipeline execution finished"
            echo "📊 Build Number: ${BUILD_NUMBER}"
            echo "⏱️ Build Duration: ${BUILD_DURATION_STRING}"
        }
    }
}