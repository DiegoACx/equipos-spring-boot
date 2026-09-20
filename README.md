<p align="center">
  <img src="https://img.shields.io/badge/Java-17-ED8B00?logo=openjdk&logoColor=white" alt="Java 17">
  <img src="https://img.shields.io/badge/Spring%20Boot-4.1.1-6DB33F?logo=springboot&logoColor=white" alt="Spring Boot 4.1.1">
  <img src="https://img.shields.io/badge/Thymeleaf-3.1-005F0F?logo=thymeleaf&logoColor=white" alt="Thymeleaf">
  <img src="https://img.shields.io/badge/H2-en%20memoria-1021FF" alt="H2 en memoria">
</p>

<h1 align="center">Registro de equipos tecnológicos</h1>

<p align="center">CRUD académico de inventario TI con Spring Boot, Thymeleaf y H2 en memoria.</p>

<p align="center">🇬🇧 <a href="README.en.md">Read in English</a></p>

## Acerca del proyecto

Aplicación web de Spring Boot para registrar equipos tecnológicos (inventario TI): permite crear, listar, editar y eliminar equipos con vistas Thymeleaf renderizadas en el servidor. Los datos viven en una base H2 en memoria y la app corre en el puerto 8082.

Es un trabajo del curso de *Infraestructura Tecnológica* (5.º semestre) de la Universidad Autónoma de Bucaramanga (UNAB), revisado y corregido en septiembre de 2026.

### Lo que NO es

- **No es un producto de inventario:** es un CRUD académico.
- **No tiene persistencia:** los datos se pierden al reiniciar.
- **No tiene autenticación ni protección CSRF:** no usa Spring Security.
- **No es una API REST:** no hay `@RestController` ni respuestas JSON.

## Funcionalidades

| Método | Ruta | Qué hace |
|---|---|---|
| GET | `/`, `/index`, `/menu` | Portada |
| GET | `/verequipo` (también `/mostrarequipo`, `/listarequipo`) | Lista de equipos. **URL de entrada recomendada** |
| GET | `/verequipo/formequipo` | Formulario para un equipo nuevo |
| POST | `/guardarequipo` | Valida y guarda; sirve para crear y editar. Con errores, vuelve al formulario con mensajes |
| GET | `/equipo/editar/{id}` | Formulario con el equipo cargado. 404 si el id no existe |
| POST | `/equipo/eliminar/{id}` | Elimina y redirige a la lista. Por GET responde 405 |
| GET | `/h2-console` | Solo con el perfil `dev`. Sin perfil: 404 |

- **Validaciones (servidor):** `nombre`, `categoria`, `marca` y `estado` no pueden estar vacíos; `precio` es obligatorio y mayor que cero; `observaciones` es opcional. Mensajes en español.
- **Formulario y lista:** el precio acepta centavos y la lista muestra ID, nombre, categoría, marca, precio, estado y observaciones.
- **Categorías** (lista fija en el HTML): Laptop, PC de escritorio, Servidor, Impresora, Accesorios. **Estados:** Nuevo, En estado, En reparación, Obsoleto. El servidor no comprueba que el valor pertenezca a esas listas, solo que no esté vacío.
- **Confirmación de borrado:** solo en el navegador (JavaScript). Sin JavaScript, el POST elimina directamente.
- **Perfil `dev`:** activa la consola H2 en `http://localhost:8082/h2-console` (JDBC URL `jdbc:h2:mem:equiposdb`, usuario `sa`, contraseña vacía). No lo uses en un servidor ni en Docker expuesto.

## Stack tecnológico

Las versiones directas salen del `pom.xml`; las transitivas, de `dependency:list` del 20/09/2026.

