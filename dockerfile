FROM openjdk:8-jre-alpine

# Метаданные 
LABEL maintainer="habit-tracker"
LABEL description="Habit Tracker Spring Boot Application"

# Создаем рабочую директорию в контейнере
WORKDIR /app

# Копируем собранный JAR файл в образ
COPY target/habit-tracker-0.0.1-SNAPSHOT.jar app.jar

# Открываем порт, на котором работает приложение
EXPOSE 8080

# Команда запуска приложения
ENTRYPOINT ["java", "-jar", "app.jar"]

