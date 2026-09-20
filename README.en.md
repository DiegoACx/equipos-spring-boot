<p align="center">
  <img src="https://img.shields.io/badge/Java-17-ED8B00?logo=openjdk&logoColor=white" alt="Java 17">
  <img src="https://img.shields.io/badge/Spring%20Boot-4.1.1-6DB33F?logo=springboot&logoColor=white" alt="Spring Boot 4.1.1">
  <img src="https://img.shields.io/badge/Thymeleaf-3.1-005F0F?logo=thymeleaf&logoColor=white" alt="Thymeleaf">
  <img src="https://img.shields.io/badge/H2-in--memory-1021FF" alt="H2 in-memory">
</p>

<h1 align="center">IT Equipment Registry</h1>

<p align="center">Academic IT inventory CRUD built with Spring Boot, Thymeleaf and in-memory H2.</p>

<p align="center">🇪🇸 <a href="README.md">Leer en español</a></p>

## About

A Spring Boot web application to register IT equipment (an IT inventory): it lets you create, list, edit and delete equipment through server-rendered Thymeleaf views. Data lives in an in-memory H2 database and the app runs on port 8082.

It is a coursework project for *Infraestructura Tecnológica* (5th semester) at Universidad Autónoma de Bucaramanga (UNAB), reviewed and fixed in September 2026.

### What it is NOT

- **Not an inventory product:** it is an academic CRUD.
- **No persistence:** data is lost on restart.
- **No authentication or CSRF protection:** it does not use Spring Security.
- **Not a REST API:** there is no `@RestController` and no JSON responses.

## Features

| Method | Route | What it does |
|---|---|---|
| GET | `/`, `/index`, `/menu` | Home page |
| GET | `/verequipo` (also `/mostrarequipo`, `/listarequipo`) | Equipment list. **Recommended entry URL** |
| GET | `/verequipo/formequipo` | Form for a new item |
| POST | `/guardarequipo` | Validates and saves; used for both create and edit. On errors it returns to the form with messages |
| GET | `/equipo/editar/{id}` | Form with the item loaded. 404 if the id does not exist |
| POST | `/equipo/eliminar/{id}` | Deletes and redirects to the list. GET returns 405 |
| GET | `/h2-console` | `dev` profile only. Without it: 404 |

- **Validation (server side):** `nombre`, `categoria`, `marca` and `estado` cannot be blank; `precio` is required and greater than zero; `observaciones` is optional. Messages are in Spanish.
- **Form and list:** the price input accepts cents, and the list shows ID, name, category, brand, price, status and notes.
- **Categories** (fixed list in the HTML): Laptop, PC de escritorio, Servidor, Impresora, Accesorios. **Statuses:** Nuevo, En estado, En reparación, Obsoleto. The server does not check that the value belongs to those lists, only that it is not blank.
- **Delete confirmation:** browser-side only (JavaScript). Without JavaScript, the POST deletes directly.
- **`dev` profile:** enables the H2 console at `http://localhost:8082/h2-console` (JDBC URL `jdbc:h2:mem:equiposdb`, user `sa`, empty password). Do not use it on an exposed server or Docker container.

## Tech stack

Direct versions come from `pom.xml`; transitive ones from `dependency:list` on 2026-09-20.

| Component | Version |
|---|---|
| Java | 17 |
| Spring Boot (parent) | 4.1.1 |
| Embedded Tomcat | 11.0.25 (override; the BOM ships 11.0.24) |
| Spring Framework | 7.0.9 |
| Hibernate ORM | 7.4.5.Final |
| Hibernate Validator | 9.1.3.Final |
| Thymeleaf | 3.1.5.RELEASE |
| H2 (runtime) | 2.4.240 |
| Maven Wrapper | 3.3.2 (downloads Maven 3.9.9) |
| Tests | JUnit Jupiter 6.0.3, Mockito 5.23.0, AssertJ 3.27.7 |

`pom.xml` pins no dependency version (they all come from the Spring Boot BOM), except `tomcat.version`.

**Tested JDKs:** 17.0.10 and 21.0.3. Other versions were not tested.

## Project structure

```
.
├─ .mvn/wrapper/
├─ src/
│  ├─ main/    (java/com/quiz/equipos: controller, entities, repository; resources)
│  └─ test/    (java/com/quiz/equipos)
├─ Dockerfile · .dockerignore
├─ mvnw · mvnw.cmd · pom.xml
└─ .gitattributes · .gitignore
```

There is no service layer: the controller uses the repository directly.

## How to run (Windows, PowerShell)

Requires JDK 17 or later and internet on first run (the wrapper downloads Maven and the dependencies).

```powershell
# Tests
.\mvnw.cmd -B clean verify

# Run in development (tested by the author)
.\mvnw.cmd spring-boot:run

# Package and run the jar (~53 MB)
.\mvnw.cmd -B clean package
java -jar target\equipos-0.0.1-SNAPSHOT.jar

# With the H2 console (dev profile)
java -jar target\equipos-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
```

