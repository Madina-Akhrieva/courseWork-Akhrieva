#!/bin/bash
# Verification Script for Education System Deployment

echo "=========================================="
echo "  Education System - Deployment Check"
echo "=========================================="
echo ""

# Color codes
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

WORKSPACE="c:\Users\madinaa\IdeaProjects\courseWork-2026-Marina"

echo -e "${YELLOW}[INFO]${NC} Checking application components..."
echo ""

# 1. Java Version Check
echo -n "1. Java 11+ installed: "
if command -v java &> /dev/null; then
    JAVA_VER=$(java -version 2>&1 | grep -oP 'version "\K[^"]*')
    echo -e "${GREEN}✓ YES${NC} (v$JAVA_VER)"
else
    echo -e "${RED}✗ NO${NC}"
fi

# 2. Maven Check
echo -n "2. Maven installed: "
if command -v mvn &> /dev/null; then
    MVN_VER=$(mvn -v 2>&1 | grep "Apache Maven" | grep -oP '\d+\.\d+\.\d+')
    echo -e "${GREEN}✓ YES${NC} (v$MVN_VER)"
else
    echo -e "${RED}✗ NO${NC}"
fi

# 3. Docker Check
echo -n "3. Docker installed: "
if command -v docker &> /dev/null; then
    DOCKER_VER=$(docker --version | grep -oP '\d+\.\d+\.\d+')
    echo -e "${GREEN}✓ YES${NC} (v$DOCKER_VER)"
else
    echo -e "${RED}✗ NO${NC}"
fi

# 4. PostgreSQL Docker Image
echo -n "4. PostgreSQL image available: "
if docker images | grep -q "postgres.*16"; then
    echo -e "${GREEN}✓ YES${NC}"
else
    echo -e "${YELLOW}⚠ Need to pull${NC} (will auto-download)"
fi

# 5. Source Files Check
echo -n "5. Source files compiled: "
if [ -d "$WORKSPACE/target/classes" ]; then
    CLASS_COUNT=$(find "$WORKSPACE/target/classes" -name "*.class" | wc -l)
    echo -e "${GREEN}✓ YES${NC} ($CLASS_COUNT classes)"
else
    echo -e "${RED}✗ NO${NC} - Run: mvn clean compile"
fi

# 6. FXML Resources Check
echo -n "6. FXML resources present: "
FXML_COUNT=$(find "$WORKSPACE/target/classes" -name "*.fxml" 2>/dev/null | wc -l)
if [ "$FXML_COUNT" -eq 3 ]; then
    echo -e "${GREEN}✓ YES${NC} (3 views)"
else
    echo -e "${YELLOW}⚠ Found $FXML_COUNT (expected 3)${NC}"
fi

# 7. Configuration
echo -n "7. application.properties: "
if [ -f "$WORKSPACE/target/classes/application.properties" ]; then
    echo -e "${GREEN}✓ YES${NC}"
else
    echo -e "${RED}✗ NO${NC}"
fi

# 8. Demo Data
echo -n "8. demo-data.sql script: "
if [ -f "$WORKSPACE/target/classes/db/demo-data.sql" ]; then
    echo -e "${GREEN}✓ YES${NC}"
else
    echo -e "${RED}✗ NO${NC}"
fi

# 9. Docker Compose
echo -n "9. docker-compose.yml: "
if [ -f "$WORKSPACE/docker-compose.yml" ]; then
    echo -e "${GREEN}✓ YES${NC}"
else
    echo -e "${RED}✗ NO${NC}"
fi

# 10. Port Availability (Windows)
echo -n "10. Port 5050 available: "
if ! netstat -ano | grep -q ":5050"; then
    echo -e "${GREEN}✓ YES${NC}"
else
    echo -e "${YELLOW}⚠ Port may be in use${NC}"
fi

echo ""
echo "=========================================="
echo "  Ready to Launch"
echo "=========================================="
echo ""
echo "Next steps:"
echo ""
echo "  1. Start PostgreSQL:"
echo "     docker-compose up -d"
echo ""
echo "  2. Start Server (Terminal 1):"
echo "     mvn exec:java -Dexec.mainClass=\"org.example.coursework2026marina.server.ServerLauncher\""
echo ""
echo "  3. Start Client (Terminal 2):"
echo "     mvn javafx:run"
echo ""
echo "  Login with:"
echo "    Admin: admin / admin123"
echo "    Student: student / student123"
echo ""
