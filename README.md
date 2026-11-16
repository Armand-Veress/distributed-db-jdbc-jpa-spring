# Distributed expense manager — JDBC / JPA / Spring

A small distributed application that manages expenses split across **three databases on two database servers**.
Each expense category lives in its own database, and each database is accessed with a different technology:

| Category | Database | Server | Accessed through |
|---|---|---|---|
| AUTO | `expenses_auto` | PostgreSQL (local) | **JDBC** |
| FOOD | `expenses_food` | PostgreSQL (local) | **JPA** (Hibernate) |
| BILLS | `expenses_bills` | MySQL (Docker) | **REST** call to a **Spring Boot** service (Spring Data JPA) |

## Architecture

```
console-app-auto-food (console menu)
   --- JDBC ----------> PostgreSQL :5432 / expenses_auto
   --- JPA -----------> PostgreSQL :5432 / expenses_food
   --- HTTP/JSON -----> spring-service-bills :8080 -----> MySQL :3306 / expenses_bills
                        (Docker)                        (Docker)
```

## Project structure

```
distributed-db-jdbc-jpa-spring/
|--- pom.xml                      # parent Maven project (2 modules)
|--- docker-compose.yml           # MySQL + spring-service-bills containers
|--- .env.example                 
|--- console-app-auto-food/       # console app: menu, service, JDBC / JPA / REST repositories
|   |--- db/expense.sql           # PostgreSQL schema (used for AUTO and FOOD)
|   |--- src/main/resources/
|       |--- db.properties.example
|       |--- META-INF/persistence.xml
|--- spring-service-bills/        # Spring Boot service for BILLS
    |--- Dockerfile
    |--- src/main/resources/
        |--- application.properties
        |--- schema.sql           # MySQL schema
        |--- templates/bills.html # Thymeleaf page
```

## How to run

**Requirements:** JDK 21+, Docker, PostgreSQL (local, port 5432)

**Optional:** pgAdmin (for creating the user and 2 postgres databases), IntelliJ (for running the console app)

1. Clone repo
2. Create a db user and then the `expenses_auto` and `expenses_food` databases using the PostgreSQL schema in `console-app-auto-food/db/expense.sql`
3. Fill out the credentials attributes from `db.properties.example` in a new `db.properties`.
4. Fill out the credentials attributes from `.env.example` in a new `.env` file.
5. Navigate in root folder and start the docker containers:
    ```bash
    docker compose up -d --build
    ```
6. Run `Main` from `console-app-auto-food`.

- Web page for bills: http://localhost:8080/bills
- REST API for bills: http://localhost:8080/api/expenses

Console functions:
1. List all expenses sorted descending by amount
2. List averages for all expense categories
3. Add new expense

When stopping the BILLS container (`docker compose stop bills-service`), the console application should display results from the 2 Postgres databases and report that the `expenses_bills` source is not reachable.