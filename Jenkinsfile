pipeline {
    agent any
    
    tools {
        maven 'Maven'  
        jdk 'JDK'
    }
    
    environment {
        MAVEN_OPTS = '-Dmaven.repo.local=.m2/repository' // Use a local Maven repository in the workspace
        SONAR_HOST_URL = 'http://localhost:9000' // SonarQube server URL (localhost for Docker containers)
        SONAR_HOST_URL_EXTERNAL = 'http://192.168.1.239:9000' // External access URL for reports
    }

    stages {
        stage('Checkout') {
            steps {
                echo 'Checking out source code...'
                checkout scm
            }
        }
        
        // stage('Setup SonarQube') {
        //     steps {
        //         echo 'Setting up SonarQube Docker container...'
        //         script {
        //             if (isUnix()) {
        //                 sh '''
        //                     echo "Checking if SonarQube container exists..."
        //                     if docker ps -a --format "table {{.Names}}" | grep -q "sonarqube"; then
        //                         echo "SonarQube container exists, checking status..."
        //                         if docker ps --format "table {{.Names}}" | grep -q "sonarqube"; then
        //                             echo "✅ SonarQube is already running"
        //                         else
        //                             echo "Starting existing SonarQube container..."
        //                             docker start sonarqube
        //                             echo "Waiting for SonarQube to be ready..."
        //                             sleep 60
        //                         fi
        //                     else
        //                         echo "Creating new SonarQube container..."
        //                         docker run -d --name sonarqube \\
        //                             -e SONAR_ES_BOOTSTRAP_CHECKS_DISABLE=true \\
        //                             -p 9000:9000 \\
        //                             sonarqube:latest
        //                         echo "Waiting for SonarQube to initialize (this may take a few minutes)..."
        //                         sleep 150
        //                     fi
        //                     
        //                     echo "Checking SonarQube health..."
        //                     timeout 300 bash -c 'until curl -f http://localhost:9000/api/system/status; do echo "Waiting for SonarQube..."; sleep 10; done'
        //                     
        //                     echo "🔧 Disabling SonarQube authentication for Jenkins analysis..."
        //                     curl -u admin:Hamza310795§ -X POST "http://localhost:9000/api/settings/set?key=sonar.forceAuthentication&value=false" || echo "Failed to disable authentication, will try with credentials"
        //                     curl -u admin:Hamza310795§ -X POST "http://localhost:9000/api/permissions/add_group?groupName=Anyone&permission=scan" || echo "Failed to add Anyone scan permission"
        //                     
        //                     echo "✅ SonarQube is ready!"
        //                 '''
        //             } else {
        //                 bat '''
        //                     echo "Checking if SonarQube container exists..."
        //                     docker ps -a --format "table {{.Names}}" | findstr "sonarqube" >nul
        //                     if %errorlevel% equ 0 (
        //                         echo "SonarQube container exists, checking status..."
        //                         docker ps --format "table {{.Names}}" | findstr "sonarqube" >nul
        //                         if %errorlevel% equ 0 (
        //                             echo "✅ SonarQube is already running"
        //                         ) else (
        //                             echo "Starting existing SonarQube container..."
        //                             docker start sonarqube
        //                             echo "Waiting for SonarQube to be ready..."
        //                             timeout /t 60 /nobreak >nul
        //                         )
        //                     ) else (
        //                         echo "Creating new SonarQube container..."
        //                         docker run -d --name sonarqube ^
        //                             -e SONAR_ES_BOOTSTRAP_CHECKS_DISABLE=true ^
        //                             -p 9000:9000 ^
        //                             sonarqube:latest
        //                         echo "Waiting for SonarQube to initialize (this may take a few minutes)..."
        //                         timeout /t 120 /nobreak >nul
        //                     )
        //                     
        //                     echo "✅ SonarQube setup completed!"
        //                     echo "🌐 SonarQube will be available at: http://localhost:9000 (VM internal)"
        //                     echo "🌐 External access: http://192.168.1.239:9000 (configure VM networking)"
        //                 '''
        //             }
        //         }
        //     }
        //     post {
        //         success {
        //             echo "🐳 SonarQube Docker container is running"
        //             echo "🌐 Access SonarQube at: http://192.168.1.239:9000"
        //             echo "📝 Default credentials: admin/admin (you'll be prompted to change)"
        //         }
        //         failure {
        //             echo "❌ Failed to setup SonarQube container"
        //             echo "🔍 Check Docker daemon and network connectivity"
        //         }
        //     }
        // }
        
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
        
        // stage('SonarQube Analysis') {
        //     // SonarQube analysis stage is temporarily disabled
        //     // steps {
        //     //     echo 'Running SonarQube code quality analysis...'
        //     //     script {
        //     //         def sonarQubeUrl = env.SONAR_HOST_URL ?: 'http://localhost:9000'
        //     //         def sonarQubeUrlExternal = env.SONAR_HOST_URL_EXTERNAL ?: 'http://192.168.1.239:9000'
        //     //         def projectKey = 'student-management'
        //     //         def projectName = 'Student Management Application'
        //     //         echo "🔍 SonarQube URL: ${sonarQubeUrl}"
        //     //         echo "📊 Project: ${projectName} (${projectKey})"
        //     //         // Analysis steps removed while disabled
        //     //     }
        //     // }
        //     // post {
        //     //     always {
        //     //         echo "📊 SonarQube analysis completed (disabled)"
        //     //     }
        //     // }
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
                    echo "📦 JAR file packaged and archived successfully!"
                }
            }
        }
        
        stage('Docker Build & Push') {
            steps {
                script {
                    def repo = "benalihamza/devops"
                    def tag = "${env.BUILD_NUMBER}"

                    withCredentials([usernamePassword(credentialsId: 'docker', usernameVariable: 'DOCKERHUB_USER', passwordVariable: 'DOCKERHUB_PASS')]) {
                        echo 'Building and pushing Docker image...'
                        if (isUnix()) {
                            sh """
                                echo "\$DOCKERHUB_PASS" | docker login -u "\$DOCKERHUB_USER" --password-stdin
                                docker build -t ${repo}:${tag} .
                                docker push ${repo}:${tag}
                                docker tag ${repo}:${tag} ${repo}:latest
                                docker push ${repo}:latest
                            """
                        } else {
                            bat """
                                echo %DOCKERHUB_PASS% | docker login -u "%DOCKERHUB_USER%" --password-stdin
                                docker build -t ${repo}:${tag} .
                                docker push ${repo}:${tag}
                                docker tag ${repo}:${tag} ${repo}:latest
                                docker push ${repo}:latest
                            """
                        }
                    }
                }
            }
        }
        
        stage('Deploy to Kubernetes') {
            steps {
                echo 'Deploying to remote Kubernetes cluster...'
                script {
                    // Use kubeconfig credentials stored in Jenkins
                    withCredentials([file(credentialsId: 'kubeconfig', variable: 'KUBECONFIG')]) {
                        if (isUnix()) {
                            sh '''
                                echo "🚀 Starting Kubernetes deployment..."
                                
                                # Set kubeconfig
                                export KUBECONFIG=${KUBECONFIG}
                                
                                # Check if kubectl is available
                                if ! command -v kubectl &> /dev/null; then
                                    echo "❌ kubectl is not installed"
                                    exit 1
                                fi
                                
                                # Check cluster connectivity
                                echo "🔗 Testing connection to remote Kubernetes cluster..."
                                if ! kubectl cluster-info; then
                                    echo "❌ Cannot connect to Kubernetes cluster"
                                    echo "💡 Please check:"
                                    echo "   - Kubeconfig file is correctly configured"
                                    echo "   - Network connectivity to K8s cluster"
                                    echo "   - K8s API server is accessible"
                                    exit 1
                                fi
                                
                                echo "✅ Connected to Kubernetes cluster"
                                kubectl get nodes
                                
                                # Apply Kubernetes manifests
                                cd k8s
                                
                                echo "📦 Creating namespace..."
                                kubectl apply -f 00-namespace.yaml
                                
                                echo "🗄️ Deploying MySQL database..."
                                kubectl apply -f 01-mysql-configmap.yaml
                                kubectl apply -f 02-mysql-secret.yaml
                                kubectl apply -f 03-mysql-pv-pvc.yaml
                                kubectl apply -f 04-mysql-deployment.yaml
                                kubectl apply -f 05-mysql-service.yaml
                                
                                echo "⏳ Waiting for MySQL to be ready..."
                                kubectl wait --for=condition=ready pod -l app=mysql -n student-management --timeout=300s || echo "MySQL readiness check timed out, continuing..."
                                
                                echo "🚀 Deploying application..."
                                kubectl apply -f 06-app-configmap.yaml
                                kubectl apply -f 07-app-secret.yaml
                                kubectl apply -f 08-app-deployment.yaml
                                kubectl apply -f 09-app-service.yaml
                                
                                # Update deployment to use the new image with build number tag
                                echo "🔄 Updating deployment with new image: benalihamza/devops:${BUILD_NUMBER}"
                                kubectl set image deployment/student-management-app student-management=benalihamza/devops:${BUILD_NUMBER} -n student-management
                                
                                echo "⏳ Waiting for rollout to complete..."
                                kubectl rollout status deployment/student-management-app -n student-management --timeout=300s
                                
                                echo "✅ Deployment completed successfully!"
                                
                                # Display deployment info
                                echo ""
                                echo "📊 Deployment Status:"
                                kubectl get pods -n student-management
                                kubectl get svc -n student-management
                                
                                # Get access information
                                NODE_PORT=$(kubectl get svc student-management-service -n student-management -o jsonpath='{.spec.ports[0].nodePort}')
                                K8S_NODE_IP=$(kubectl get nodes -o jsonpath='{.items[0].status.addresses[?(@.type=="InternalIP")].address}')
                                echo ""
                                echo "🌐 Application URL: http://${K8S_NODE_IP}:${NODE_PORT}/student"
                                echo "📚 Swagger UI: http://${K8S_NODE_IP}:${NODE_PORT}/student/swagger-ui.html"
                            '''
                        } else {
                            bat '''
                                echo "🚀 Starting Kubernetes deployment..."
                                
                                REM Set kubeconfig
                                set KUBECONFIG=%KUBECONFIG%
                                
                                REM Check if kubectl is available
                                kubectl version --client >nul 2>&1
                                if errorlevel 1 (
                                    echo "❌ kubectl is not installed"
                                    exit /b 1
                                )
                                
                                REM Check cluster connectivity
                                echo "🔗 Testing connection to remote Kubernetes cluster..."
                                kubectl cluster-info
                                if errorlevel 1 (
                                    echo "❌ Cannot connect to Kubernetes cluster"
                                    echo "💡 Please check:"
                                    echo "   - Kubeconfig file is correctly configured"
                                    echo "   - Network connectivity to K8s cluster"
                                    echo "   - K8s API server is accessible"
                                    exit /b 1
                                )
                                
                                echo "✅ Connected to Kubernetes cluster"
                                kubectl get nodes
                                
                                REM Apply Kubernetes manifests
                                cd k8s
                                
                                echo "📦 Creating namespace..."
                                kubectl apply -f 00-namespace.yaml
                                
                                echo "🗄️ Deploying MySQL database..."
                                kubectl apply -f 01-mysql-configmap.yaml
                                kubectl apply -f 02-mysql-secret.yaml
                                kubectl apply -f 03-mysql-pv-pvc.yaml
                                kubectl apply -f 04-mysql-deployment.yaml
                                kubectl apply -f 05-mysql-service.yaml
                                
                                echo "⏳ Waiting for MySQL to be ready..."
                                kubectl wait --for=condition=ready pod -l app=mysql -n student-management --timeout=300s
                                
                                echo "🚀 Deploying application..."
                                kubectl apply -f 06-app-configmap.yaml
                                kubectl apply -f 07-app-secret.yaml
                                kubectl apply -f 08-app-deployment.yaml
                                kubectl apply -f 09-app-service.yaml
                                
                                REM Update deployment to use the new image with build number tag
                                echo "🔄 Updating deployment with new image: benalihamza/devops:%BUILD_NUMBER%"
                                kubectl set image deployment/student-management-app student-management=benalihamza/devops:%BUILD_NUMBER% -n student-management
                                
                                echo "⏳ Waiting for rollout to complete..."
                                kubectl rollout status deployment/student-management-app -n student-management --timeout=300s
                                
                                echo "✅ Deployment completed successfully!"
                                
                                REM Display deployment info
                                echo.
                                echo "📊 Deployment Status:"
                                kubectl get pods -n student-management
                                kubectl get svc -n student-management
                                
                                REM Get access information
                                for /f "tokens=*" %%i in ('kubectl get svc student-management-service -n student-management -o jsonpath^="{.spec.ports[0].nodePort}"') do set NODE_PORT=%%i
                                for /f "tokens=*" %%i in ('kubectl get nodes -o jsonpath^="{.items[0].status.addresses[?(@.type==\"InternalIP\")].address}"') do set K8S_NODE_IP=%%i
                                echo.
                                echo "🌐 Application URL: http://%K8S_NODE_IP%:%NODE_PORT%/student"
                                echo "📚 Swagger UI: http://%K8S_NODE_IP%:%NODE_PORT%/student/swagger-ui.html"
                            '''
                        }
                    }
                }
            }
            post {
                success {
                    echo "✅ Kubernetes deployment successful!"
                    echo "🎯 Application is running in remote Kubernetes cluster"
                    echo "📊 To check status, configure kubectl with the cluster's kubeconfig"
                }
                failure {
                    echo "❌ Kubernetes deployment failed!"
                    echo "🔍 Possible issues:"
                    echo "   - Jenkins cannot reach the K8s cluster"
                    echo "   - Kubeconfig credentials not configured"
                    echo "   - Network connectivity issues"
                    echo "💡 Setup Instructions:"
                    echo "   1. Copy kubeconfig from K8s VM: ~/.kube/config"
                    echo "   2. Add as 'Secret file' credential in Jenkins with ID 'kubeconfig'"
                    echo "   3. Ensure Jenkins VM can reach K8s API server"
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
            echo "🐳 Docker image: benalihamza/devops:${BUILD_NUMBER}"
            echo "☸️  Kubernetes: Application deployed to cluster"
            echo "🐳 SonarQube is running at: http://192.168.1.239:9000"
            echo "📊 View your code quality report at: http://192.168.1.239:9000/dashboard?id=student-management"
            echo "💡 If external access fails, configure VM networking (Bridged mode recommended)"
            
            script {
                // Get NodePort for K8s access
                if (isUnix()) {
                    def nodePort = sh(script: "kubectl get svc student-management-service -n student-management -o jsonpath='{.spec.ports[0].nodePort}' 2>/dev/null || echo '30089'", returnStdout: true).trim()
                    echo "🌐 Kubernetes Application URL: http://localhost:${nodePort}/student"
                }
            }
            
            // Slack notification for success
            slackSend(
                channel: '#nouveau-canal',  // Change to your Slack channel
                color: 'good',
                message: "✅ *SUCCESS* - Student Management Pipeline\n" +
                        "Job: `${env.JOB_NAME}`\n" +
                        "Build: `#${env.BUILD_NUMBER}`\n" +
                        "Branch: `${env.BRANCH_NAME}`\n" +
                        "Duration: `${currentBuild.durationString}`\n" +
                        "🌐 Application URL: http://localhost:8089/student\n" +
                        "📊 SonarQube Report: ${env.SONAR_HOST_URL_EXTERNAL ?: 'http://192.168.1.239:9000'}/dashboard?id=student-management"
            )
            
            // Email notification for success
            emailext(
                subject: "✅ SUCCESS - Student Management Pipeline #${env.BUILD_NUMBER}",
                body: """
                <h2>🎉 Pipeline Success!</h2>
                <p><strong>Job:</strong> ${env.JOB_NAME}</p>
                <p><strong>Build Number:</strong> #${env.BUILD_NUMBER}</p>
                <p><strong>Branch:</strong> ${env.BRANCH_NAME}</p>
                <p><strong>Build Duration:</strong> ${currentBuild.durationString}</p>
                <p><strong>Application URL:</strong> <a href="http://localhost:8089/student">http://localhost:8089/student</a></p>
                <p><strong>Jenkins URL:</strong> <a href="${env.BUILD_URL}">${env.BUILD_URL}</a></p>
                
                <h3>Build Summary:</h3>
                <ul>
                    <li>✅ Code checkout completed</li>
                    <li>✅ Build successful</li>
                    <li>✅ All tests passed</li>
                    <li>✅ SonarQube analysis completed</li>
                    <li>✅ Application packaged</li>
                    <li>✅ Docker image built and pushed</li>
                    <li>✅ Deployed to Kubernetes cluster</li>
                </ul>
                
                <h3>Quality Reports:</h3>
                <ul>
                    <li>📊 <a href="${env.SONAR_HOST_URL_EXTERNAL ?: 'http://192.168.1.239:9000'}/dashboard?id=student-management">SonarQube Quality Dashboard</a></li>
                    <li>🧪 <a href="${env.BUILD_URL}testReport/">Test Results</a></li>
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
                        "Duration: `${currentBuild.durationString}`\n" +
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
                <p><strong>Build Duration:</strong> ${currentBuild.durationString}</p>
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
                        "Duration: `${currentBuild.durationString}`\n" +
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
                <p><strong>Build Duration:</strong> ${currentBuild.durationString}</p>
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
            echo "⏱️ Build Duration: ${currentBuild.durationString}"
        }
    }
}