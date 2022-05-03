FROM maven:latest as maven

WORKDIR /app

COPY . .

RUN mvn package

FROM openjdk:17.0.2-jdk

WORKDIR /app

COPY --from=maven /app/out/myhealthnetwork-0.0.1.jar /app

EXPOSE 8080

CMD ["java", "-jar", "/app/myhealthnetwork-0.0.1.jar"]

