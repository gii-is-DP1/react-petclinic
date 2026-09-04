# React Petclinic

Teaching project for the Design and Testing 1 course in the Software Engineering
degree at the University of Seville. It combines a Spring Boot REST API, JWT
authentication, and a React single-page application.

This repository is based on
[Spring Petclinic](https://github.com/spring-projects/spring-petclinic). It has
been simplified and extended with REST controllers, role-based access, clinics,
consultations, pricing plans, and a React frontend.

## Technologies and versions

| Component | Version | Notes |
| --- | --- | --- |
| Java | 21 | Target language and bytecode version. |
| JDK | 21 or 25 | Maven compiles with `--release 21` in both cases. |
| Maven | 3.9.16 | Included through Maven Wrapper. |
| Spring Boot | 3.5.16 | Backend and REST API. |
| Node.js | 24.x | Maven uses Node 24.16.0 in the `frontend` profile. |
| npm | 11 or 12 | Maven uses npm 12.0.2 in the `frontend` profile. |
| React | 18.3.1 | User interface. |
| Vite | 8.2.2 | Development server and production build. |
| Vitest | 5.0.0 | Frontend tests. |

JDK 25 can run the build without changing its compatibility target. The
`maven.compiler.release=21` property prevents accidental use of Java APIs newer
than Java 21.

## Requirements

- Git.
- JDK 21 or JDK 25. JDK 21 is recommended for a shared team setup.
- Node.js 24 and npm 11 or 12 when running frontend commands directly.
- An IDE with Maven, Java, and React support.

The Maven `frontend` profile downloads its own Node and npm distributions into
`target/`. A global Node installation is therefore unnecessary for Maven builds,
but it is needed for day-to-day work inside `frontend/`.

Check the local environment with:

```console
java --version
node --version
npm --version
```

Check which JDK Maven is using:

```bash
./mvnw --version
```

On Windows PowerShell:

```powershell
.\mvnw.cmd --version
```

The Maven output must report Java 21 or Java 25. If it does not, review
`JAVA_HOME` and the JDK configured in the IDE.

## Getting and verifying the project

On Linux, macOS, or Git Bash:

```bash
git clone https://github.com/gii-is-DP1/react-petclinic.git
cd react-petclinic
./mvnw clean verify -Pfrontend
```

On Windows PowerShell:

```powershell
git clone https://github.com/gii-is-DP1/react-petclinic.git
Set-Location react-petclinic
.\mvnw.cmd clean verify -Pfrontend
```

The complete verification compiles and tests the Java backend, installs the
exact frontend dependencies with `npm ci`, runs the Vitest suite, builds the
Vite production bundle, copies it into the Spring Boot application, and creates
the test reports. The first run takes longer because it downloads the toolchains
and dependencies.

Frontend integration is opt-in. Maven commands without `-Pfrontend` operate on
the backend only, keeping common commands fast:

```bash
# Backend only
./mvnw clean install

# Backend and frontend
./mvnw clean install -Pfrontend
```

## Local development

Run the backend and frontend in separate terminals to get fast frontend reloads.

### Start the backend

From the repository root:

```bash
./mvnw spring-boot:run
```

On Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

The backend starts at <http://localhost:8080>. This command does not install or
start the frontend.

### Start the frontend

```bash
cd frontend
npm ci
npm run dev
```

The application starts at <http://localhost:5173>. Vite proxies `/api` and
`/v3/api-docs` to the backend on port 8080. Run `npm ci` after cloning and after
every change to `package-lock.json`.

## Useful URLs

| Resource | URL |
| --- | --- |
| Frontend development server | <http://localhost:5173> |
| Packaged application | <http://localhost:8080> |
| Swagger UI | <http://localhost:8080/swagger-ui/index.html> |
| OpenAPI specification | <http://localhost:8080/v3/api-docs> |
| H2 console | <http://localhost:8080/h2-console> |

The H2 console uses `jdbc:h2:mem:testdb`, user `sa`, and an empty password.

## Demo users

The database is populated from `src/main/resources/data.sql` at startup.

| Role | User name | Password |
| --- | --- | --- |
| Administrator | `admin1` | `4dm1n` |
| Clinic owner | `clinicOwner1` through `clinicOwner3` | `clinic_owner` |
| Pet owner | `owner1` through `owner10` | `0wn3r` |
| Veterinarian | `vet1` through `vet6` | `v3t` |

The default H2 database is in memory and uses
`spring.jpa.hibernate.ddl-auto=create-drop`, so its contents are recreated when
the backend restarts.

## Building the complete application

```bash
./mvnw clean package -Pfrontend
java -jar target/spring-petclinic-2.2.0.BUILD-SNAPSHOT.jar
```

On Windows:

```powershell
.\mvnw.cmd clean package -Pfrontend
java -jar target/spring-petclinic-2.2.0.BUILD-SNAPSHOT.jar
```

Vite creates `frontend/dist/`; Maven copies that directory into
`target/classes/static/` before packaging the JAR. The resulting application is
served entirely from <http://localhost:8080>.

## Testing and quality

Run backend tests only:

```bash
./mvnw test
```

Run backend and frontend tests:

```bash
./mvnw test -Pfrontend
```

Run the complete lifecycle and generate reports:

```bash
./mvnw clean verify -Pfrontend
```

Important outputs include:

- `target/site/jacoco/`: Java coverage report.
- `target/site/test-status/`: Allure test status report.
- `docs/deliverables/D3/coverage/`: copied Java coverage report.
- `docs/deliverables/D3/status/`: copied test status report.
- `frontend/dist/`: frontend production bundle.

For frontend-only work:

```bash
cd frontend
npm test
npm run test:watch
npm run coverage
npm run build
```

## Database profiles

The default profile uses an in-memory H2 database. MySQL settings are available
in `src/main/resources/application-mysql.properties`, and a local database can
be started with the provided `docker-compose.yml` when needed.

Never commit production credentials or JWT secrets. The JWT value in
`application.properties` is a Base64-encoded development key and must be
overridden in deployed environments.

## Repository structure

```text
frontend/                  React application, Vite configuration, and tests
src/main/java/             Spring Boot application
src/main/resources/        configuration and seed data
src/test/java/             backend tests
src/test/jmeter/           performance test plan
docs/                      diagrams and course deliverables
pom.xml                    Maven build and frontend profile
```

## Troubleshooting

If Maven uses the wrong Java version, correct `JAVA_HOME`, restart the terminal,
and check `.\mvnw.cmd --version` (Windows) or `./mvnw --version` again.

If Node or npm reports an unsupported version, use a version manager from the
`frontend/` directory. Both `.nvmrc` and `.node-version` pin Node 24.16.0.

If frontend dependencies are inconsistent, do not repair `node_modules`
manually. Run `npm ci` to recreate it from `package-lock.json`.

If the frontend cannot reach the API, confirm that Spring Boot is listening on
port 8080 and that Vite is running on port 5173.

## Additional documentation

- [Frontend guide](frontend/README.md)
- [Requirements analysis](docs/deliverables/Análisis%20de%20requisitos%20del%20sistema.md)
- [System design](docs/deliverables/Diseño%20del%20Sistema.md)
- [Layer package diagram](docs/diagrams/LayersUMLPackageDiagram.iuml)
