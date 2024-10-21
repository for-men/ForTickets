FROM openjdk:17-jdk-slim

# 시간대 설정
ENV TZ=Asia/Seoul
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 작업 디렉토리를 설정합니다.
WORKDIR /app

# 호스트의 JAR 파일을 컨테이너의 /app 디렉토리로 복사합니다.
COPY build/libs/gateway-service.jar gateway.jar

ENV SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE}

# 컨테이너가 시작될 때 실행할 명령을 지정합니다.
CMD ["java", "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE}", "-jar", "gateway.jar"]