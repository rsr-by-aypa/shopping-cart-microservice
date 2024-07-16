# Verwende das offizielle OpenJDK 22-Image als Basis-Image
FROM openjdk:17

# Setze das Arbeitsverzeichnis auf /app
WORKDIR /app

# Kopiere die mvnw und die zugehörigen Dateien ins Image
COPY mvnw ./
COPY .mvn .mvn
COPY pom.xml ./

# Konvertiere mvnw zu Unix-Zeilenendungen und mache sie ausführbar
RUN sed -i 's/\r$//' mvnw && chmod +x mvnw && ls -l mvnw

# Installiere die Abhängigkeiten ohne die Quellcodes zu kopieren
RUN ./mvnw dependency:resolve

# Kopiere den Rest des Projekt-Quellcodes
COPY src ./src

# Baue die Anwendung und überspringe die Tests
RUN ./mvnw package -DskipTests

# Setze den Port, den die Anwendung nutzt
EXPOSE 8080

# Definiere den Einstiegspunkt, um die Anwendung zu starten
ENTRYPOINT ["java", "-jar", "target/shopping-cart-microservice-0.0.1-SNAPSHOT.jar"]