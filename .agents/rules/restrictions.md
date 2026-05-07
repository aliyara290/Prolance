---
trigger: always_on
---

# GEMINI.md

## 🧠 Project Overview

This project is a **multi-tenant SaaS platform** for managing clients, projects, tasks, time tracking, and billing.

The system is built using:

* Microservices architecture
* Hexagonal architecture (Ports & Adapters)
* Spring Boot (Web MVC for services)
* Spring Cloud Gateway (WebFlux)
* Keycloak (authentication & authorization)
* PostgreSQL (per service database)

---

## 🏗️ Global Architecture Rules

### 1. Multi-Tenancy (CRITICAL)

* Every entity MUST include `tenant_id` (tenant identifier)
* Tenant is extracted from JWT (Keycloak)
* No cross-tenant data access is allowed
* All queries MUST be tenant-scoped

---

### 2. Service Independence

* Each service has its own database
* No direct database sharing between services
* Communication via:

  * REST APIs (Feign Client)
  * Events (Kafka)

---

### 3. Hexagonal Architecture

Each service must follow:

```
├── domain
│   ├── model
│   │   ├── aggregate
│   │   ├── entity
│   │   ├── valueobject
│   │   └── event
│   │
│   │
│   └── service       // Domain services (pure business logic)

├── application
│   ├── service       // Use case implementations
│   ├── dto           // Request/Response objects
│   ├── port
│   │   ├── in        // Use cases (input ports)
│   │   └── out       // Interfaces for external world
│   ├── mapper        // DTO <-> Domain

├── infrastructure
│   ├── adapter
│   │   ├── in        // Controllers, REST
│   │   └── out       // DB, external APIs, messaging
│   │
│   ├── persistence
│   │   ├── entity    // JPA entities
│   │   ├── jpa
│   │   └── mapper
│   │
│   ├── security      // JWT, Keycloak, filters
│   └── config        // Spring configs, beans

└── bootstrap
    └── Application.java
```

Rules:

* `domain` → pure business logic (NO framework)
* `application` → use cases
* `infrastructure` → DB, REST clients, Keycloak integration

---

### 4. Shared Modules

* `common-dto` → ONLY for API contracts and events
* No business logic in shared modules
* Versioned as a Maven dependency

---

### 5. Security

* Authentication handled by Keycloak
* Services only validate JWT
* Authorization handled at each service
* Extract the `tenantId` from jwt tokenand store it in ThreadLocal in each service
* Extract roles from keycloak jwt token and store it in the GrantedAuthority to use with `@PreAuthorize`
* 

---

## 🚪 API Gateway

### Tech:

* Spring Cloud Gateway (WebFlux)

### Responsibilities:

* Route requests to services
* Validate JWT tokens
* Centralized logging & monitoring

### Must NOT:

* Contain business logic
* Access databases

---

## 🏢 Tenant Service

### Purpose:

Manages tenant (companies) and their users.

### Responsibilities:

* Company registration
* User invitation (keycloak creation)
* Role management
* Mapping users to Keycloak

### Domain:

* Aggregate Root: `Tenant`
* Entities:

  * TenantUser
  * UserPreference
  * TenantLog
  * Role
  * TenantSettings

### Rules:

* A user belongs to ONE tenant
* Email must be unique per tenant
* Cannot exceed tenant plan limits

---

## 👥 CRM Service

### Purpose:

Manages client relationships before projects exist.

### Responsibilities:

* Clients (companies you work with)
* Contacts (people inside clients)
* Leads & Opportunities

### Flow:

Lead -> (Qualify) → Opportunity → Won → Project creation

### Rules:

* Only Qualify leads can create opportunity
* Only “WON” opportunities can create projects
* Opportunities must belong to a client

---

## 📁 Project Service

### Purpose:

Manages projects lifecycle.

### Responsibilities:

* Create project (manual or from opportunity)
* Manage milestones
* Track project status

### Rules:

* Project belongs to a company
* Project must have start date
* Cannot exceed company limits

---

## ✅ Task Service

### Purpose:

Handles execution layer.

### Responsibilities:

* Tasks & Subtasks
* Assign users
* Manage status workflow

### Workflow:

To Do → In Progress → Done -> ..

### Rules:

* Task must belong to a project
* Assigned user must belong to same company
* Task dependencies must not create cycles

---

## ⏱️ Time Tracking Service

### Purpose:

Tracks work effort.

### Responsibilities:

* Log time per task
* Track user activity

### Rules:

* Time must be linked to a task
* No negative or overlapping time entries

---

## 🔔 Notification Service

### Purpose:

Handles system notifications.

### Responsibilities:

* Notify on:

  * task assignment
  * status change
* Can use:

  * polling OR WebSocket

---

## 💰 Billing Service

### Purpose:

Handles invoicing (no payments).

### Responsibilities:

* Generate invoices
* Track invoice status
* Calculate totals

### Rules:

* Invoice linked to project
* Supports:

  * fixed price
  * hourly billing (via time tracking)

---

## 📡 Communication Rules

### Synchronous:

* REST (Feign/WebClient)

### Asynchronous (optional):

* Kafka / RabbitMQ

Events must be:

* immutable
* versioned

---

## 🧪 Development Rules

* No business logic in controllers
* No direct entity exposure in APIs
* Use DTOs for communication
* Validate input at application layer
* Log important actions (audit)

---

## 💣 Anti-Patterns (STRICTLY FORBIDDEN)

* Sharing databases between services
* Using `@Data` in domain models
* Putting business logic in DTOs
* Skipping tenant validation
* Tight coupling between services

---

## 🚀 Goal

Build a **scalable, maintainable, production-ready SaaS platform** with strict boundaries and clean architecture.
