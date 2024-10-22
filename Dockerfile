# Dockerfile

FROM openjdk:17-jdk-slim

# 작업 디렉토리 설정
WORKDIR /app

# 서비스 이름 인자를 받아 해당 서비스의 JAR 파일을 복사
ARG SERVICE_NAME
COPY ./application/${SERVICE_NAME}/build/libs/${SERVICE_NAME}.jar /app/${SERVICE_NAME}.jar

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "/app/${SERVICE_NAME}.jar"]
