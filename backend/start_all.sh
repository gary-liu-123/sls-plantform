#!/bin/bash

# H5 Photo Upload - Start All Services

echo "=== Starting H5 Photo Upload Services ==="

# Kill any existing processes on our ports
echo "Cleaning up existing processes..."
pkill -f "spring-boot" 2>/dev/null || true
pkill -f "PhotoApplication" 2>/dev/null || true
pkill -f "vite" 2>/dev/null || true

# Wait for cleanup
sleep 2

# Start Backend (Java 21)
echo ""
echo "=== Starting Backend on port 8283 ==="
export JAVA_HOME="/c/Program Files/Java/jdk-21.0.2"
cd "$(dirname "$0")"
cd ..
backend/pom.xml 2>/dev/null || cd backend

nohup mvn spring-boot:run -Dmaven.repo.local="/c/Users/刘念/.m2/repository" -DskipTests > ../backend.log 2>&1 &
BACKEND_PID=$!
echo "Backend starting... (PID: $BACKEND_PID)"

# Wait for backend to be ready
echo "Waiting for backend to start..."
for i in {1..30}; do
    if curl -s http://localhost:8283/api/upload -X POST -w "%{http_code}" 2>/dev/null | grep -q "200\|400\|500"; then
        echo "Backend is ready!"
        break
    fi
    sleep 2
done

# Start Frontend
echo ""
echo "=== Starting Frontend on port 5173 ==="
cd ..
cd frontend

# Check if node_modules exists
if [ ! -d "node_modules" ]; then
    echo "Installing frontend dependencies..."
    npm install 2>&1 | tail -3
fi

nohup node node_modules/vite/bin/vite.js --port 5173 > ../frontend.log 2>&1 &
FRONTEND_PID=$!
echo "Frontend starting... (PID: $FRONTEND_PID)"

# Wait for frontend to be ready
echo "Waiting for frontend to start..."
for i in {1..15}; do
    if curl -s http://localhost:5173 2>/dev/null | grep -q "root"; then
        echo "Frontend is ready!"
        break
    fi
    sleep 1
done

# Get IP address
IP=$(ipconfig 2>/dev/null | grep -A2 "IPv4" | grep "192.168" | head -1 | sed 's/.*: //' | sed 's/ .*//')

echo ""
echo "=== All Services Started ==="
echo "Frontend: http://localhost:5173"
echo "Backend:  http://localhost:8283"
if [ -n "$IP" ]; then
    echo "Mobile:   http://$IP:5173"
fi
echo ""
echo "Logs: backend.log, frontend.log"
echo "To stop: pkill -f 'spring-boot\|vite' "