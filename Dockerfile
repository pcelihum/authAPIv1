# ====== BUILD STAGE ======
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -DskipTests package

# ====== RUN STAGE ======
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Render inyecta PORT
ENV PORT=8080
EXPOSE 8080

# (Opcional) si NO tienes actuator, borra healthcheck
# Si SÍ tienes actuator, deja esto
HEALTHCHECK --interval=15s --timeout=5s --start-period=20s \
  CMD wget -qO- http://localhost:${PORT}/actuator/health | grep -q UP || exit 1

ENTRYPOINT ["sh","-c","java -jar app.jar"]
