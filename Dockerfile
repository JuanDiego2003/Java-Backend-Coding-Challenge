# Usa una imagen base de OpenJDK
FROM openjdk:21-jdk-slim AS builder

# Instala Maven para compilar el proyecto
RUN apt-get update && apt-get install -y maven

# Establece el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copia el archivo pom.xml para resolver las dependencias
COPY pom.xml .

# Descarga las dependencias del proyecto sin ejecutar el código
RUN mvn dependency:go-offline -B

# Copia el código fuente al contenedor
COPY src ./src

# Compila y empaqueta el archivo JAR ejecutable
RUN mvn clean package -DskipTests

# Define el archivo JAR como el punto de entrada
CMD ["java", "-jar", "target/finservice-0.0.1-SNAPSHOT.jar"]
