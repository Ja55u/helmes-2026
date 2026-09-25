# Helmes-2026

Tasks:
1. Correct all of the deficiencies in index.html

2. "Sectors" selectbox:
   2.1. Add all the entries from the "Sectors" selectbox to database
   2.2. Compose the "Sectors" selectbox using data from database

3. Perform the following activities after the "Save" button has been pressed:
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

## Task 1 – deficiencies fixed

The original `docs/original-index.html` had the following problems, all fixed in the React form:

1. Is not a valid HTML document: no `<!DOCTYPE html>`, `<html lang>`, `<head>`, `<meta charset>` (so `Children's` could render garbled), viewport, or `<title>`.
2. Has no `<form>` element, so the Save button submits nothing, and it uses `<input type="submit">` outside any form.
3. Inputs have no `name`/`id`, so no data would be sent even inside a form.
4. Has no `<label>` elements. The captions aren't linked to their controls, which is bad for screen readers, and clicking "Agree to terms" doesn't toggle the checkbox.
5. Has no validation, even though all fields are mandatory.
6. Hardcodes the sectors in HTML (they now come from the database).
7. Fakes the hierarchy with literal `&nbsp;` runs in the markup (it's now generated from the parent/child data in the database).
8. Has trailing whitespace in several option labels (e.g. `"Fish & fish products "`).
9. Has three options called "Other" that can't be told apart (each option now shows its full path on hover).
10. Uses `<br />` for layout.
11. Gives no feedback on success or failure, and no hint on how to multi-select.

## Design notes

- Sector ids and their hierarchy are kept from the original select box (`parent_id` self-reference), so they remain the natural, stable ids rather than newly generated ones.
- "Allow the user to edit his/her own data during the session" is implemented with a server-side `HttpSession` (`JSESSIONID` cookie): the session stores the id of the submission it created, and the database id is never exposed to the client. A new browser session (e.g. a private window) always starts with an empty form.
- Validation rules and messages are defined once per side and kept in sync by hand: `frontend/src/validation.ts` (a zod schema used by react-hook-form) mirrors the Bean Validation rules on `backend/.../submission/SubmissionRequest.java`. The client runs its copy first for immediate feedback; the server's is authoritative, since the client can't be trusted.
- The sectors `<select multiple>` indents child options with non-breaking spaces (`\u00A0`), not regular spaces — browsers (and some assistive tech) can collapse leading regular whitespace inside `<option>` text, so a plain space silently loses the indentation.
- Data fetching and the save mutation go through TanStack Query (`useQuery`/`useMutation`) rather than hand-rolled `fetch` + `useState` bookkeeping; form state and validation go through react-hook-form with a zod resolver rather than manually tracked fields.

## Deliverables

- **Database dump**: [`database/dump.sql`](database/dump.sql) — full schema (`sector`, `submission`, `submission_sector`, `flyway_schema_history`) and data, including real submissions created while verifying the app.
- **Source code**: [`database/`](database) (Flyway migrations live under `backend/src/main/resources/db/migration`), [`backend/`](backend) (Spring Boot API), [`frontend/`](frontend) (React UI).
