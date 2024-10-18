# OpenJDK 이미지를 기반으로 이미지 생성
FROM openjdk:17-jdk-slim

# jar 파일을 컨테이너 안에 복사
COPY ../../application/concert-service/build/libs/concert-service.jar /app/concert-service.jar

# 컨테이너 실행 시 jar 파일을 실행하도록 설정
ENTRYPOINT ["java", "-jar", "/app/concert-service.jar"]