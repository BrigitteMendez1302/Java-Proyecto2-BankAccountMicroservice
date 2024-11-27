
# Bank Account Microservice

A microservice for managing bank account data in a banking system. This microservice handles operations such as creating, retrieving, updating, depositing, withdrawing, and deleting bank accounts.

## Table of Contents
- [Features](#features)
- [Technologies Used](#technologies-used)
- [Installation](#installation)
- [Configuration](#configuration)
- [API Documentation](#api-documentation)
- [Project Structure](#project-structure)
- [Coverage Report](#coverage-report)
- [License](#license)

---

## Features
- Create new bank accounts with unique account numbers.
- Retrieve all bank accounts or a specific account by ID.
- Deposit funds into a bank account.
- Withdraw funds from a bank account with rules:
  - Savings accounts cannot have a negative balance.
  - Checking accounts allow overdrafts up to -500.
- Delete bank accounts.

---

## Technologies Used
- **Java 11**
- **Spring Boot** (Web, Data JPA, Validation)
- **MySQL** (Relational Database)
- **Hibernate** (ORM for database interactions)
- **OpenAPI 3.0** (API Documentation)
- **Maven** (Dependency management)

---

## Installation

### Prerequisites
1. Install **Java 11** or higher.
2. Install **Maven**.
3. Set up **MySQL** (local or cloud).

### Steps
1. Clone the repository:
   ```bash
   git clone https://github.com/BrigitteMendez1302/Java-Proyecto2-BankAccountMicroservice
   cd bankaccount-microservice
   ```

2. Build the project:
   ```bash
   mvn clean install
   ```

3. Configure the application properties (see [Configuration](#configuration)).

---

## Configuration

Modify the `application.properties` file to match your environment:

```properties
# Spring Application Configuration
spring.application.name=bankaccount

# Server Configuration
server.port=8082

# MySQL Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/db_proyecto2
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
```

---

## API Documentation

The API is documented using **OpenAPI 3.0**.

### Key Endpoints
| Endpoint                         | Method | Description                         |
|----------------------------------|--------|-------------------------------------|
| `/accounts`                      | POST   | Create a new bank account.          |
| `/accounts`                      | GET    | Retrieve all bank accounts.         |
| `/accounts/{id}`                 | GET    | Retrieve a specific account by ID.  |
| `/accounts/{id}/deposit?amount= `| PUT    | Deposit funds into an account.      |
| `/accounts/{id}/withdraw?amount=`| PUT    | Withdraw funds from an account.     |
| `/accounts/{id}`                 | DELETE | Delete a bank account.              |

---

## Project Structure
```plaintext
src/
├── main/
│   ├── java/
│   │   └── com.example.bankaccount/
│   │       ├── config/        # Contains config classes
│   │       ├── controller/    # REST controllers
│   │       ├── dto/           # Contains the body for Error Response
│   │       ├── model/         # Entity models
│   │       ├── repository/    # Repositories for JPA
│   │       ├── service/       # Service layer
│   │       └── BanksccountApplication.java # Main application
│   └── resources/
│       ├── application.properties    # Application configuration
│       ├── api.yml                   # Open API Documentation
│       ├── static/            # Static files (if any)
│       └── templates/         # Templates for views (if any)
└── test/
    └── java/                  # Unit and integration tests
```
---
## Coverage Report
Aquí se muestra el reporte de cobertura de pruebas unitarias:

![Reporte de cobertura](https://i.ibb.co/5jvxJZN/bankaccountms.png)

---

## License
This project is licensed under the [MIT License](LICENSE).
