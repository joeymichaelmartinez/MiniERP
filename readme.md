# Mini ERP Project

A simple **Enterprise Resource Planning (ERP)** system built with **Spring Boot**, **Java**, and **MySQL**. This project allows management of **customers**, **products**, and **orders**, including stock tracking and order lifecycle handling.

---

## Table of Contents

- [Features](#features)
- [Technologies](#technologies)
- [Getting Started](#getting-started)
- [API Endpoints](#api-endpoints)
- [Order Lifecycle](#order-lifecycle)

---

## Features

- **Customers**: Create, read, update, and delete customers.
- **Products**: Create, read, update, and delete products.
- **Orders**: Create and update orders, manage order items, calculate totals.
- **Stock Management**: Automatically adjust product stock when orders are created or updated.
- **Order Status**: Track order states (`NEW`, `PROCESSING`, `COMPLETE`, `CANCELED`) and prevent modifications on completed orders.
---

## Technologies

- Java 21
- Spring Boot 3.x
- Spring Data JPA / Hibernate
- MySQL 8
- Lombok
- Maven

---

## Getting Started

### Prerequisites

- Java 21+
- Maven
- MySQL 8+

### Setup

1. **Clone the repository:**

```bash
git clone <repository-url>
cd mini-erp
```

## MySQL Docker Setup

To quickly run a MySQL instance for the Mini ERP project using Docker:

```bash
docker run --name mysql-erp \
  -e MYSQL_ROOT_PASSWORD=root \
  -p 3306:3306 \
  -d mysql:8.1
````

## Run the Application
```bash
mvn spring-boot:run
```

The server will start on http://localhost:8080

## API Endpoints

### Customer
| Method | Endpoint        | Description        |
|--------|-----------------|------------------|
| GET    | /customers      | Get all customers |
| GET    | /customers?id=  | Get customer by ID|
| POST   | /customers      | Create new customer |
| PUT    | /customers/{id} | Update customer   |
| DELETE | /customers/{id} | Delete customer   |

### Product

| Method | Endpoint       | Description               |
| ------ |----------------| ------------------------- |
| GET    | /products      | Get all products          |
| GET    | /products?id=  | Get product by ID         |
| POST   | /products      | Create new product        |
| PUT    | /products/{id} | Update product info/stock |
| DELETE | /products/{id} | Delete product            |

### Order
| Method        | Endpoint            | Description                     |
|---------------|---------------------|---------------------------------|
| GET           | /orders             | Get all orders                  |
| GET           | /orders?id=         | Get order by ID                 |
| POST          | /orders             | Create new order                |
| PUT           | /orders/{id}        | Update order items / quantities |
| DELETE        | /orders/{id}        | Delete order                    |
| CANCEL        | /orders/{id}/cancel | Cancel order                    |
| UPDATE STATUS | /orders/{id}/status | Change order status             |

## Order Lifecycle
### Orders have a status that restricts certain operations:

- NEW: Can be updated or canceled
- PROCESSING: Cannot be updated or deleted
- COMPLETE: Cannot be updated or deleted
- CANCELED: Cannot be updated or deleted

Orders track stock levels automatically. Updates will adjust the associated product's stock. Creating or increasing order quantities will throw an error if insufficient stock is available.