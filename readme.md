# 🧾 Transaction Microservice – Spring Boot (Tabcorp Technical Test)

A simple microservice built with Spring Boot, Spring Security, and JPA using an in-memory H2 database.  
This project handles financial transactions and provides reporting capabilities, with authentication and validation.

### Prerequisites
- Java 21
- Maven

### Run the App

```bash
./mvnw spring-boot:run
```
App will run at:
http://localhost:8080

This app uses Basic Auth

### Create a Transaction using JSON request
```bash
curl -X POST http://localhost:8080/transactions \
  -u admin:password \
  -H "Content-Type: application/json" \
  -d '{
    "transactionTime": "2026-04-25T12:00:00",
    "customerId": 10001,
    "quantity": 2,
    "productCode": "PRODUCT_001"
  }'
```

### Create a Transaction using binary request
```bash
curl -X POST http://localhost:8080/transactions \
  -u admin:password \
  -H "Content-Type: application/octet-stream" \
  --data-binary '{
    "transactionTime": "2026-04-25T12:00:00",
    "customerId": 10001,
    "quantity": 2,
    "productCode": "PRODUCT_001"
  }'
```

### Report – Total Cost Per Customer
```bash
curl -X GET http://localhost:8080/transactions/report/customer/10001 \
  -u admin:password
```

### Report – Total Cost Per Product
```bash
curl -X GET http://localhost:8080/transactions/report/product/PRODUCT_001 \
  -u admin:password
```

### Report – Number of Transactions by Australian Customers
```bash
curl -X GET http://localhost:8080/transactions/report/australian-customers \
  -u admin:password
```