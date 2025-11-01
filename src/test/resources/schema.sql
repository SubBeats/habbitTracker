-- Создание таблиц для тестов
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    user_name VARCHAR(255),
    password_hash VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS habits (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    created_at TIMESTAMP NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT true,
    frequency_days INTEGER DEFAULT 1,
    target_streak INTEGER,
    current_streak INTEGER DEFAULT 0,
    user_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
