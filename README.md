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
git clone <repository-url>
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

## Available Endpoints

| Endpoint | Intended Access |
|---|---|
| `/api/public` | Everyone |
| `/api/user` | Authenticated users |
| `/api/manager` | Managers and administrators |
| `/api/admin` | Administrators only |

**Current implementation:** The endpoints are defined, but custom role-based authorization has not yet been implemented. Spring Security currently requires authentication for all endpoints by default.

## Project Structure

```text
src/main/java/edu/rit/securitytactics/
├── SecurityTacticsApplication.java
├── controller/
│   └── ServiceController.java
├── authentication/
├── authorization/
└── config/
```

The authentication, authorization, and configuration packages will be populated as development progresses.
