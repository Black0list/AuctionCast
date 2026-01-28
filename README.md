# Bidly - Auction & Shipping Platform

## Architecture
Microservices architecture based on Spring Boot 3.x and Spring Cloud.

### Structure
- **infrastructure/**: Config Server, Eureka Server, API Gateway
- **services/**: Business microservices (User, Wallet, Catalog, etc.)
- **shared/**: Shared libraries (Common, Event, Security)
- **docs/**: Documentation

## Prerequisites
- Java 17
- Docker & Docker Compose
- Maven

## Getting Started
1. Start Infrastructure:
   ```bash
   docker-compose up -d
   ```
2. Build Services:
   ```bash
   mvn clean install
   ```