| Componente | Versión |
|---|---|
| Java | 17 |
| Spring Boot (parent) | 4.1.1 |
| Tomcat embebido | 11.0.25 (override; el BOM trae 11.0.24) |
| Spring Framework | 7.0.9 |
| Hibernate ORM | 7.4.5.Final |
| Hibernate Validator | 9.1.3.Final |
| Thymeleaf | 3.1.5.RELEASE |
| H2 (runtime) | 2.4.240 |
| Maven Wrapper | 3.3.2 (descarga Maven 3.9.9) |
| Tests | JUnit Jupiter 6.0.3, Mockito 5.23.0, AssertJ 3.27.7 |

El `pom.xml` no fija ninguna versión de dependencia (todas vienen del BOM de Spring Boot), salvo `tomcat.version`.

**JDK probados:** 17.0.10 y 21.0.3. Otras versiones no se probaron.

## Estructura del proyecto

```
.
├─ .mvn/wrapper/
├─ src/
│  ├─ main/    (java/com/quiz/equipos: controlador, entidades, repositorio; resources)
│  └─ test/    (java/com/quiz/equipos)
├─ Dockerfile · .dockerignore
├─ mvnw · mvnw.cmd · pom.xml
└─ .gitattributes · .gitignore
```

No hay capa de servicios: el controlador usa el repositorio directamente.

## Cómo correrlo (Windows, PowerShell)

Requiere JDK 17 o superior e internet la primera vez (el wrapper descarga Maven y las dependencias).

```powershell
# Tests
.\mvnw.cmd -B clean verify

# Ejecutar en desarrollo (probado por el autor)
.\mvnw.cmd spring-boot:run

# Empaquetar y ejecutar el jar (~53 MB)
.\mvnw.cmd -B clean package
java -jar target\equipos-0.0.1-SNAPSHOT.jar

# Con la consola H2 (perfil dev)
java -jar target\equipos-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
```

- **URL de entrada:** http://localhost:8082/verequipo
- **Los datos se pierden al reiniciar** (H2 en memoria).
- Para usar el JDK 17 solo en la sesión: `$env:JAVA_HOME = 'C:\Program Files\Java\jdk-17'`.
- `-Dspring-boot.run.profiles=dev` con `spring-boot:run` está documentado pero **no se ha verificado**.

## Docker

El `Dockerfile` es multi-etapa: compila con `maven:3.9-eclipse-temurin-17` (`mvn clean package -DskipTests`) y ejecuta con `eclipse-temurin:17-jre` como usuario no root, exponiendo el puerto 8082. El jar se compila dentro de la imagen, así que hereda el override de Tomcat.

**No se probó:** Docker no está instalado en la máquina de desarrollo, por lo que no se construyó ninguna imagen ni se probó la creación del usuario no root. Solo se comprobó que `target/*.jar` coincide con un único archivo. Las etiquetas de imagen son flotantes y su contenido cambia con el tiempo. No hay `docker-compose`.

## Qué se probó y qué no

**Automatizado** (Windows 11, antes y después del override de Tomcat):

- 17 tests: 1 de arranque de contexto, 11 con MockMvc (`@WebMvcTest`) y 5 de repositorio (`@DataJpaTest`). Pasan con JDK 17.0.10 y con 21.0.3.
- Comprobaciones HTTP sobre el jar real, con y sin perfil `dev`, con ambos JDK: portada, lista y formulario responden 200; editar un id inexistente da 404; borrar por GET da 405; un POST válido guarda y uno inválido muestra errores.
- Consola H2: `/h2-console` da 404 sin perfil y 302 con `dev`.

**Prueba manual del autor** (no cubierta por los tests): en el navegador, con Spring Boot 4.1.1 y repetida con Tomcat 11.0.25, usando `spring-boot:run`. Funcionó bien: crear con precio decimal, formulario vacío, editar, eliminar y abrir `/`.

**No verificado:**

- Docker y `docker run`.
- `spring-boot:run` con `-Dspring-boot.run.profiles=dev`.
- Entrar a la consola H2 con `sa` (solo se comprobó el 302).
- Otros navegadores, Linux o macOS, otros JDK, varios usuarios, rendimiento y accesibilidad.

## Limitaciones y riesgos

