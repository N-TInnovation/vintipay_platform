FROM openjdk:17
EXPOSE 8080
ADD target/*.jar gateway-payment-0.0.1.jar
ENTRYPOINT ["java","-jar", "gateway-payment-0.0.1.jar"]