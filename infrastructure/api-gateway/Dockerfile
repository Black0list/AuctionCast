FROM eclipse-temurin:17-jre-alpine
EXPOSE 8080
WORKDIR /app
COPY target/*.jar app.jar
RUN apk add --no-cache curl
ENTRYPOINT ["java", "-jar", "app.jar"]
