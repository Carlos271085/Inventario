FROM eclipse-temurin:17-jdk AS test
WORKDIR /app 
COPY inventario  /app/ 
RUN chmod +x  mvnw 
RUN  ./mvnw clean test 

FROM eclipse-temurin:17-jdk AS compile
WORKDIR /app 
COPY inventario /app/ 
RUN chmod +x mvnw 
RUN  ./mvnw clean package 


FROM eclipse-temurin:17-jdk AS prod

WORKDIR /app/inventario
COPY --from=compile /app/target/*.jar app.jar
CMD [ "java","-jar", "app.jar" ]