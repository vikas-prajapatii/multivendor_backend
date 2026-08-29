# Noir Bazaar: Multi-Vendor E-Commerce REST API

A robust, enterprise-grade Multi-Vendor E-Commerce REST API built with Spring Boot, PostgreSQL, Spring Security, JWT, Razorpay, Stripe, and Gemini AI.

---

## 🚀 Key Features

### 👤 User & Authentication Management
- **Role-Based Access Control (RBAC)**: Support for `USER`, `SELLER`, and `ADMIN` roles.
- **OTP Verification via Email**: SMTP-integrated OTP generator for secure email verification.
- **JWT-Based Authentication**: Secure stateless session management using JSON Web Tokens.
- **Admin Auto-Initialization**: Command-line runner to auto-provision default admin accounts on startup.

### 🏪 Vendor (Seller) Features
- **Seller Registration & Verification**: Vendors can register, upload business information, and wait for admin approval.
- **Product Inventory Management**: Complete CRUD operations for products (images, prices, color, size, quantity).
- **Revenue Dashboard & Analytics**: Daily, monthly, yearly, and hourly revenue calculation charts based on transaction history.
- **Seller Reports**: Track total sales, net revenue, status metrics, and earnings.

### 🛍️ Customer Features
- **AI-Powered Chatbot (Gemini AI)**: Interactive shopping assistant providing product recommendations, order status lookup, and general support.
- **Advanced Search & Category Navigation**: Multi-level hierarchical categories.
- **Wishlist & Cart**: Add, update, and remove items with real-time price calculations.
- **Order Flow**: Multi-item orders with dynamic order tracking and status management.
- **Coupon System**: Create, validate, and apply percentage-based discount coupons with threshold conditions.
- **Reviews & Ratings**: Customers can write textual reviews, rate products, and upload product images.

### 💳 Payment & Transactions
- **Dual Payment Gateway Integration**: Support for Razorpay and Stripe checkout processes.
- **Secure Transaction Ledger**: Automatically ledger customer/seller/order relations on checkout.
- **Refunds & Payout Tracking**: Manage status transactions for sellers.

---

## 🛠️ Tech Stack & Dependencies
- **Core Framework**: Java 21, Spring Boot 4.1.0 (Web, JPA, Security, Mail, Validation)
- **Database**: PostgreSQL (relational mappings, sequences, constraints)
- **Authentication**: JWT (jjwt-api, jjwt-impl, jjwt-jackson)
- **AI Integration**: Google Gemini API via REST requests and JsonPath parsing
- **Payment Gateways**: Stripe Java SDK, Razorpay Java SDK
- **Utilities**: Lombok, Spring Boot DevTools

---

## 📂 Project Architecture & Packages

```
com.vikas
├── ai               # Gemini AI chatbot integration and product recommendation services
├── controller       # REST endpoints organized by domain (Auth, Cart, Deal, Seller, etc.)
├── domain           # Enums and global constants (UserRole, OrderStatus, etc.)
├── dto              # Data Transfer Objects (ProductDto, OrderDto, UserDto, etc.)
├── exception        # Custom domain exceptions (ProductException, CouponNotValidException, etc.)
├── mapper           # Entity-to-DTO conversion mappers (ProductMapper, OrderMapper, etc.)
├── model            # JPA Entities (User, Seller, Product, Order, Cart, Transaction, etc.)
├── repository       # Spring Data JPA Repository interfaces
├── request          # DTO payload requests (SignupRequest, LoginRequest, etc.)
├── response         # Structured REST response wrappers (ApiResponse, PaymentLinkResponse, etc.)
└── service          # Business logic interfaces & implementations (Auth, Order, Revenue, etc.)
```

---

## 🚦 Getting Started

### Prerequisites
- JDK 21 or higher
- Maven 3.9+
- PostgreSQL instance running

### Configuration (`application.properties`)
Create or edit `src/main/resources/application.properties` with the following variables:
```properties
# Database Configurations
spring.datasource.url=jdbc:postgresql://localhost:5432/noirbazaar
spring.datasource.username=your_db_username
spring.datasource.password=your_db_password
spring.jpa.hibernate.ddl-auto=update

# SMTP Mail configurations
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your_email@gmail.com
spring.mail.password=your_app_password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# AI Integration
gemini.api.key=your_gemini_api_key

# Payment Configurations
razorpay.api.key=your_razorpay_key
razorpay.api.secret=your_razorpay_secret
stripe.api.key=your_stripe_key
```

### Installation & Execution
1. Clone the repository:
   ```bash
   git clone https://github.com/vikas-prajapatii/multivendor_backend.git
   cd "Multi Vendor backend"
   ```
2. Build the project using Maven:
   ```bash
   mvn clean install
   ```
3. Run the application:
   ```bash
   mvn spring-boot:run
   ```
