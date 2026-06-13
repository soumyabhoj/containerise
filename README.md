# Task Manager — Spring Boot + PostgreSQL on Kubernetes

## Architecture

```
GitHub Push → GitHub Actions CI/CD
  ├─ 1. mvn verify (build + test with H2 in-memory)
  ├─ 2. docker build + push to ghcr.io
  └─ 3. kubectl deploy to Kubernetes
           ├─ Namespace: taskmanager
           ├─ Secret: postgres-secret  (DB credentials)
           ├─ ConfigMap: taskmanager-config  (non-sensitive config)
           ├─ PostgreSQL Deployment + PVC + Service
           └─ App Deployment (2 replicas) + Service
```

## REST API

| Method | Path              | Description          |
|--------|-------------------|----------------------|
| GET    | /api/tasks        | List all tasks       |
| GET    | /api/tasks?status=PENDING | Filter by status |
| GET    | /api/tasks/{id}   | Get a task           |
| POST   | /api/tasks        | Create a task        |
| PUT    | /api/tasks/{id}   | Update a task        |
| DELETE | /api/tasks/{id}   | Delete a task        |
| GET    | /actuator/health  | Health check         |

## Required GitHub Secrets

| Secret       | Description                                          |
|--------------|------------------------------------------------------|
| `KUBECONFIG` | Base64-encoded kubeconfig: `base64 ~/.kube/config`  |

> `GITHUB_TOKEN` is provided automatically by GitHub Actions for GHCR push.

## First-time Kubernetes setup

```bash
# 1. Apply the Secret (do this once — never commit real secrets to git)
#    Edit k8s/01-secret.yaml with real base64 values, then:
kubectl apply -f k8s/01-secret.yaml

# 2. The CI/CD pipeline handles everything else on every push to main.
```

## Local development

```bash
# Start PostgreSQL
docker run -d --name pg \
  -e POSTGRES_USER=taskuser \
  -e POSTGRES_PASSWORD=taskpassword \
  -e POSTGRES_DB=taskdb \
  -p 5432:5432 postgres:16-alpine

# Run the app
export DB_URL=jdbc:postgresql://localhost:5432/taskdb
export DB_USERNAME=taskuser
export DB_PASSWORD=taskpassword
mvn spring-boot:run
```

## Local Kubernetes (minikube)

```bash
minikube start
kubectl apply -f k8s/
# Access the service
minikube service taskmanager-service -n taskmanager --url
```