# Estágio 1: Build da Aplicação com Gradle
# Usamos uma imagem oficial do Gradle com JDK 21 para compilar o projeto
FROM gradle:8.8.0-jdk21 AS build

# Define o diretório de trabalho dentro do contêiner
WORKDIR /home/gradle/src

# Copia os arquivos de build do Gradle para o contêiner
# Isso otimiza o cache de dependências do Docker
COPY build.gradle settings.gradle ./
COPY gradle ./gradle
COPY gradlew gradlew.bat ./

# Copia o código-fonte da aplicação
COPY src ./src

# Executa o comando de build do Gradle para gerar o arquivo .jar
# Usamos -x test para pular os testes durante o deploy
RUN ./gradlew build -x test --no-daemon

# Estágio 2: Execução da Aplicação
# Usamos uma imagem oficial do Java 21, que é muito menor e otimizada para rodar
FROM openjdk:21-slim

# Expõe a porta 8080 para que o Render possa se comunicar com a aplicação
EXPOSE 8080

# Copia o arquivo .jar gerado no estágio de build para o contêiner final
# ATENÇÃO: Verifique se o nome 'backend-0.0.1-SNAPSHOT.jar' está correto
COPY --from=build /home/gradle/src/build/libs/backend-0.0.1-SNAPSHOT.jar app.jar

# Define o comando para iniciar a aplicação quando o contêiner for executado
ENTRYPOINT ["java", "-jar", "/app.jar"]
