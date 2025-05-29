#!/bin/bash
# deploy.sh - Deployment script for TaskManager

set -e

echo "🚀 TaskManager Deployment Script"
echo "================================"

# Configuration
APP_NAME="taskmanager"
VERSION=${1:-"latest"}
REGISTRY=${REGISTRY:-"ghcr.io"}
NAMESPACE=${NAMESPACE:-"your-username"}

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

print_status() {
    echo -e "${GREEN}✓${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}⚠️${NC} $1"
}

print_error() {
    echo -e "${RED}❌${NC} $1"
}

# Check prerequisites
check_prerequisites() {
    echo "Checking prerequisites..."
    
    if ! command -v docker &> /dev/null; then
        print_error "Docker is not installed"
        exit 1
    fi
    
    if ! command -v mvn &> /dev/null; then
        print_error "Maven is not installed"
        exit 1
    fi
    
    if ! command -v java &> /dev/null; then
        print_error "Java is not installed"
        exit 1
    fi
    
    print_status "All prerequisites are satisfied"
}

# Build application
build_app() {
    echo "Building application..."
    
    # Clean and compile
    mvn clean compile
    
    # Run tests
    print_status "Running tests..."
    mvn test
    
    # Create fat JAR
    print_status "Creating fat JAR..."
    mvn package
    
    print_status "Application built successfully"
}

# Build Docker image
build_docker() {
    echo "Building Docker image..."
    
    local tag="${REGISTRY}/${NAMESPACE}/${APP_NAME}:${VERSION}"
    
    docker build -t "$tag" .
    
    print_status "Docker image built: $tag"
    
    # Tag as latest if version is not latest
    if [ "$VERSION" != "latest" ]; then
        docker tag "$tag" "${REGISTRY}/${NAMESPACE}/${APP_NAME}:latest"
        print_status "Tagged as latest"
    fi
}

# Push Docker image
push_docker() {
    echo "Pushing Docker image..."
    
    local tag="${REGISTRY}/${NAMESPACE}/${APP_NAME}:${VERSION}"
    
    docker push "$tag"
    
    if [ "$VERSION" != "latest" ]; then
        docker push "${REGISTRY}/${NAMESPACE}/${APP_NAME}:latest"
    fi
    
    print_status "Docker image pushed successfully"
}

# Deploy to Kubernetes (optional)
deploy_k8s() {
    if [ -f "k8s/deployment.yaml" ]; then
        echo "Deploying to Kubernetes..."
        
        # Replace image tag in deployment
        sed -i "s|image: .*|image: ${REGISTRY}/${NAMESPACE}/${APP_NAME}:${VERSION}|g" k8s/deployment.yaml
        
        kubectl apply -f k8s/
        
        print_status "Deployed to Kubernetes"
    else
        print_warning "No Kubernetes configuration found, skipping K8s deployment"
    fi
}

# Create GitHub release (if running in GitHub Actions)
create_release() {
    if [ -n "$GITHUB_TOKEN" ] && [ -n "$GITHUB_REF" ]; then
        echo "Creating GitHub release..."
        
        # This would typically be handled by GitHub Actions
        print_status "Release creation handled by GitHub Actions"
    else
        print_warning "Not running in GitHub Actions, skipping release creation"
    fi
}

# Main deployment flow
main() {
    echo "Starting deployment process..."
    
    check_prerequisites
    build_app
    build_docker
    
    # Only push if not in PR
    if [ "$GITHUB_EVENT_NAME" != "pull_request" ]; then
        push_docker
        deploy_k8s
        create_release
    else
        print_warning "Pull request detected, skipping push and deployment"
    fi
    
    print_status "Deployment completed successfully! 🎉"
    
    echo ""
    echo "📦 Deployment Summary:"
    echo "  - Application: $APP_NAME"
    echo "  - Version: $VERSION"
    echo "  - Registry: $REGISTRY"
    echo "  - Image: ${REGISTRY}/${NAMESPACE}/${APP_NAME}:${VERSION}"
    echo ""
    echo "🐳 To run locally:"
    echo "  docker run -it --rm ${REGISTRY}/${NAMESPACE}/${APP_NAME}:${VERSION}"
}

# Run main function
main "$@"