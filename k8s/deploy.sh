#!/bin/bash

echo "🚀 Deploying Student Management Application to Kubernetes..."
echo ""

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Function to print colored messages
print_message() {
    echo -e "${GREEN}✓${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}⚠${NC} $1"
}

print_error() {
    echo -e "${RED}✗${NC} $1"
}

# Check if kubectl is installed
if ! command -v kubectl &> /dev/null; then
    print_error "kubectl is not installed. Please install kubectl first."
    exit 1
fi

# Check if cluster is accessible
if ! kubectl cluster-info &> /dev/null; then
    print_error "Cannot connect to Kubernetes cluster. Please check your cluster connection."
    exit 1
fi

print_message "Connected to Kubernetes cluster"

# Apply manifests in order
echo ""
echo "📦 Applying Kubernetes manifests..."
echo ""

# 1. Namespace
print_message "Creating namespace..."
kubectl apply -f 00-namespace.yaml

# 2. MySQL ConfigMap and Secret
print_message "Creating MySQL configuration..."
kubectl apply -f 01-mysql-configmap.yaml
kubectl apply -f 02-mysql-secret.yaml

# 3. MySQL Storage
print_message "Creating MySQL persistent storage..."
kubectl apply -f 03-mysql-pv-pvc.yaml

# 4. MySQL Deployment and Service
print_message "Deploying MySQL database..."
kubectl apply -f 04-mysql-deployment.yaml
kubectl apply -f 05-mysql-service.yaml

# Wait for MySQL to be ready
print_warning "Waiting for MySQL to be ready (this may take a minute)..."
kubectl wait --for=condition=ready pod -l app=mysql -n student-management --timeout=300s

# 5. Application ConfigMap and Secret
print_message "Creating application configuration..."
kubectl apply -f 06-app-configmap.yaml
kubectl apply -f 07-app-secret.yaml

# 6. Application Deployment and Service
print_message "Deploying Student Management application..."
kubectl apply -f 08-app-deployment.yaml
kubectl apply -f 09-app-service.yaml

# 7. Ingress (optional)
if [ -f "10-ingress.yaml" ]; then
    print_warning "Applying Ingress (requires Ingress Controller)..."
    kubectl apply -f 10-ingress.yaml || print_warning "Ingress failed - you may need to install nginx-ingress-controller"
fi

# Wait for application to be ready
print_warning "Waiting for application pods to be ready..."
kubectl wait --for=condition=ready pod -l app=student-management -n student-management --timeout=300s

echo ""
echo "════════════════════════════════════════════════════════════"
print_message "Deployment completed successfully!"
echo "════════════════════════════════════════════════════════════"
echo ""

# Display deployment information
echo "📊 Deployment Status:"
echo ""
kubectl get all -n student-management
echo ""

# Get NodePort
NODE_PORT=$(kubectl get svc student-management-service -n student-management -o jsonpath='{.spec.ports[0].nodePort}')
NODE_IP=$(kubectl get nodes -o jsonpath='{.items[0].status.addresses[?(@.type=="InternalIP")].address}')

echo "🌐 Access the application:"
echo ""
echo "   Via NodePort:  http://${NODE_IP}:${NODE_PORT}/student"
echo "   Via localhost: http://localhost:${NODE_PORT}/student"
echo ""
echo "📚 API Documentation (Swagger): http://localhost:${NODE_PORT}/student/swagger-ui.html"
echo ""

# Show logs command
echo "📝 To view application logs:"
echo "   kubectl logs -f -l app=student-management -n student-management"
echo ""

# Show useful commands
echo "🔧 Useful commands:"
echo "   kubectl get pods -n student-management          # List all pods"
echo "   kubectl get svc -n student-management           # List all services"
echo "   kubectl describe pod <pod-name> -n student-management  # Pod details"
echo "   kubectl exec -it <pod-name> -n student-management -- /bin/sh  # Shell into pod"
echo ""
