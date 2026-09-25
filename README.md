# Helmes-2026

Tasks:
1. Correct all of the deficiencies in index.html

2. "Sectors" selectbox:
   
   2.1. Add all the entries from the "Sectors" selectbox to database
   
   2.2. Compose the "Sectors" selectbox using data from database

4. Perform the following activities after the "Save" button has been pressed:
   
   3.1. Validate all input data (all fields are mandatory)
   
   3.2. Store all input data to database (Name, Sectors, Agree to terms)
   
   3.3. Refill the form using stored data
   
   3.4. Allow the user to edit his/her own data during the session
   

Write us Your best code!

After completing the tasks, please provide us with:
1. Full database dump (structure and data)
2. Source code

---

## Tech stack

| Layer    | Technology |
|----------|------------|
| Database | PostgreSQL 16, run via Docker Compose, schema managed with Flyway |
| Backend  | Java 21, Spring Boot 3.5 (Web, Data JPA, Validation), Gradle |
| Frontend | React 19 + TypeScript, built with Vite, plain CSS (no UI/component library). react-hook-form + zod for form state and validation, TanStack Query for data fetching and the save mutation |
| Tests    | Backend: JUnit 5 + MockMvc + Testcontainers (Postgres). Frontend: Vitest + React Testing Library |

## How to run

Prerequisites: Docker Desktop, JDK 21, Node 20+.

```bash
docker compose up -d db                 # start Postgres on :5432

cd backend
./gradlew bootRun                       # API on :8080 (JAVA_HOME must point at a JDK 21)

cd frontend
npm install
npm run dev                             # UI on :5173, proxies /api to :8080
```

Open `http://localhost:5173`.

Tests:

```bash
cd backend;  ./gradlew test              # backend unit + integration tests (needs Docker)
cd frontend; npm test -- --run          # frontend tests
cd frontend; npm run build              # type-check + production build
```

## Deliverables

- **Database dump**: [`database/dump.sql`](database/dump.sql) — full schema (`sector`, `submission`, `submission_sector`, `flyway_schema_history`) and data, including real submissions created while verifying the app.
- **Source code**: [`database/`](database) (Flyway migrations live under `backend/src/main/resources/db/migration`), [`backend/`](backend) (Spring Boot API), [`frontend/`](frontend) (React UI).
