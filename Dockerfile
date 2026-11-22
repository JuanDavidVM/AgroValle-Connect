# Dockerfile de AgroValle Connect
#
# Render no ofrece runtime nativo de Java, asi que el despliegue se hace por
# contenedor. La construccion es multi-etapa: la primera compila con Maven y
# JDK 17, la segunda solo conserva el JRE y el JAR, lo que reduce el tamano de
# la imagen y la superficie expuesta.

# ---------------------------------------------------------------------------
# Etapa 1: construccion
# ---------------------------------------------------------------------------
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /build

# Las dependencias cambian mucho menos que el codigo fuente. Copiarlas en una
# capa aparte permite que Docker reutilice la cache y acorta los despliegues
# siguientes.
COPY pom.xml .
RUN mvn -B dependency:go-offline

# Checkstyle esta enlazado a la fase 'validate', asi que su configuracion debe
# estar presente o la compilacion falla antes de empezar.
COPY checkstyle.xml checkstyle-suppressions.xml ./
COPY src ./src

# Las pruebas ya corrieron en el pipeline de CI antes de llegar aqui; repetirlas
# dentro de la imagen solo alargaria el despliegue.
RUN mvn -B clean package -DskipTests

# ---------------------------------------------------------------------------
# Etapa 2: ejecucion
# ---------------------------------------------------------------------------
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Usuario sin privilegios: si alguien llegara a ejecutar codigo dentro del
# contenedor, no lo hace como root.
RUN addgroup -S agrovalle && adduser -S agrovalle -G agrovalle

COPY --from=build /build/target/agrovalle-connect-*.jar app.jar
RUN chown agrovalle:agrovalle app.jar
USER agrovalle

# La instancia gratuita de Render tiene 512 MB y 0.1 CPU. Sin estos limites la
# JVM calcula mal su heap y el contenedor muere por falta de memoria.
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=70 -XX:+UseSerialGC -Xss512k"

# Informativo: el puerto real lo inyecta Render en la variable PORT, que
# application-staging.yml ya lee.
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
