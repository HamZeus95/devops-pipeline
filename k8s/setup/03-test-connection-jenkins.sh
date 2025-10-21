#!/bin/bash

# Run this script on Jenkins VM (192.168.182.146)
# PREREQUISITE: jenkins-kubeconfig file should be in current directory

echo "======================================"
echo "Testing K8s Connection from Jenkins VM"
echo "======================================"

# Set kubeconfig
export KUBECONFIG=./jenkins-kubeconfig

echo "1️⃣  Testing network connectivity to K8s API server..."
echo "   Trying to reach: 192.168.182.152:6443"
timeout 5 bash -c 'cat < /dev/null > /dev/tcp/192.168.182.152/6443' && echo "   ✅ Port 6443 is reachable" || echo "   ❌ Cannot reach port 6443"

echo ""
echo "2️⃣  Testing kubectl connection..."
kubectl cluster-info

echo ""
echo "3️⃣  Getting cluster nodes..."
kubectl get nodes

echo ""
echo "4️⃣  Testing namespace creation..."
kubectl create namespace test-jenkins || echo "Namespace might already exist"
kubectl get namespace test-jenkins
kubectl delete namespace test-jenkins

echo ""
if [ $? -eq 0 ]; then
    echo "✅ SUCCESS! Jenkins VM can connect to K8s cluster"
    echo ""
    echo "Next step: Add this kubeconfig to Jenkins credentials"
else
    echo "❌ FAILED! Check the following:"
    echo "   1. Firewall on master allows port 6443"
    echo "   2. Kubeconfig has correct server IP (192.168.182.152:6443)"
    echo "   3. Network connectivity between VMs"
fi
