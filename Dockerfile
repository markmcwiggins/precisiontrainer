FROM openjdk:8-alpine

COPY target/uberjar/finalcloj.jar /finalcloj/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/finalcloj/app.jar"]
