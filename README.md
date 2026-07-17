# Noir Bazaar: Multi-Vendor E-Commerce REST API

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-blue.svg)](https://www.oracle.com/java/)
[![PostgreSQL](https://img.shields.io/badge/Database-PostgreSQL-blue.svg)](https://www.postgresql.org/)
[![Spring Security](https://img.shields.io/badge/Security-Spring%20Security%20%26%20JWT-red.svg)](https://spring.io/projects/spring-security)

Noir Bazaar is a robust, production-ready REST API backend for a **Multi-Vendor E-Commerce Platform** built using Java 21, Spring Boot, and PostgreSQL. It features an industry-standard package architecture, stateless JWT security, OTP-based registration/login, automated transactional email alerts, product catalogs, and detailed seller management.

---

## 🌟 Key Features

* **Dual-Role OTP Authentication**: Unified endpoint for customer & seller registration and signing-in using random 6-digit OTP codes sent via email.
* **Stateless JWT Security**: Custom filter-based JWT authentication injecting security configurations across the `/api/**` route namespace.
* **Onboarding & Seller Verification**: Standardized seller workflow tracking account verification levels (`PENDING_VERIFICATION`, `ACTIVE`, `DEACTIVATED`) and verified contact details.
* **Flexible Product Catalog**: Public endpoint searching, sorting, and listing products with multi-dimensional filters (brand, color, size, price, stock status).
* **Seller Inventory Console**: Product creation, update, and deletion panel for verified seller accounts.
* **Fully Audited Shopping Cart**: Retrieve, add, increment, and remove items with automated price adjustments and discount calculations.

---

## 📂 Architecture & Directory Layout

```
src/main/java/com/vikas/
├── config/                # Spring Security rules, JWT filter validators, and CORS
├── controller/            # REST API endpoints grouped by domain (auth, products, cart, etc.)
├── domain/                # Data structures and enums (Roles, order statuses, payment methods)
├── exception/             # Global exception interceptor and custom error wrappers
├── model/                 # JPA database entities (User, Seller, Product, Cart, etc.)
├── repository/            # Spring Data JPA repositories interfacing with PostgreSQL
├── request/               # Input transaction transfer objects (DTOs)
├── response/              # Server-sent transaction response wrappers
├── service/               # Service layers managing business core rules
│   └── impl/              # Core implementation classes containing transaction details
└── util/                  # Helper utilities (OTP generation, parsing helpers)
```

---

## 🛠️ Technology Stack

* **Core Framework**: Spring Boot
* **Persistence**: Spring Data JPA & Hibernate
* **Database**: PostgreSQL (Local/Remote)
* **Security**: Spring Security & JSON Web Tokens (JJWT)
* **Mailing**: Spring Boot Mail Sender (SMTP protocol)
* **Utilities**: Project Lombok (code boilerplates reduction)

---

## ⚙️ Configuration & Setup

### Prerequisites
* Java Development Kit (JDK) 21
* PostgreSQL Database
* SMTP Mail Credentials (e.g. Gmail App Password)

### Database Configuration
Modify the database settings inside the [application.properties](file:///c:/Users/vikas%20prajapati/Downloads/Multi%20Vendor/Multi-Vendor/src/main/resources/application.properties) file:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/vendor_db
spring.datasource.username=your_postgres_username
spring.datasource.password=your_postgres_password
spring.datasource.driver-class-name=org.postgresql.Driver
```

### Mail Server Configuration
Modify SMTP settings for email OTP delivery:
```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your_email@gmail.com
spring.mail.password=your_gmail_app_password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

---

## 🚀 Running the Project

Run the application using the Maven wrapper:

```bash
# Build the project
./mvnw clean install

# Launch the spring boot application
./mvnw spring-boot:run
```

The application runs on: `http://localhost:8080`

---

## 📝 API Endpoints Summary

### Authentication & Profiles
* `POST /auth/sent/login-signup-otp` - Transmit OTP to customer/seller email.
* `POST /auth/signup` - Register a customer account using the received OTP.
* `POST /auth/signing` - Customer login validation.
* `GET /users/profile` - Get authenticated customer profile (requires Customer JWT).

### Sellers Portal
* `POST /sellers` - Onboard a seller account.
* `PATCH /sellers/verify/{otp}` - Verify a seller account's email verification code.
* `POST /sellers/login` - Verify OTP and sign in a seller.
* `GET /sellers/profile` - Get authenticated seller profile (requires Seller JWT).

### Products Catalog
* `GET /product` - Public page querying/filtering of all products.
* `GET /product/search` - Public text search for products.
* `GET /product/{productId}` - Retrieve a product's details.

### Seller Dashboard (Requires Seller JWT)
* `POST /api/sellers/products` - List a new product under the seller's store.
* `GET /api/sellers/products` - Retrieve all products listed by the seller.
* `DELETE /api/sellers/products/{productId}` - Delete a listed product.

### Customer Carts (Requires Customer JWT)
* `GET /api/cart` - View shopping bag, item lists, and discount summaries.
* `PUT /api/cart/add` - Add an item to the shopping cart.
* `PUT /api/cart/item/{cartItemId}` - Adjust cart item quantities.
* `DELETE /api/cart/item/{cartItemId}` - Remove item from the cart.

---

## 🧪 Testing the APIs

For immediate manual testing, import the preconfigured Postman collection included in this repository:
* **Path**: `postman/Noir_Bazaar.postman_collection.json`

**How to use the Postman Collection:**
1. Open Postman, click **Import**, and select [postman/Noir_Bazaar.postman_collection.json](file:///C:/Users/vikas%20prajapati/Downloads/Multi%20Vendor/postman/Noir_Bazaar.postman_collection.json).
2. The collection is pre-configured with a Postman script that automatically captures customer and seller JWT tokens upon successful login, saving them to variables (`{{customer_jwt}}` and `{{seller_jwt}}`) so that authenticated routes run seamlessly out of the box.

---

## 🔮 Future Improvements

* **Order Processing Service**: Fully implement order placement, tracking, and cancellation modules.
* **Coupon & Deal Engines**: Activate the database-level coupon validation and home page promotional banners service routes.
* **Payment Gateway Integration**: Secure integrations with Razorpay, Stripe, or PayPal for live customer checkout.
* **Seller Analytics Dashboard**: Implement transaction history logs and automated seller balance payouts.
* **Interactive Frontend Client**: Build a complete React/Next.js client web application integrating all e-commerce operations.
