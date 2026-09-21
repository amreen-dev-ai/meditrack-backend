# MediTrack — Backend

A REST API for a patient and appointment management system, built with Spring Boot, Spring Security, JWT authentication, and MySQL.

## Tech Stack
- Java 17, Spring Boot 3.3.4
- Spring Security + JWT (jjwt) for stateless authentication
- Spring Data JPA / Hibernate
- MySQL

## Features
- User registration and login with BCrypt-hashed passwords
- JWT-based stateless authentication — every protected endpoint requires a valid token
- CRUD APIs for Patients, Doctors, and Appointments
- Appointments model a many-to-one relationship with both Patients and Doctors via foreign keys

## Architecture
Controller → Repository (Spring Data JPA) → MySQL, with a custom `JwtAuthFilter` intercepting every request to validate the JWT before it reaches a controller.

## Running Locally
1. Create a MySQL database and user matching `src/main/resources/application.properties`
2. Run `MeditrackBackendApplication.java` from your IDE, or `mvn spring-boot:run`
3. API is available at `http://localhost:8080/api`

## Key Endpoints
| Method | Endpoint | Description | Auth required |
|---|---|---|---|
| POST | `/api/auth/register` | Create a new user | No |
| POST | `/api/auth/login` | Log in, returns a JWT | No |
| GET/POST | `/api/patients` | List / create patients | Yes |
| GET/POST | `/api/doctors` | List / create doctors | Yes |
| GET/POST | `/api/appointments` | List / create appointments | Yes |

## Frontend
The companion React frontend lives at [meditrack-frontend](https://github.com/amreen-dev-ai/meditrack-frontend).
