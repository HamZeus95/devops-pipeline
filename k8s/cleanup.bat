@echo off
echo.
echo ================================================
echo   Cleaning up Student Management from K8s
echo ================================================
echo.

echo [WARNING] This will delete all resources in the student-management namespace
echo.
set /p CONFIRM="Are you sure you want to continue? (yes/no): "

if /i not "%CONFIRM%"=="yes" (
    echo Cleanup cancelled.
    exit /b 0
)

echo.
echo Deleting Kubernetes resources...
echo.

kubectl delete -f 10-ingress.yaml 2>nul
kubectl delete -f 09-app-service.yaml
kubectl delete -f 08-app-deployment.yaml
kubectl delete -f 07-app-secret.yaml
kubectl delete -f 06-app-configmap.yaml
kubectl delete -f 05-mysql-service.yaml
kubectl delete -f 04-mysql-deployment.yaml
kubectl delete -f 03-mysql-pv-pvc.yaml
kubectl delete -f 02-mysql-secret.yaml
kubectl delete -f 01-mysql-configmap.yaml
kubectl delete -f 00-namespace.yaml

echo.
echo [OK] Cleanup completed!
echo.
