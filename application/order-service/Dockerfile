# 1. Base image
FROM openjdk:17-jdk-slim

# 2. Set the working directory in the container
WORKDIR /app

# 3. Copy the build output (JAR file) into the container
COPY build/libs/order-service.jar app.jar

# 4. Set environment variables for database and Redis
# 이 환경 변수들은 application.yml에서 사용할 수 있습니다.
ENV SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/fortickets
ENV SPRING_DATASOURCE_DATABASE=fortickets
ENV SPRING_DATASOURCE_USERNAME=forman
ENV SPRING_DATASOURCE_PASSWORD=1234
ENV SPRING_REDIS_HOST=redis
ENV SPRING_REDIS_PORT=6379
ENV EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE=http://eureka:12001/eureka/

# 5. Expose the port the app runs on
EXPOSE 12041

# 6. Run the application
CMD ["java", "-jar", "app.jar"]