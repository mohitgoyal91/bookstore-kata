# Online Bookstore API

A REST API for an online bookstore built with Spring Boot.

The application supports:

* User registration and authentication using JWT
* Browsing books
* Viewing the authenticated user's cart
* Adding books to the cart
* Creating orders
* Inventory validation during checkout

## Assumptions and Potential Improvements

This project was implemented within a limited timeframe and focuses primarily on the core functionality of the bookstore API. The following assumptions and potential improvements should be considered:

1. **Credential and Secret Management**
   Application credentials and secrets, including the JWT secret, are currently configured within the application configuration. In a production environment, these should be externalized and managed using a secure secrets management solution such as HashiCorp Vault, AWS Secrets Manager, Azure Key Vault, or environment-specific secret management.

2. **Logging and Observability**
   Comprehensive application logging has not been implemented due to time constraints. Structured logging can be incorporated using **SLF4J/Logback**, with **Spring AOP** used where appropriate for cross-cutting concerns such as request, service, performance, and exception logging. Production readiness could be further improved by adding metrics, distributed tracing, and centralized log aggregation.

3. **Administrative Book Management**
   Administrative functionality has intentionally been kept outside the current scope. Endpoints for operations such as creating, updating, and deleting books have not been implemented. These could be introduced as secured administrative endpoints with appropriate authorization controls.

These items are considered production-readiness and feature enhancements rather than requirements for the current implementation.


## Prerequisites

Make sure the following are installed:

* Java 21 or later
* Maven

Verify your installation:

```bash
java -version
mvn -version
```

## Running the Tests

Run all tests from the project root:

```bash
mvn clean test
```

## Running the Application

Start the application with Maven:

```bash
mvn spring-boot:run
```

Alternatively, build the application first:

```bash
mvn clean package
```

Then run the generated JAR:

```bash
java -jar target/*.jar
```

By default, the API is available at:

```text
http://localhost:8080
```

## Authentication

The API uses JWT Bearer authentication.

Registration and login return the JWT in the `Authorization` response header:

```text
Authorization: Bearer <token>
```

Copy the token from this header and use it for authenticated requests.

---

## API Examples

The following commands can be copied directly into Postman using **Import → Raw text**, or executed from a terminal with `curl`.

### Register

```bash
curl --location --request POST 'http://localhost:8080/api/auth/register' \
--header 'Content-Type: application/json' \
--data-raw '{
    "email": "john.doe@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "password": "Password123!"
}'
```

A successful registration returns:

```text
HTTP 201 Created
Authorization: Bearer <token>
```

Save the token for the authenticated requests below.

### Login

```bash
curl --location --request POST 'http://localhost:8080/api/auth/login' \
--header 'Content-Type: application/json' \
--data-raw '{
    "email": "john.doe@example.com",
    "password": "Password123!"
}'
```

A successful login returns:

```text
HTTP 200 OK
Authorization: Bearer <token>
```

Replace `<token>` in the following requests with the JWT returned by registration or login.

### Get Books

```bash
curl --location 'http://localhost:8080/api/books' \
--header 'Authorization: Bearer <token>'
```

If the API supports the `available` query parameter, available books can be retrieved with:

```bash
curl --location 'http://localhost:8080/api/books?available=true' \
--header 'Authorization: Bearer <token>'
```

Unavailable books can be retrieved with:

```bash
curl --location 'http://localhost:8080/api/books?available=false' \
--header 'Authorization: Bearer <token>'
```

Use a `bookId` returned by this endpoint when adding a book to the cart.

### Get Cart

```bash
curl --location 'http://localhost:8080/api/cart' \
--header 'Authorization: Bearer <token>'
```

The cart belongs to the currently authenticated user. A user ID does not need to be provided explicitly.

### Add Book to Cart

Replace `<bookId>` with the UUID of a book returned by the books endpoint.

```bash
curl --location --request POST 'http://localhost:8080/api/cart/books' \
--header 'Authorization: Bearer <token>' \
--header 'Content-Type: application/json' \
--data-raw '{
    "bookId": "<bookId>",
    "quantity": 1
}'
```

For example:

```json
{
    "bookId": "550e8400-e29b-41d4-a716-446655440000",
    "quantity": 1
}
```

Adding another quantity for a book already present in the cart increases its existing quantity.

The requested quantity cannot exceed the currently available inventory.

### Create Order

Create an order from the authenticated user's current cart:

```bash
curl --location --request POST 'http://localhost:8080/api/orders' \
--header 'Authorization: Bearer <token>'
```

During order creation, the application validates the cart and verifies inventory before completing the order.

Inventory updates and order creation are performed transactionally to prevent multiple concurrent orders from purchasing more copies of a book than are available.

After a successful order, the purchased books are removed from the user's cart.

---

## Typical Test Flow

A simple end-to-end flow is:

```text
1. Register a user
        ↓
2. Copy the JWT from the Authorization response header
        ↓
3. GET /api/books
        ↓
4. Copy a book ID
        ↓
5. POST /api/cart/books
        ↓
6. GET /api/cart
        ↓
7. POST /api/orders
```

## HTTP Status Codes

Common responses include:

| Status             | Meaning                                           |
| ------------------ | ------------------------------------------------- |
| `200 OK`           | Request completed successfully                    |
| `201 Created`      | Resource created successfully                     |
| `400 Bad Request`  | Invalid request or quantity                       |
| `401 Unauthorized` | Missing or invalid JWT                            |
| `404 Not Found`    | Requested resource does not exist                 |
| `409 Conflict`     | Resource conflict, such as insufficient inventory |

## Technology Stack

* Java
* Spring Boot
* Spring Web
* Spring Data JPA
* Spring Security
* JWT authentication
* Auth0 Java JWT
* H2 Database
* JUnit 5
* Mockito
* Maven
