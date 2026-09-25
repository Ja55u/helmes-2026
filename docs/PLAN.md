# Implementation plan

This is the build plan for the sectors form assignment. Requirements are in `README.md`; the
original, deficient form is kept as-is at `docs/original-index.html` for reference. The plan is
organized by layer, in the order they were built: database, backend, frontend, then verification.

---

## Database

PostgreSQL 16, run via Docker Compose, schema managed with Flyway.

- `docker-compose.yml` at the repo root starts a single `db` service (`postgres:16-alpine`),
  db/user/password `helmes`/`helmes`/`helmes`, port 5432, with a healthcheck.
- `backend/src/main/resources/db/migration/V1__schema.sql` creates `sector` (`id`, `name`,
  `parent_id` self-reference, `sort_order`), `submission`, and the `submission_sector` join table.
- `V2__seed_sectors.sql` seeds all 79 sectors with their original ids and hierarchy, carried over
  from the `<option>` list in `docs/original-index.html` (`parent_id` from indentation depth,
  `sort_order` from position). Keeping the original ids means they stay the natural, stable
  identifiers rather than being renumbered.
- Hibernate runs with `ddl-auto: validate` — Flyway owns the schema, the app never generates DDL.

**Check:** `docker compose up -d db`, then `docker compose exec db psql -U helmes -d helmes -c
"select count(*) from sector"` returns 79, and `... where parent_id is null` returns 3.

---

## Backend

Java 21, Spring Boot 3.5 (Web, Data JPA, Validation), Gradle, package `ee.helmes.sectors`
organized by feature (`sector/`, `submission/`, `common/`).

### Sectors API

- `Sector` entity, `SectorRepository`.
- `SectorService` flattens the tree into a pre-order list (parent before its children) with a
  computed `depth`.
- `SectorController` exposes `GET /api/sectors` returning `[{ id, name, depth }, ...]`.

### Submission API

- `Submission` entity with a many-to-many relation to `Sector` via `submission_sector`.
- `SubmissionRequest` (Bean Validation: `name` required/max 100 chars, `sectorIds` non-empty and
  must all exist, `agreeToTerms` must be `true`) and `SubmissionResponse`.
- `SubmissionService` validates, saves, and looks up a submission by id.
- `SubmissionController` uses the servlet `HttpSession` to remember which submission this browser
  session created:
  - `GET /api/submission` returns the saved data for this session, or `204` if nothing was saved
    yet.
  - `PUT /api/submission` validates and creates or updates the session's submission, returning the
    saved data (same shape as `GET`) so the form can be refilled from the response.
  - The database id itself is never sent to the client — only the session cookie ties a browser to
    its submission.
- `ApiExceptionHandler` turns validation failures into `400 { "message": "Validation failed",
  "errors": { "<field>": "<message>" } }`.

### Tests

JUnit 5 + MockMvc + Testcontainers (Postgres), in `backend/src/test/java`:

- `SectorControllerIT` — the sectors endpoint returns all 79 rows in pre-order with correct depth.
- `SubmissionControllerIT` — validation errors per field, a successful save, refill from `GET`
  after `PUT`, editing an existing submission within a session, and a fresh session getting `204`.

**Check:** `./gradlew test` passes. With the app running, `curl http://localhost:8080/api/sectors`
returns 79 items, starting with `{"id":1,"name":"Manufacturing","depth":0}`.

---

## Frontend

Vite + React 19 + TypeScript, plain CSS, no UI/component library. Form state and validation go
through react-hook-form + zod; data fetching and the save mutation go through TanStack Query,
rather than hand-rolled `fetch` + `useState` bookkeeping.

- Vite dev server (port 5173) proxies `/api` to the backend (port 8080), so there's no CORS and
  the session cookie is same-origin.
- `src/types.ts` — shared types for a sector and a submission.
- `src/api.ts` — thin wrapper around `fetch` for the three endpoints; used as `queryFn`/`mutationFn`
  by TanStack Query.
