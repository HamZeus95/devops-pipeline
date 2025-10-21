#!/bin/bash

# Run this script on Jenkins VM (192.168.182.146)

echo "======================================"
echo "Installing kubectl on Jenkins VM"
echo "======================================"

# Download kubectl
curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl"

# Make it executable
chmod +x kubectl

# Move to system path
sudo mv kubectl /usr/local/bin/

# Verify installation
kubectl version --client

echo ""
echo "✅ kubectl installed successfully!"
echo ""
echo "Next steps:"
echo "1. Get kubeconfig from K8s master"
echo "2. Test connection to K8s cluster"
