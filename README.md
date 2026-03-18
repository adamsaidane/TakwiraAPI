# ⚽ Takwira API

![Java](https://img.shields.io/badge/Java-17+-ED8B00?style=flat-square&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.0+-6DB33F?style=flat-square&logo=springboot&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8.0+-4479A1?style=flat-square&logo=mysql&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-3.8+-C71A36?style=flat-square&logo=apachemaven&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-ready-2496ED?style=flat-square&logo=docker&logoColor=white)
![License](https://img.shields.io/badge/License-Private-red?style=flat-square)

A robust, RESTful backend API for football team management built with **Java** and **Spring Boot**. Takwira API provides comprehensive endpoints for managing players, matches, goals, and team statistics.

---

## 📋 Table of Contents

- [Overview](#-overview)
- [Features](#-features)
- [Tech Stack](#-tech-stack)
- [Project Structure](#-project-structure)
- [Installation](#-installation)
- [Running the API](#-running-the-api)
- [API Endpoints](#-api-endpoints)
- [Authentication](#-authentication)
- [Database Schema](#-database-schema)
- [Configuration](#-configuration)
- [Deployment](#-deployment)
- [Contributing](#-contributing)
- [License](#-license)

---

## 🎯 Overview

**Takwira API** is a comprehensive backend service for managing football teams and matches. It exposes a clean RESTful interface for all core football management operations — from registering players and scheduling matches to tracking goals and computing statistics — secured behind JWT-based authentication.

---

## ✨ Features

### 👥 Player Management
- Create, read, update, and delete player profiles
- Search and filter players by name and other criteria
- Track per-player statistics across all matches

### 🏆 Match Management
- Create matches and manage team lineups
- Update live scores and match status
- Browse full match history with detailed breakdowns

### ⚽ Goal Tracking
- Record goals and assist providers per match
- Update or remove goal entries
- Aggregate goal counts by player and match

### 📊 Statistics
- Player and team performance metrics
- Goals-per-player and matches-played tracking
- Historical trends across the season

### 🔐 Security
- JWT-based authentication and token refresh
- Role-based access control (RBAC)
- Secure password storage with hashing

### 📝 API Quality
- OpenAPI / Swagger documentation at `/swagger-ui`
- Request validation with informative error responses
- CORS support for cross-origin clients
- Global exception handling with structured error payloads

---

## 💻 Tech Stack

| Layer | Technology |
|---|---|
| **Language** | Java 17+ |
| **Framework** | Spring Boot 3.0+ |
| **Build Tool** | Maven 3.8+ |
| **Database** | MySQL 8.0+ / PostgreSQL 12+ |
| **ORM** | Hibernate / JPA |
| **Authentication** | JWT (JSON Web Token) |
| **API Docs** | Swagger / OpenAPI 3 |
| **Logging** | SLF4J + Logback |
| **Containerisation** | Docker + Docker Compose |
| **Testing** | JUnit 5, Mockito |

---

## 📁 Project Structure

```
takwira-api/
├── src/
│   ├── main/
│   │   ├── java/com/takwira/
│   │   │   ├── TakwiraApplication.java       # Entry point
│   │   │   ├── controller/
│   │   │   │   ├── PlayerController.java
│   │   │   │   ├── MatchController.java
│   │   │   │   ├── PlayerStatsController.java
│   │   │   │   ├── AuthController.java
│   │   │   ├── service/
│   │   │   │   ├── PlayerService.java
│   │   │   │   ├── MatchService.java
│   │   │   │   ├── StatsService.java
│   │   │   │   ├── JWTService.java
│   │   │   │   └── UserService.java
│   │   │   ├── repository/
│   │   │   │   ├── PlayerRepository.java
│   │   │   │   ├── MatchRepository.java
│   │   │   │   ├── GoalRepository.java
│   │   │   │   ├── UserRepository.java
│   │   │   │   └── MatchPlayerRepository.java
│   │   │   ├── entity/
│   │   │   │   ├── Player.java
│   │   │   │   ├── Match.java
│   │   │   │   ├── Goal.java
│   │   │   │   ├── User.java
│   │   │   │   ├── Stats.java
│   │   │   │   └── MatchPlayer.java
│   │   │   ├── dto/
│   │   │   │   ├── PlayerDto.java
│   │   │   │   ├── MatchDto.java
│   │   │   │   ├── GoalDto.java
│   │   │   │   ├── UserDto.java
│   │   │   │   └── ApiResponse.java
│   │   │   ├── config/
│   │   │   │   ├── SecurityConfig.java
│   │   │   │   ├── CorsConfig.java
│   │   │   │   └── CacheConfig.java
│   │   │   ├── mapper/
│   │   │   │   ├── Mapper.java
│   │   │   ├── util/
│   │   │   └── enums/
│   │   │       ├── TeamEnum.java
│   │   │       ├── RoleEnum.java
│   │   │       └── StatusEnum.java
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-dev.yml
│   │       ├── application-prod.yml
│   │       ├── logback.xml
│   │       └── db/migration/
│   │           ├── V1__Initial_Schema.sql
│   │           └── V2__Add_Tables.sql
│   └── test/java/com/takwira/
│       ├── service/
│       │   ├── PlayerServiceTest.java
│       │   ├── MatchServiceTest.java
│       │   └── GoalServiceTest.java
│       ├── controller/
│       │   └── PlayerControllerTest.java
│       └── integration/
│           └── ApiIntegrationTest.java
├── Dockerfile
├── docker-compose.yml
├── pom.xml
└── .gitignore
```

---

## 🚀 Installation

### Prerequisites

- Java 17 or later
- Maven 3.8+
- MySQL 8.0+ or PostgreSQL 12+
- Git

### Step 1 — Clone the repository

```bash
git clone https://github.com/adamsaidane/TakwiraAPI.git
cd TakwiraAPI
```

### Step 2 — Configure the database

Create a database named `takwira_db`, then edit `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/takwira_db
    username: root
    password: your_password
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false
```

### Step 3 — Build the project

```bash
mvn clean install
```

### Step 4 — Run the application

```bash
mvn spring-boot:run
```

The API will be available at **http://localhost:8080**  
Swagger UI will be available at **http://localhost:8080/swagger-ui**

---

## 🏃 Running the API

### Local development

```bash
# Build
mvn clean install

# Run
mvn spring-boot:run

# Run with a specific profile
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
```

### Docker

```bash
# Build the image
docker build -t takwira-api:latest .

# Run the container
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/takwira_db \
  -e SPRING_DATASOURCE_USERNAME=root \
  -e SPRING_DATASOURCE_PASSWORD=your_password \
  takwira-api:latest
```

### Docker Compose (recommended)

```bash
docker-compose up -d
```

---

## 📚 API Endpoints

### Players

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/players` | Get all players |
| `GET` | `/api/players/{id}` | Get player by ID |
| `POST` | `/api/players` | Create a new player |
| `PUT` | `/api/players/{id}` | Update a player |
| `DELETE` | `/api/players/{id}` | Delete a player |
| `GET` | `/api/players/search?name=` | Search players by name |
| `GET` | `/api/players/{id}/stats` | Get player statistics |

### Matches

| Method   | Endpoint                           | Description              |
|----------|------------------------------------|--------------------------|
| `GET`    | `/api/matches`                     | Get all matches          |
| `GET`    | `/api/matches/{id}`                | Get match by ID          |
| `PUT`    | `/api/matches/create/{id}`         | Update a match           |
| `POST`   | `/api/matches/create`              | Create a new match       |
| `POST`   | `/api/matches/create/{id}/players` | Add players to a match   |
| `POST`   | `/api/matches/create/{id}/goals`   | Get goals for a match    |
| `DELETE` | `/api/matches/delete/{id}`         | Delete a match           |
| `DELETE` | `/api/matches/{id}/goals/{goalId}` | Delete a goal from match |

### Statistics

| Method | Endpoint                               | Description               |
|--------|----------------------------------------|---------------------------|
| `GET` | `/api/stats/players`                   | All player statistics     |
| `GET` | `/api/stats/players/{id}`              | Player statistics         |
| `GET` | `/api/stats/top-scorers`               | Top goalscorer            |
| `GET` | `/api/stats/top-contributors`          | Top contirubtorss         |
| `GET` | `/api/stats/top-assisters`             | Top assisters             |
| `GET` | `/api/stats/most-active`               | Most matches played       |
| `GET` | `/api/stats/best-win-ratio`            | Top win ratio             |
| `GET` | `/api/stats/best-goal-average`         | Top goal per match        |
| `GET` | `/api/stats/best-contribution-average` | Top contibution per match |
| `GET` | `/api/stats/best-assist-average`       | Top assist per match      |

### Authentication

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/auth/login` | Log in and receive a JWT |

---

## 🔐 Authentication

Takwira API uses **JWT (JSON Web Token)** for stateless authentication.

### Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "user@example.com",
    "password": "password123"
  }'
```

**Response:**

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 3600
}
```

### Using the token

Pass the token in the `Authorization` header for all protected endpoints:

```bash
curl -H "Authorization: Bearer <YOUR_JWT_TOKEN>" \
  http://localhost:8080/api/players
```

Public routes (login, register, Swagger) are accessible without a token. All other routes require a valid JWT.

---

## 🗄️ Database Schema

### `players`

```sql
CREATE TABLE players (
    player_id     INT PRIMARY KEY AUTO_INCREMENT,
    player_name   VARCHAR(100) NOT NULL,
    position      VARCHAR(50),
    jersey_number INT,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### `matches`

```sql
CREATE TABLE matches (
    match_id    INT PRIMARY KEY AUTO_INCREMENT,
    match_name  VARCHAR(200) NOT NULL,
    match_date  DATETIME,
    team1_score INT DEFAULT 0,
    team2_score INT DEFAULT 0,
    status      VARCHAR(20),
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### `goals`

```sql
CREATE TABLE goals (
    goal_id            INT PRIMARY KEY AUTO_INCREMENT,
    match_id           INT NOT NULL,
    goal_scorer_id     INT NOT NULL,
    assist_provider_id INT,
    team               VARCHAR(20),
    goal_time          INT,
    created_at         TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (match_id)       REFERENCES matches(match_id),
    FOREIGN KEY (goal_scorer_id) REFERENCES players(player_id)
);
```

---

## ⚙️ Configuration

### `application.yml`

```yaml
spring:
  application:
    name: takwira-api
  datasource:
    url: jdbc:mysql://localhost:3306/takwira_db
    username: root
    password: your_password
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
    properties:
      hibernate:
        format_sql: true
  jackson:
    serialization:
      write-dates-as-timestamps: false

jwt:
  secret: your-secret-key-here-minimum-32-characters
  expiration: 3600000         # 1 hour in ms
  refresh-expiration: 604800000   # 7 days in ms

server:
  port: 8080
  servlet:
    context-path: /api

logging:
  level:
    root: INFO
    com.takwira: DEBUG
```

### Environment variables

When running via Docker or CI/CD, override sensitive values with environment variables:

| Variable | Description |
|---|---|
| `SPRING_DATASOURCE_URL` | JDBC connection string |
| `SPRING_DATASOURCE_USERNAME` | Database username |
| `SPRING_DATASOURCE_PASSWORD` | Database password |
| `JWT_SECRET` | Secret key for signing tokens (min. 32 chars) |
| `JWT_EXPIRATION` | Token TTL in milliseconds |

---

## 🐳 Deployment

### `docker-compose.yml`

```yaml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: password
      MYSQL_DATABASE: takwira_db
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql

  api:
    build: .
    depends_on:
      - mysql
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/takwira_db
      SPRING_DATASOURCE_USERNAME: root
      SPRING_DATASOURCE_PASSWORD: password
    ports:
      - "8080:8080"

volumes:
  mysql_data:
```

---

## 🤝 Contributing

Contributions are welcome!

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/your-feature-name`
3. Commit your changes: `git commit -m "feat: add your feature"`
4. Push to the branch: `git push origin feature/your-feature-name`
5. Open a Pull Request against `master`

Please follow the existing code style and include tests for any new functionality.

---

## 📄 License

This is a private project. All rights reserved.
