# Usa una imagen oficial de Java como base
FROM openjdk:21-slim AS build

# Instala Maven
RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*

# Establece el directorio de trabajo para la compilación
WORKDIR /app

# Copia el archivo pom.xml y las dependencias para aprovechar el cacheo de Docker
COPY pom.xml .

# Descarga las dependencias del proyecto
RUN mvn dependency:go-offline

# Copia el código fuente del proyecto
COPY src ./src

# Compila y empaqueta la aplicación
RUN mvn clean package -DskipTests

# Fase final: usa una imagen ligera de Java para la ejecución
FROM openjdk:21-slim

# Establece el directorio de trabajo en el contenedor
WORKDIR /app

# Copia el .jar generado desde la fase de construcción
COPY --from=build /app/target/backend-0.0.1-SNAPSHOT.jar /app/backend.jar

COPY .env /app/.env

# Expone el puerto 8080 para que la aplicación sea accesible
EXPOSE 8082

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "/app/backend.jar"]
