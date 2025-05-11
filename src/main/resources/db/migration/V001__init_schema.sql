-

-- таблица users
CREATE TABLE IF NOT EXISTS users (
                                                  id              BIGSERIAL PRIMARY KEY,
                                                  login           VARCHAR(255) NOT NULL UNIQUE,
    password_hash   VARCHAR(255) NOT NULL,
    name            VARCHAR(255) NOT NULL,
    date_of_birth   DATE
    );

-- таблица email_data
CREATE TABLE IF NOT EXISTS email_data (
                                                       id           BIGSERIAL PRIMARY KEY,
                                                       email        VARCHAR(255) NOT NULL,
    primary_flag BOOLEAN DEFAULT FALSE,
    created_at   TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    user_id      BIGINT NOT NULL,
    CONSTRAINT uc_email UNIQUE (user_id, email),
    CONSTRAINT fk_email_user FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE
    );

-- таблица phone_data
CREATE TABLE IF NOT EXISTS phone_data (
                                                       id           BIGSERIAL PRIMARY KEY,
                                                       phone        VARCHAR(16) NOT NULL,
    primary_flag BOOLEAN DEFAULT FALSE,
    created_at   TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    user_id      BIGINT NOT NULL,
    CONSTRAINT uc_phone UNIQUE (user_id, phone),
    CONSTRAINT fk_phone_user FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE
    );

-- таблица accounts
CREATE TABLE IF NOT EXISTS accounts (
                                                     id               BIGINT PRIMARY KEY,
                                                     balance          NUMERIC(19,4) NOT NULL,
    initial_balance  NUMERIC(19,4) NOT NULL,
    version          BIGINT,
    CONSTRAINT fk_account_user FOREIGN KEY(id) REFERENCES users(id) ON DELETE CASCADE
    );