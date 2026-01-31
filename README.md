# Quora Clone Backend

A developer community platform backend built with **Spring Boot 3.5.5** and **Java 17**, featuring JWT authentication, RESTful APIs, and comprehensive security.

## 🚀 Features

- **Authentication & Authorization**: JWT-based authentication with Spring Security
- **RESTful API**: Comprehensive REST endpoints for posts, comments, responses, votes, and developers
- **Developer Profiles**: Track reputation, skills, posts, and contributions
- **Content Management**: Create, update, delete posts with topics and search functionality
- **Voting System**: Upvote/downvote posts, responses, and comments
- **API Documentation**: Interactive Swagger/OpenAPI documentation
- **MySQL Database**: Persistent storage with JPA/Hibernate

## 📋 Prerequisites

- **Java 17** or higher
- **Maven 3.6+**
- **MySQL 8.0+**
- **OpenSSL** (for generating JWT secrets)

## ⚙️ Environment Setup

### 1. Clone the Repository

```bash
git clone https://github.com/AbhijeetFasate13/Quora-Backend.git
cd Quora-Backend
```

### 2. Configure Environment Variables

Copy the example environment file:

```bash
cp .env.example env.properties
```

### 3. Generate JWT Secret

Generate a secure 256-bit secret key for JWT signing:

```bash
# Using OpenSSL (recommended)
openssl rand -base64 64

# Using Python
python3 -c "import secrets; print(secrets.token_urlsafe(64))"

# Using Node.js
node -e "console.log(require('crypto').randomBytes(64).toString('base64'))"
```

### 4. Update `env.properties`

Edit `env.properties` with your configuration:

```properties
# Database
DB_PASSWORD=your-secure-database-password

# JWT Configuration
JWT_SECRET=<paste-your-generated-secret-here>
JWT_ACCESS_TOKEN_EXPIRATION=900000
JWT_REFRESH_TOKEN_EXPIRATION=604800000
```

> ⚠️ **IMPORTANT**: Never commit `env.properties` to git! It's already in `.gitignore`.

### 5. Create MySQL Database

```sql
CREATE DATABASE temp;
```

Or change the database name in `application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/your_database_name
```

## 🔧 Build & Run

### Using Maven Wrapper

```bash
# Build the project
./mvnw clean install

# Run tests
./mvnw test

# Run the application
./mvnw spring-boot:run
```

### Using Installed Maven

```bash
mvn clean install
mvn test
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## 📚 API Documentation

Once the application is running, access the interactive API documentation:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI Spec**: http://localhost:8080/v3/api-docs

## 🔐 Security Configuration

### JWT Token Expiration

- **Access Token**: 15 minutes (configurable via `JWT_ACCESS_TOKEN_EXPIRATION`)
- **Refresh Token**: 7 days (configurable via `JWT_REFRESH_TOKEN_EXPIRATION`)

### Public Endpoints (No Authentication Required)

- `POST /api/auth/login` - User login
- `POST /api/auth/register` - User registration
- `GET /api/posts/all` - Get all posts
- `GET /api/posts/topic/{keyword}` - Search posts by keyword
- `GET /api/dev/**` - Developer endpoints

### Protected Endpoints

All other endpoints require JWT authentication. Include the token in the `Authorization` header:

```
Authorization: Bearer <your-jwt-token>
```

## 🧪 Testing

Run the test suite:

```bash
# Run all tests
./mvnw test

# Run tests with coverage
./mvnw test jacoco:report

# Run specific test class
./mvnw test -Dtest=DeveloperServiceTest
```

## 📦 Project Structure

```
src/
├── main/
│   ├── java/com/devcommunity/
│   │   ├── config/          # Security & app configuration
│   │   ├── controller/      # REST controllers
│   │   ├── dto/             # Data Transfer Objects
│   │   ├── entity/          # JPA entities
│   │   ├── exception/       # Exception handlers
│   │   ├── repository/      # JPA repositories
│   │   ├── service/         # Business logic
│   │   └── util/            # Utility classes
│   └── resources/
│       └── application.properties  # Application configuration
└── test/                    # Unit & integration tests
```

## 🛠️ Tech Stack

- **Framework**: Spring Boot 3.5.5
- **Security**: Spring Security + JWT (jjwt 0.12.5)
- **Database**: MySQL + Spring Data JPA
- **Documentation**: SpringDoc OpenAPI (Swagger)
- **Utilities**: Lombok, ModelMapper
- **Build Tool**: Maven

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📝 License

This project is open source and available for learning purposes.

## 👨‍💻 Author

**Abhijeet Fasate**

- GitHub: [@AbhijeetFasate13](https://github.com/AbhijeetFasate13)

---

## 🔥 Troubleshooting

### Application won't start

**Error**: `Access denied for user 'root'@'localhost'`

- **Solution**: Check MySQL is running and password in `env.properties` is correct

**Error**: `JWT secret not found`

- **Solution**: Ensure `env.properties` exists and contains `JWT_SECRET`

### Tests failing

**Error**: `NullPointerException` in tests

- **Solution**: Run `./mvnw clean test` to ensure clean build

---

**⚠️ Security Reminders**:

- ✅ Never commit `env.properties` to git
- ✅ Use strong, randomly generated JWT secrets
- ✅ Change default database passwords
- ✅ Review CORS settings before deploying to production
