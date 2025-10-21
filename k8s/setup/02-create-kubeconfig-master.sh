#!/bin/bash

# Run this script on K8s Master (192.168.182.152)

echo "======================================"
echo "Configuring kubeconfig for Jenkins"
echo "======================================"

# Create a copy of kubeconfig for Jenkins
cp ~/.kube/config ~/jenkins-kubeconfig

# Edit the server address to use master IP instead of localhost
sed -i 's|https://127.0.0.1:6443|https://192.168.182.152:6443|g' ~/jenkins-kubeconfig
sed -i 's|https://localhost:6443|https://192.168.182.152:6443|g' ~/jenkins-kubeconfig

echo ""
echo "✅ Kubeconfig created: ~/jenkins-kubeconfig"
echo ""
echo "Server address updated to: https://192.168.182.152:6443"
echo ""
cat ~/jenkins-kubeconfig | grep server:
echo ""
echo "Next step: Copy this file to Jenkins VM"
echo "Run on your laptop/another machine:"
echo "  scp master@192.168.182.152:~/jenkins-kubeconfig ./jenkins-kubeconfig"
