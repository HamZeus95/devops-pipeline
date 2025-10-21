@echo off
echo.
echo ================================================
echo   Deploying Student Management to Kubernetes
echo ================================================
echo.

REM Check if kubectl is installed
kubectl version --client >nul 2>&1
if errorlevel 1 (
    echo [ERROR] kubectl is not installed. Please install kubectl first.
    exit /b 1
)

REM Check if cluster is accessible
kubectl cluster-info >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Cannot connect to Kubernetes cluster.
    exit /b 1
)

echo [OK] Connected to Kubernetes cluster
echo.

echo Applying Kubernetes manifests...
echo.

REM 1. Namespace
echo [1/10] Creating namespace...
kubectl apply -f 00-namespace.yaml

REM 2. MySQL ConfigMap and Secret
echo [2/10] Creating MySQL configuration...
kubectl apply -f 01-mysql-configmap.yaml
kubectl apply -f 02-mysql-secret.yaml

REM 3. MySQL Storage
echo [3/10] Creating MySQL persistent storage...
kubectl apply -f 03-mysql-pv-pvc.yaml

REM 4. MySQL Deployment and Service
echo [4/10] Deploying MySQL database...
kubectl apply -f 04-mysql-deployment.yaml
kubectl apply -f 05-mysql-service.yaml

REM Wait for MySQL
echo [5/10] Waiting for MySQL to be ready...
kubectl wait --for=condition=ready pod -l app=mysql -n student-management --timeout=300s

REM 5. Application ConfigMap and Secret
echo [6/10] Creating application configuration...
kubectl apply -f 06-app-configmap.yaml
kubectl apply -f 07-app-secret.yaml

REM 6. Application Deployment and Service
echo [7/10] Deploying Student Management application...
kubectl apply -f 08-app-deployment.yaml
kubectl apply -f 09-app-service.yaml

REM 7. Ingress (optional)
echo [8/10] Applying Ingress...
kubectl apply -f 10-ingress.yaml 2>nul
if errorlevel 1 (
    echo [WARNING] Ingress not applied - you may need nginx-ingress-controller
)

REM Wait for application
echo [9/10] Waiting for application pods to be ready...
kubectl wait --for=condition=ready pod -l app=student-management -n student-management --timeout=300s

echo [10/10] Deployment completed!
echo.

echo ================================================
echo   Deployment Status
echo ================================================
echo.
kubectl get all -n student-management
echo.

REM Get access information
for /f "tokens=*" %%i in ('kubectl get svc student-management-service -n student-management -o jsonpath^="{.spec.ports[0].nodePort}"') do set NODE_PORT=%%i

echo ================================================
echo   Access Information
echo ================================================
echo.
echo Application URL: http://localhost:%NODE_PORT%/student
echo Swagger UI: http://localhost:%NODE_PORT%/student/swagger-ui.html
echo.
echo ================================================
echo   Useful Commands
echo ================================================
echo.
echo View logs:
echo   kubectl logs -f -l app=student-management -n student-management
echo.
echo List pods:
echo   kubectl get pods -n student-management
echo.
echo Describe pod:
echo   kubectl describe pod ^<pod-name^> -n student-management
echo.
echo Shell into pod:
echo   kubectl exec -it ^<pod-name^> -n student-management -- /bin/sh
echo.
