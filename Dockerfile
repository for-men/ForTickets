# Dockerfile

FROM openjdk:17-jdk-slim

# 작업 디렉토리 설정
WORKDIR /app

# 서비스 이름 인자를 받아 해당 서비스의 JAR 파일을 복사
ARG SERVICE_NAME
COPY artifacts/${SERVICE_NAME}.jar /app/${SERVICE_NAME}.jar

ENV SERVICE_NAME = ${SERVICE_NAME}
# 애플리케이션 실행
ENTRYPOINT ["sh", "-c", "java -jar /app/$SERVICE_NAME.jar"]

#FROM gradle:8.10.1-jdk17 AS build
#
#WORKDIR /app
#
#ARG SERVICE_NAME
#
#COPY application/$SERVICE_NAME /app
#
#RUN gradle clean build
#
#FROM openjdk:17-jdk-slim
#
#COPY --from=build /app/build/libs/*.jar /app.jar
#
#CMD ["java", "-jar", "app.jar"]
