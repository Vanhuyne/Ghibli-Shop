FROM maven:3-openjdk-17 AS build
COPY . .
RUN mvn clean package -DskipTests

FROM maven:3-openjdk-17-slim
COPY --from=build /target/ecommerce-0.0.1-SNAPSHOT.jar ecommerce.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "ecommerce.jar"]