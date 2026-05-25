#!/bin/bash

# H5 Photo Upload - Start All Services

echo "=== Starting H5 Photo Upload Services ==="

# Setup paths
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_DIR="$SCRIPT_DIR"
BACKEND_DIR="$PROJECT_DIR/backend"
FRONTEND_DIR="$PROJECT_DIR/frontend"
LOG_DIR="$PROJECT_DIR/logs"
mkdir -p "$LOG_DIR"

# Kill any existing processes on our ports
echo "Cleaning up existing processes..."
pkill -f "spring-boot" 2>/dev/null || true
pkill -f "PhotoApplication" 2>/dev/null || true
pkill -f "vite" 2>/dev/null || true
pkill -f "node.*vite" 2>/dev/null || true

# Windows fallback: kill whatever is still listening on our ports
# (Git Bash pkill can't reach java.exe spawned by mvn, so it leaks)
for PORT in 8283 5173; do
    PIDS=$(netstat -ano 2>/dev/null | grep "LISTENING" | grep ":$PORT " | awk '{print $5}' | sort -u)
    for PID in $PIDS; do
        [ -n "$PID" ] && [ "$PID" != "0" ] && taskkill //PID "$PID" //F >/dev/null 2>&1 || true
    done
done

# Wait for cleanup
sleep 2

# Start Backend (Java 21)
echo ""
echo "=== Starting Backend on port 8283 ==="
export JAVA_HOME="/c/Program Files/Java/jdk-21.0.2"
export PATH="$JAVA_HOME/bin:$PATH"

cd "$BACKEND_DIR"
nohup mvn spring-boot:run -DskipTests > "$LOG_DIR/backend.log" 2>&1 &
BACKEND_PID=$!
echo "Backend starting... (PID: $BACKEND_PID)"

# Wait for backend to be ready (check with GET first, then POST is fine too)
echo "Waiting for backend to start..."
for i in {1..30}; do
    HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8283 --connect-timeout 2 2>/dev/null)
    if [ "$HTTP_CODE" != "000" ]; then
        echo "Backend is ready! (HTTP $HTTP_CODE)"
        break
    fi
    sleep 2
done

# Start Frontend
echo ""
echo "=== Starting Frontend on port 5173 ==="

# Check if node_modules exists
if [ ! -d "$FRONTEND_DIR/node_modules" ]; then
    echo "Installing frontend dependencies..."
    cd "$FRONTEND_DIR"
    npm install 2>&1 | tail -3
fi

cd "$FRONTEND_DIR"
nohup node node_modules/vite/bin/vite.js --port 5173 > "$LOG_DIR/frontend.log" 2>&1 &
FRONTEND_PID=$!
echo "Frontend starting... (PID: $FRONTEND_PID)"

# Wait for frontend to be ready
echo "Waiting for frontend to start..."
for i in {1..15}; do
    HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:5173 --connect-timeout 2 2>/dev/null)
    if [ "$HTTP_CODE" != "000" ]; then
        echo "Frontend is ready! (HTTP $HTTP_CODE)"
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
echo "Logs: $LOG_DIR/backend.log, $LOG_DIR/frontend.log"
echo "To stop: pkill -f 'spring-boot\|vite\|PhotoApplication'"