- **Entry URL:** http://localhost:8082/verequipo
- **Data is lost on restart** (in-memory H2).
- To use JDK 17 for the session only: `$env:JAVA_HOME = 'C:\Program Files\Java\jdk-17'`.
- `-Dspring-boot.run.profiles=dev` with `spring-boot:run` is documented but **not verified**.

## Docker

The `Dockerfile` is multi-stage: it builds with `maven:3.9-eclipse-temurin-17` (`mvn clean package -DskipTests`) and runs on `eclipse-temurin:17-jre` as a non-root user, exposing port 8082. The jar is built inside the image, so it inherits the Tomcat override.

**Not tested:** Docker is not installed on the development machine, so no image was built and the non-root user creation was not tried. Only the fact that `target/*.jar` matches a single file was checked. Image tags are floating and their contents change over time. There is no `docker-compose`.

## What was tested and what was not

**Automated** (Windows 11, before and after the Tomcat override):

- 17 tests: 1 context-load, 11 with MockMvc (`@WebMvcTest`) and 5 repository tests (`@DataJpaTest`). They pass on JDK 17.0.10 and 21.0.3.
- HTTP checks against the real jar, with and without the `dev` profile, on both JDKs: home, list and form return 200; editing a missing id returns 404; deleting via GET returns 405; a valid POST saves and an invalid one shows errors.
- H2 console: `/h2-console` returns 404 without the profile and 302 with `dev`.

**Manual test by the author** (not covered by the tests): in the browser, with Spring Boot 4.1.1 and repeated with Tomcat 11.0.25, using `spring-boot:run`. It worked well: create with a decimal price, submit an empty form, edit, delete and open `/`.

**Not verified:**

- Docker and `docker run`.
- `spring-boot:run` with `-Dspring-boot.run.profiles=dev`.
- Logging into the H2 console as `sa` (only the 302 was checked).
- Other browsers, Linux or macOS, other JDKs, multiple users, performance and accessibility.

## Limitations and risks

- **No authentication or CSRF.** Deletion uses POST without a token, and the confirmation is JavaScript only.
- **CDN front end, no `integrity` attributes and not audited:** Bootstrap 4.5.0, jQuery 3.5.1 slim, `@popperjs/core` 2.5.3 and Font Awesome (unpinned version, plus an external kit script). Without internet the app works but loses its styling.
- **`open-in-view` is on:** Spring warns about it on every startup.
- **Mockito warning on JDK 21:** it self-attaches as an agent, which future JDKs will block. It does not appear on JDK 17.
- **Data model:** `precio` is a `Double` (not `BigDecimal`), the identifier field is named `Id`, and there is no pagination or search.

### Dependency audit

- **Tool and scope:** OSV, on 2026-09-20, over 122 resolved artifacts (73 compile, 12 runtime, 37 test), with JDK 17.
- **Finding:** `tomcat-embed-core` 11.0.24 (the one in the Spring Boot 4.1.1 BOM) had 3 critical advisories, CVE-2026-65905, CVE-2026-65182 and CVE-2026-68525, fixed in 11.0.25. By their descriptions, they affect Tomcat's authentication and security constraints, which this app does not configure. Exploitability was not tested.
- **Override:** `tomcat.version` = 11.0.25. With it, OSV reports 0 advisories. **It is not officially validated with Spring Boot 4.1.1:** only the 17 tests and a real startup cover it. It should be removed once Spring Boot ships a patch with Tomcat 11.0.25 or later, and nothing notifies when that happens.
- **What the audit does not cover:** it only queries OSV (published advisories; "0" does not mean "no vulnerabilities"); it does not cover Maven plugins or the wrapper's Maven, the CDN front end, the Docker base image, the JDK, or the app's code and configuration.

## Project history

The original is a coursework project. In September 2026 the following was fixed:

- **Route `/`:** the home class was not registered as a controller and the "View equipment" button pointed to a non-existent route. It now works and links to `/verequipo`.
- **Editing a missing id:** returned a 500 error. It now returns 404.
- **Delete:** was a GET link. It is now a POST form.
- **Validation:** there was none. `spring-boot-starter-validation` was added, the price accepts cents and the list shows the notes.
- **Configuration:** there were trivial sample credentials for an in-memory database. It now uses `sa` with no password, and the H2 console is only in the `dev` profile.
- **Dockerfile:** pointed to a jar from another project that does not exist. It is now multi-stage.
- **Other:** 17 tests, `.env` in `.gitignore`, executable `mvnw` and a `.dockerignore`.
- **Migration:** from Spring Boot 3.4.5 (out of OSS support since 2025-12-31), through 3.5.16, to 4.1.1. Changes: `spring-boot-starter-web` became `spring-boot-starter-webmvc`, per-technology test starters, two relocated imports (`@WebMvcTest` and `@DataJpaTest`) and `spring-boot-h2console` so the `dev` profile kept working. The application code did not change. The test packages and the H2 console were not in the official migration guide; they were resolved by inspecting the JARs and running the app.
- **Security patch:** Tomcat override to 11.0.25.

## License

This repository has no `LICENSE` file, so the code is all rights reserved. Dependency licenses were not verified.

## Author

- Diego Castro — [@DiegoACx](https://github.com/DiegoACx)

The 2026 refactor was developed with assistance from Claude (Anthropic).
