#!/bin/bash

# BrokerApp - Start All Services
# This script starts both backend and frontend in development mode

set -e

PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

echo "=========================================="
echo "  BrokerApp - Starting All Services"
echo "=========================================="

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Check if Docker is running
check_docker() {
    if ! docker info > /dev/null 2>&1; then
        echo -e "${RED}Error: Docker is not running!${NC}"
        echo "Please start Docker first."
        exit 1
    fi
    echo -e "${GREEN}✓ Docker is running${NC}"
}

# Start PostgreSQL
start_database() {
    echo -e "\n${YELLOW}Starting PostgreSQL...${NC}"
    cd "$PROJECT_ROOT/docker"
    
    if docker ps | grep -q mypostgres; then
        echo -e "${GREEN}✓ PostgreSQL is already running${NC}"
    else
        docker compose up -d
        echo -e "${GREEN}✓ PostgreSQL started${NC}"
    fi
}

# Start Backend
start_backend() {
    echo -e "\n${YELLOW}Starting Backend (Spring Boot)...${NC}"
    cd "$PROJECT_ROOT"
    ./gradlew bootRun &
    BACKEND_PID=$!
    echo -e "${GREEN}✓ Backend starting on http://localhost:8080${NC}"
}

# Start Frontend
start_frontend() {
    echo -e "\n${YELLOW}Starting Frontend (React)...${NC}"
    cd "$PROJECT_ROOT/frontend"
    
    # Install dependencies if node_modules doesn't exist
    if [ ! -d "node_modules" ]; then
        echo "Installing frontend dependencies..."
        npm install
    fi
    
    npm run dev &
    FRONTEND_PID=$!
    echo -e "${GREEN}✓ Frontend starting on http://localhost:3000${NC}"
}

# Cleanup function
cleanup() {
    echo -e "\n${YELLOW}Shutting down services...${NC}"
    kill $BACKEND_PID 2>/dev/null || true
    kill $FRONTEND_PID 2>/dev/null || true
    echo -e "${GREEN}✓ Services stopped${NC}"
    exit 0
}

# Trap CTRL+C
trap cleanup SIGINT SIGTERM

# Main execution
check_docker
start_database
sleep 2
start_backend
sleep 5
start_frontend

echo -e "\n=========================================="
echo -e "${GREEN}  All services are running!${NC}"
echo "=========================================="
echo "  Backend:  http://localhost:8080"
echo "  Frontend: http://localhost:3000"
echo ""
echo "  Press CTRL+C to stop all services"
echo "=========================================="

# Wait for processes
wait
