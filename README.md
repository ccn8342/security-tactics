# Authentication and Authorization Security Tactics

## Project Overview

This project demonstrates the implementation of authentication and authorization security tactics using Java, Spring Boot, and Spring Security.

The application provides REST endpoints with different intended access privileges to demonstrate role-based access control (RBAC).

## Technologies

- Java 21
- Spring Boot 4.1.1
- Spring Security
- Spring Web
- Gradle (Groovy DSL)
- JUnit / Spring Boot Test (for automated testing)

## Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/ccn8342/security-tactics.git
cd security-tactics
```

### 2. Open the Project

Open the project folder in IntelliJ IDEA or another Java-compatible IDE.

Ensure Java 21 is configured as the project SDK and allow Gradle to synchronize dependencies.

### 3. Build the Application

On macOS or Linux:

```bash
./gradlew clean build
```

On Windows:

```powershell
.\gradlew.bat clean build
```

### 4. Run the Application

On macOS or Linux:

```bash
./gradlew bootRun
```

On Windows:

```powershell
.\gradlew.bat bootRun
```

The application runs at:

http://localhost:8080

Press Ctrl+C in the terminal to stop the server.

## Demo Login

| Username | Password | Role |
|---|---|---|
| `alice` | `AliceDemo123!` | USER |
| `morgan` | `MorganDemo123!` | MANAGER |
| `alex` | `AlexDemo123!` | ADMIN |

These are public demo credentials. Accounts are recreated at startup and stored in memory with BCrypt password hashes.

1. Open http://localhost:8080/api/public without logging in.
2. Open http://localhost:8080/login and sign in with a demo account. A direct login takes you to `/api/user`.
3. Visit another service listed below. The session keeps you logged in without resubmitting your password.
4. Open http://localhost:8080/logout and confirm logout. Visiting `/api/user` afterward requires login again.

## Available Endpoints

| Endpoint | Intended Access |
|---|---|
| `/api/public` | Everyone |
| `/api/user` | Authenticated users |
| `/api/manager` | Managers and administrators |
| `/api/admin` | Administrators only |

Authorization is enforced by the Spring Security filter chain in `SecurityConfig`. Authenticated users can access `/api/user`, managers and administrators can access `/api/manager`, and only administrators can access `/api/admin`.

## Project Structure

```text
src/main/java/edu/rit/security_tactics/
├── SecurityTacticsApplication.java
├── controller/
│   └── ServiceController.java
└── config/
    └── SecurityConfig.java
```

`SecurityConfig` defines the demo users, password encoder, and login/logout and basic access configuration.
