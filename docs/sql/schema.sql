CREATE DATABASE IF NOT EXISTS ticket_db
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_general_ci;

USE ticket_db;

DROP TABLE IF EXISTS ticket_log;
DROP TABLE IF EXISTS ticket;
DROP TABLE IF EXISTS user;

CREATE TABLE user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'STUDENT'
) ENGINE = InnoDB;

CREATE TABLE ticket (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    title VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    assignee_id BIGINT,
    category VARCHAR(20),
    summary VARCHAR(200),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_status_time (status, created_at)
) ENGINE = InnoDB;

CREATE TABLE ticket_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    ticket_id BIGINT NOT NULL,
    action VARCHAR(50) NOT NULL,
    operator_id BIGINT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    KEY idx_ticket_time (ticket_id, created_at)
) ENGINE = InnoDB;

-- Local demo account only. Replace plaintext passwords before production use.
INSERT INTO user (username, password, role)
VALUES ('admin', '123456', 'ADMIN')
ON DUPLICATE KEY UPDATE password = VALUES(password), role = VALUES(role);