- `src/validation.ts` — `submissionSchema`, a zod schema with the same rules and messages as the
  backend, passed to react-hook-form via `zodResolver` so client-side errors match the server's
  exactly. Also exports `validate(data)`, a thin wrapper around the schema kept for direct testing.
- `src/main.tsx` — wraps `<App />` in a `QueryClientProvider`.
- `src/components/SectorForm.tsx` (+ `.css`) — the form itself:
  - `useQuery` loads sectors (`GET /api/sectors`) and the session's existing submission
    (`GET /api/submission`) independently; a combined loading/error state gates the form.
  - Sectors render as a nested multi-select (`<select multiple>`), indented with non-breaking
    spaces (`\u00A0`, not regular spaces — browsers can collapse leading regular whitespace inside
    `<option>` text), with each option's full path shown on hover so same-named entries (e.g. the
    three "Other" sectors) are distinguishable.
  - `useForm` (react-hook-form) with `zodResolver(submissionSchema)` manages form state; the
    multi-select is wired through `Controller` since a native multi-select needs custom
    get/set logic that plain `register` doesn't handle.
  - When the submission query resolves with existing data, a `useEffect` calls `reset()` to refill
    the form.
  - Proper `<form>`, `<label htmlFor>` on every control, and a real `<input type="checkbox">` for
    "Agree to terms" so clicking the label toggles it.
  - `useMutation` wraps `PUT /api/submission`. On success it calls `reset()` with the response
    (refilling from the server's copy) and shows a success message; on a 400 it maps each field
    error onto the form via `setError()`.
- `src/App.tsx` renders `<SectorForm />`; `src/index.css` holds base page styles.

### Fixes applied relative to the original form

The original `index (1).html` (kept at `docs/original-index.html`) had these problems, all
addressed above:

1. Not a valid HTML document — missing `<!DOCTYPE html>`, `<html lang>`, `<head>`, `<meta
   charset>` (so `Children's` rendered garbled), viewport, and `<title>`.
2. No `<form>` element — the Save button submitted nothing, and `<input type="submit">` sat
   outside any form.
3. Inputs had no `name`/`id`, so no data would have been sent even inside a form.
4. No `<label>` elements — captions weren't linked to their controls (bad for screen readers),
   and clicking "Agree to terms" didn't toggle the checkbox.
5. No validation, despite every field being mandatory.
6. Sectors were hardcoded in the HTML instead of coming from a database.
7. The hierarchy was faked with literal `&nbsp;` runs in the markup instead of real parent/child
   data.
8. Trailing whitespace in several option labels (e.g. `"Fish & fish products "`).
9. Three options named "Other" with no way to tell them apart.
10. `<br />` used for layout.
11. No feedback on success or failure, and no hint on how to multi-select.

### Tests

Vitest + React Testing Library + jsdom, in `frontend/src`:

- `validation.test.ts` — each validation rule, valid and invalid cases.
- `SectorForm.test.tsx` — sectors render nested from the API, submitting invalid data shows
  field errors, a successful submit shows a success state, and an existing session's data refills
  the form on load. A `renderForm()` helper wraps the component in its own `QueryClientProvider`
  per test (`retry: false`, so a mocked rejection fails the query immediately instead of retrying).

**Check:** `npm run build` succeeds with no TypeScript errors; `npm test -- --run` passes.

---

## Verification and deliverables

With Postgres, the backend, and the frontend all running:

- Submit the form with a missing name, no sectors selected, and terms unchecked — confirm all
  three field errors appear, matching the messages in the table above.
- Submit a valid form — confirm a success state, and that reloading the page refills the same
  data (same session).
- Open a private/incognito window — confirm the form starts empty (different session).
- `database/dump.sql` — full `pg_dump` of the schema and data (`sector`, `submission`,
  `submission_sector`, `flyway_schema_history`), including at least one real submission created
  during verification.
- `README.md` — keeps the original task text, plus "Tech stack", "How to run", "Task 1 –
  deficiencies fixed", "Design notes", and "Deliverables" sections.
