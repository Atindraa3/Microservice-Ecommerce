# Ecommerce Microservices

## Services
- Discovery Server: `8761`
- API Gateway: `8082`
- Auth Service (JWT + Google OAuth2): `9898`
- Order Service: `8080`
- Inventory Service: `8081`
- Notification Service: `8083`

## Local Run (Docker)
```bash
export GOOGLE_CLIENT_ID=your_google_client_id
export GOOGLE_CLIENT_SECRET=your_google_client_secret

docker compose up --build
```

## Local Run (Manual)
1. Start MySQL and create databases: `auth_db`, `inventory_db`, `order_db`.
2. Start Discovery Service.
3. Start Auth, Inventory, Order, and API Gateway.
4. Start the frontend.

## Frontend
```bash
cd /Users/atindraa/Desktop/ecommerce-microservice/frontend
npm install
npm run dev
```

## Auth Flow
- Register: `POST http://localhost:8082/api/auth/register`
- Login: `POST http://localhost:8082/api/auth/login`
- Google OAuth2: `http://localhost:8082/oauth2/authorization/google`
- Profile: `GET http://localhost:8082/api/auth/me`
- Introspect: `POST http://localhost:8082/api/auth/introspect`

Google redirect URI to register:
```
http://localhost:8082/login/oauth2/code/google
```

Use the JWT from login/register for all secured endpoints:
```
Authorization: Bearer <token>
```

## RBAC
- `ROLE_USER`: place orders, check inventory.
- `ROLE_ADMIN`: can add inventory items.

Admin emails are controlled by `ADMIN_EMAILS`.

## Admin Role Management
- List users: `GET http://localhost:8082/api/auth/admin/users`
- Update roles: `PUT http://localhost:8082/api/auth/admin/users/{id}/roles`
- Audit log: `GET http://localhost:8082/api/auth/admin/audit`

## Notification Service
- Ping: `GET http://localhost:8082/api/notification/ping`
- Broadcast (admin): `POST http://localhost:8082/api/notification/broadcast`

## Inventory Reservation (Concurrency Safe)
- Reserve stock atomically: `POST http://localhost:8082/api/inventory/reserve`

Order service now calls inventory reservation in a single request and only saves the order if the reservation succeeds.

## EC2 Deployment (AWS CLI)
Scripts live in `/Users/atindraa/Desktop/ecommerce-microservice/deploy/aws`.

1. Create a security group:
```bash
export AWS_REGION=us-east-1
export VPC_ID=vpc-xxxx
export MY_IP=203.0.113.10/32
export PUBLIC_PORTS=8082

./deploy/aws/create-security-group.sh
```
Optional (open internal ports to your VPC CIDR for debugging):
```bash
export OPEN_INTERNAL_PORTS=true
export VPC_CIDR=10.0.0.0/16
./deploy/aws/create-security-group.sh
```

2. Bootstrap Docker on the EC2 instance:
```bash
./deploy/aws/bootstrap-ec2.sh
```

3. Deploy services (run on the EC2 host):
```bash
cp deploy/aws/.env.example .env
./deploy/aws/deploy-compose.sh
```
