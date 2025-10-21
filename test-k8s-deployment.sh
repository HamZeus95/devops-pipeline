#!/bin/bash

echo "🧪 Testing Kubernetes Deployment (Simulating Jenkins Pipeline)"
echo "=============================================================="

# Test 1: Check kubectl availability
echo ""
echo "Test 1: Checking kubectl..."
if command -v kubectl &> /dev/null; then
    echo "✅ kubectl is installed: $(kubectl version --client --short 2>/dev/null || kubectl version --client 2>&1 | head -n1)"
else
    echo "❌ kubectl is NOT installed"
    exit 1
fi

# Test 2: Check kubeconfig access as jenkins user
echo ""
echo "Test 2: Testing kubectl as jenkins user..."
if sudo -u jenkins kubectl get nodes &> /dev/null; then
    echo "✅ kubectl works as jenkins user"
    sudo -u jenkins kubectl get nodes
else
    echo "❌ kubectl fails as jenkins user"
    exit 1
fi

# Test 3: Test cluster connectivity
echo ""
echo "Test 3: Testing cluster connectivity..."
if sudo -u jenkins kubectl cluster-info &> /dev/null; then
    echo "✅ Can connect to K8s API server"
    sudo -u jenkins kubectl cluster-info
else
    echo "❌ Cannot connect to K8s cluster"
    exit 1
fi

# Test 4: Check if namespace exists or can be created
echo ""
echo "Test 4: Checking namespace access..."
if sudo -u jenkins kubectl get namespace student-management &> /dev/null; then
    echo "✅ Namespace 'student-management' already exists"
    sudo -u jenkins kubectl get all -n student-management
else
    echo "⚠️  Namespace 'student-management' does not exist yet (will be created during deployment)"
fi

# Test 5: Check if manifests directory exists
echo ""
echo "Test 5: Checking K8s manifests..."
if [ -d "k8s" ]; then
    echo "✅ k8s/ directory exists"
    echo "📁 Manifest files:"
    ls -1 k8s/*.yaml 2>/dev/null || echo "   No YAML files found"
else
    echo "❌ k8s/ directory not found"
    exit 1
fi

# Test 6: Verify Docker Hub access (optional)
echo ""
echo "Test 6: Checking Docker images..."
if docker images | grep -q "benalihamza/devops"; then
    echo "✅ Docker images found locally:"
    docker images | grep "benalihamza/devops" | head -3
else
    echo "⚠️  No local Docker images found (will be pulled from Docker Hub during deployment)"
fi

echo ""
echo "=============================================================="
echo "🎉 All tests passed! Your Jenkins VM is ready for K8s deployment!"
echo ""
echo "📋 Next Steps:"
echo "   1. Commit and push your code: git add . && git commit -m 'Disable SonarQube' && git push"
echo "   2. Go to Jenkins: http://192.168.182.146:8080"
echo "   3. Click 'Build Now' on your pipeline"
echo "   4. Watch the deployment progress!"
echo ""
