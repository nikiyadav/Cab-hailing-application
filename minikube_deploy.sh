# Build images of services
docker build -t project-1_cab-service ./cab-service &&

docker build -t project-1_db-service ./db-service &&

docker build -t project-1_ride-service ./ride-service &&

docker build -t project-1_wallet-service ./wallet-service &&

# Deploy cab service
minikube kubectl -- apply -f k8s/cab-service &&

# Deploy external database service
minikube kubectl -- apply -f k8s/db-service &&

# Deploy ride service
minikube kubectl -- apply -f k8s/ride-service &&

# Deploy wallet service
minikube kubectl -- apply -f k8s/wallet-service &&

# Check deployment, pod, service status
minikube kubectl -- get deployment,pods,svc 
sleep 20

# Apply port-forwarding
minikube kubectl -- port-forward svc/wallet-service 8082:8082 &
minikube kubectl -- port-forward svc/cab-service 8080:8080 &
minikube kubectl -- port-forward svc/ride-service 8081:8081 &
