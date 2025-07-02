# --- Stage 1: Build Spring Boot JAR ---
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /build
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# --- Stage 2: Node.js for Playwright dependencies ---
FROM node:18-bullseye-slim AS node_deps
WORKDIR /app
COPY package.json ./
RUN npm install && npx playwright install --with-deps

# --- Stage 3: Final runtime image ---
# Use openjdk as base, install Node.js and copy built artifacts
FROM eclipse-temurin:17-jre as final

# Install Node.js (replace with org-specific if needed)
RUN apt-get update && \
    apt-get install -y curl && \
    curl -fsSL https://deb.nodesource.com/setup_18.x | bash - && \
    apt-get install -y nodejs && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

# Install Playwright browser dependencies
RUN apt-get update && \
    apt-get install -y \
    libglib2.0-0 \
    libnss3 \
    libgdk-pixbuf2.0-0 \
    libgtk-3-0 \
    libxss1 \
    libasound2t64 \
    libatk-bridge2.0-0 \
    libatk1.0-0 \
    libcups2 \
    libdbus-1-3 \
    libdrm2 \
    libxcomposite1 \
    libxdamage1 \
    libxrandr2 \
    xdg-utils \
    fonts-liberation \
    libgbm1 \
    libnspr4 \
    --no-install-recommends && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Copy Node.js dependencies and Playwright browsers from node_deps
COPY --from=node_deps /app/node_modules ./node_modules
COPY --from=node_deps /app/package.json ./
COPY --from=node_deps /root/.cache/ms-playwright /root/.cache/ms-playwright

# Copy Spring Boot JAR from builder
COPY --from=builder /build/target/playwright-test-execution-engine-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]

# ---
# To use org-specific images, replace the FROM lines above with your org's images as needed. 