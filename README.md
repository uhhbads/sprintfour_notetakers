# SPRINT FOUR - Secure Note-Taking API
## Project Duration: February 5, 2026 – February 10, 2026
### Overview
The Secure Note-Taking API is a high-security backend service focused on Identity and Access Management (IAM). This sprint moves beyond data structures and REST design to implement a robust security perimeter using Spring Security and JSON Web Tokens (JWT).

The primary goal was to ensure data isolation: a multi-tenant environment where users can only interact with their own data, supported by a stateless authentication mechanism and a secure token refresh cycle.

### Concepts & Architecture
This project implements a defense-in-depth strategy to protect user data:

#### 1. Stateless Authentication with JWT
Unlike traditional session-based security, this API is completely stateless:
```
a. Access Tokens: Short-lived tokens (1 hour) used to authorize every request via the 'Authorization: Bearer' header.
b. Refresh Tokens: Long-lived tokens (24 hours) stored in the database, allowing users to obtain new access tokens without re-entering credentials.
c. Custom JWT Filter: A specialized filter intercepting every request to validate the token's signature, expiration, and claims before reaching the controller.
```
#### 2. Role-Based Access Control (RBAC)
The application enforces strict authorization boundaries:
```
a. User Role: Can perform CRUD operations on their own notes.
b. Admin Role: Can access system-wide statistics and user management endpoints.
c. Method-Level Security: Use of @PreAuthorize to lock down sensitive administrative routes.
```
#### 3. Data Isolation & Ownership
The system ensures a user cannot access another user's notes, even if they guess the Note ID:
```
a. Ownership Validation: The service layer queries data using both Note ID and User ID (e.g., findByIdAndUserId).
b. Security Context: The application extracts the "Subject" from the JWT to identify the current user, rather than trusting a User ID passed in a request body.
```
#### 4. Cryptography & Persistence
```
a. Password Hashing: Utilizes BCrypt with a random salt to ensure passwords are never stored in plain text.
b. Database-Backed Refresh Tokens: Allows the server to revoke access if a refresh token is compromised.
c. JPA Auditing: Automatically tracks 'createdAt' and 'updatedAt' timestamps for both users and notes.
```
### Tech Stack
Language: Java 21

Framework: Spring Boot 3.2+ (Security, Data JPA)

Security: Spring Security 6, JJWT (Java JWT)

Database: H2 (In-Memory SQL)

Build Tool: Maven

Utilities: Lombok, BCrypt
