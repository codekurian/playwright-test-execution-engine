# Playwright Test Execution Engine (Spring Boot + Node.js)

This project exposes a REST API to download and execute a TypeScript Playwright test file from GitHub, running it via Node.js/Playwright, and returning the results.

## Prerequisites
- Docker
- Maven (for local build)
- (Optional) Your organization's Java or Node base images (see Dockerfile for how to swap)

## Build and Run

### 1. Build the Spring Boot JAR

```
./mvnw clean package
```

### 2. Build the Docker Image (Multi-Stage)

```
docker build -t playwright-springboot .
```

### 3. Run the Container

```
docker run -p 8080:8080 playwright-springboot
```

## Dockerfile Structure

This project uses a **multi-stage Docker build**:

- **Stage 1:** Builds the Spring Boot JAR using Maven and an open source Java image.
- **Stage 2:** Installs Node.js dependencies and Playwright browsers using an open source Node.js image.
- **Stage 3 (Final):** Uses an open source Java runtime image, installs Node.js, and copies in the built JAR and Node.js dependencies/browsers.

**To use your organization's images:**
- Replace the `FROM` lines in the Dockerfile with your org's Java and Node images as needed.

## API Usage

Send a POST request to `/run-test` with a JSON body:

```
{
  "githubFileUrl": "https://raw.githubusercontent.com/your/repo/main/test.spec.ts"
}
```

Example using `curl`:

```
curl -X POST http://localhost:8080/run-test \
  -H "Content-Type: application/json" \
  -d '{"githubFileUrl":"https://raw.githubusercontent.com/your/repo/main/test.spec.ts"}'
```

## Notes
- The Playwright test file must be a valid TypeScript test compatible with Playwright's test runner.
- The container installs Node.js, Playwright, and all required browsers.
- For production, add error handling, security, and resource cleanup as needed. 