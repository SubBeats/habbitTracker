#!/bin/bash

echo "🚀 Запуск приложения Habit Tracker..."

# Проверяем, запущен ли Docker
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker не запущен. Пожалуйста, запустите Docker Desktop"
    exit 1
fi

# Запускаем PostgreSQL через Docker Compose
echo "🐳 Запуск PostgreSQL..."
docker-compose up -d postgres

# Ждем, пока PostgreSQL запустится
echo "⏳ Ожидание запуска PostgreSQL..."
sleep 10

# Проверяем, что PostgreSQL запущен
if docker-compose ps postgres | grep -q "Up"; then
    echo "✅ PostgreSQL запущен успешно"
else
    echo "❌ Не удалось запустить PostgreSQL"
    exit 1
fi

# Запускаем приложение
echo "🏃 Запуск Spring Boot приложения..."
mvn spring-boot:run

echo "🌐 Приложение доступно по адресу: http://localhost:8080"
