#!/bin/bash

echo "🔧 Disabling SonarQube authentication for testing..."

# Wait for SonarQube to be ready
echo "⏳ Waiting for SonarQube to be fully ready..."
sleep 30

# Disable force authentication
echo "🔐 Disabling force user authentication..."
curl -X POST \
  -u admin:admin \
  "http://localhost:9000/api/settings/set?key=sonar.forceAuthentication&value=false" \
  -H "Content-Type: application/x-www-form-urlencoded"

echo ""
echo "✅ SonarQube authentication disabled!"
echo "🌐 You can now access SonarQube without authentication"
echo "📊 Dashboard: http://192.168.182.146:9000"
echo ""
echo "⚠️  Note: This is for testing only. Enable authentication in production!"