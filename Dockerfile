FROM openjdk:21-jdk-slim

EXPOSE 19132/tcp
EXPOSE 19132/udp

WORKDIR /home

ADD build/libs/Waterdog-all.jar /home

ENTRYPOINT ["java", "-jar", "Waterdog-all.jar"]