- **Sin autenticación ni CSRF.** El borrado va por POST sin token, y la confirmación es solo de JavaScript.
- **Front por CDN, sin `integrity` y sin auditar:** Bootstrap 4.5.0, jQuery 3.5.1 slim, `@popperjs/core` 2.5.3 y Font Awesome (versión no fijada, más un script de kit externo). Sin internet la app funciona pero pierde el estilo.
- **`open-in-view` activo:** Spring lo avisa en cada arranque.
- **Aviso de Mockito con JDK 21:** se autoadjunta como agente y dejará de funcionar en futuros JDK. Con JDK 17 no aparece.
- **Modelo de datos:** `precio` es `Double` (no `BigDecimal`), el campo del identificador se llama `Id`, y no hay paginación ni búsqueda.

### Auditoría de dependencias

- **Herramienta y alcance:** OSV, el 20/09/2026, sobre 122 artefactos resueltos (73 compile, 12 runtime, 37 test), con JDK 17.
- **Hallazgo:** `tomcat-embed-core` 11.0.24 (el que trae el BOM de Spring Boot 4.1.1) tenía 3 avisos críticos, CVE-2026-65905, CVE-2026-65182 y CVE-2026-68525, corregidos en 11.0.25. Según sus textos, afectan a la autenticación y a las restricciones de seguridad de Tomcat, que esta app no configura. No se probó su explotabilidad.
- **Override:** `tomcat.version` = 11.0.25. Con él, OSV da 0 avisos. **No está validado oficialmente con Spring Boot 4.1.1:** solo lo cubren los 17 tests y el arranque real. Debe quitarse cuando Spring Boot publique un parche con Tomcat 11.0.25 o superior, y nada avisa cuando eso ocurra.
- **Lo que la auditoría no cubre:** solo consulta OSV (avisos ya publicados; "0" no significa "sin vulnerabilidades"); no cubre los plugins de Maven ni el Maven del wrapper, el front por CDN, la imagen base de Docker, el JDK, ni el código y la configuración de la app.

## Historia del proyecto

El original es un trabajo del curso. En septiembre de 2026 se corrigió:

- **Ruta `/`:** la clase de portada no estaba registrada como controlador y el botón "Ver equipos" apuntaba a una ruta inexistente. Ahora funciona y enlaza a `/verequipo`.
- **Editar un id inexistente:** daba error 500. Ahora responde 404.
- **Borrar:** era un enlace GET. Ahora es un formulario POST.
- **Validación:** no había ninguna. Se añadió `spring-boot-starter-validation`, el precio acepta centavos y la lista muestra las observaciones.
- **Configuración:** había credenciales de ejemplo triviales de una base en memoria. Ahora se usa `sa` sin contraseña, y la consola H2 solo está en el perfil `dev`.
- **Dockerfile:** apuntaba a un jar de otro proyecto que no existe. Ahora es multi-etapa.
- **Otros:** 17 tests, `.env` en `.gitignore`, `mvnw` ejecutable y `.dockerignore`.
- **Migración:** de Spring Boot 3.4.5 (sin soporte OSS desde el 31/12/2025), pasando por 3.5.16, hasta 4.1.1. Cambios: `spring-boot-starter-web` pasó a `spring-boot-starter-webmvc`, starters de test por tecnología, dos imports reubicados (`@WebMvcTest` y `@DataJpaTest`) y `spring-boot-h2console` para que el perfil `dev` siguiera funcionando. El código de la aplicación no cambió. Los paquetes de test y la consola H2 no estaban en la guía oficial de migración; se resolvieron inspeccionando los JAR y ejecutando la app.
- **Parche de seguridad:** override de Tomcat a 11.0.25.

## Licencia

Este repositorio no tiene un archivo `LICENSE`, por lo que el código queda con todos los derechos reservados. Las licencias de las dependencias no se verificaron.

## Autor

- Diego Castro — [@DiegoACx](https://github.com/DiegoACx)

El refactor de 2026 se desarrolló con asistencia de Claude (Anthropic).
