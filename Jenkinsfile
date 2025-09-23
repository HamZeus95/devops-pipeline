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
                            
                            echo "Starting new application with H2 database..."
                            # Start with H2 in-memory database for testing
                            nohup java -jar target/student-management-*.jar \\
                                --spring.datasource.url=jdbc:h2:mem:testdb \\
                                --spring.datasource.driverClassName=org.h2.Driver \\
                                --spring.datasource.username=sa \\
                                --spring.datasource.password= \\
                                --spring.jpa.database-platform=org.hibernate.dialect.H2Dialect \\
                                --spring.h2.console.enabled=true \\
                                --server.port=8089 > app.log 2>&1 &
                            
                            echo "Waiting for application to start..."
                            sleep 20
                            
                            echo "Checking application startup..."
                            if pgrep -f "student-management"; then
                                echo "✅ Application process is running!"
                                
                                # Check if application is responding
                                for i in {1..5}; do
                                    if curl -f http://localhost:8089/actuator/health 2>/dev/null; then
                                        echo "✅ Application is healthy and responding!"
                                        break
                                    elif curl -f http://localhost:8089 2>/dev/null; then
                                        echo "✅ Application is responding on port 8089!"
                                        break
                                    else
                                        echo "⏳ Waiting for application to respond... (attempt $i/5)"
                                        sleep 5
                                    fi
                                done
                                
                                echo "📋 Application logs (last 10 lines):"
                                tail -10 app.log || echo "No recent logs found"
                            else
                                echo "❌ Application failed to start!"
                                echo "📋 Full application logs:"
                                cat app.log || echo "No log file found"
                                exit 1
                            fi
                        '''
                    } else {
                        bat '''
                            echo "Stopping existing application (if running)..."
                            taskkill /F /IM java.exe /FI "COMMANDLINE eq *student-management*" 2>nul || echo "No existing process found"
                            
                            echo "Starting application with H2 database..."
                            start /B java -jar target\\student-management-*.jar ^
                                --spring.datasource.url=jdbc:h2:mem:testdb ^
                                --spring.datasource.driverClassName=org.h2.Driver ^
                                --spring.datasource.username=sa ^
                                --spring.datasource.password= ^
                                --spring.jpa.database-platform=org.hibernate.dialect.H2Dialect ^
                                --spring.h2.console.enabled=true ^
                                --server.port=8089
                            
                            echo "Waiting for application startup..."
                            timeout /t 20 /nobreak
                            
                            echo "✅ Application deployment initiated with H2 database!"
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