
minikube start --mount --mount-string="$PWD:/home/project"

minikube addons enable metrics-server

#source minikube_env.sh
