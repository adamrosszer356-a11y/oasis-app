-- Adatbázis létrehozása
CREATE DATABASE IF NOT EXISTS oasis_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE oasis_db;

-- Felhasználók (users) tábla
-- A RegistrationScreen.kt és LoginScreen.kt alapján
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL, -- Jelszó hash tárolására
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Eszközök (boxes) tábla
-- Az api_server_code.php mock adatai alapján
CREATE TABLE IF NOT EXISTS boxes (
    id VARCHAR(50) PRIMARY KEY,       -- Az eszköz egyedi azonosítója (pl. "oasis-001")
    user_id INT NOT NULL,             -- Tulajdonos azonosítója
    name VARCHAR(100) NOT NULL,       -- Eszköz neve (pl. "Nappali Oázis")
    plant_name VARCHAR(100),          -- Növény neve (pl. "Monstera Deliciosa")
    status VARCHAR(20) DEFAULT 'offline', -- Státusz: 'online' vagy 'offline'
    moisture INT DEFAULT 0,           -- Talajnedvesség (0-100)
    light INT DEFAULT 0,              -- Fényerő (0-100)
    temp DECIMAL(4, 1) DEFAULT 0.0,   -- Hőmérséklet (pl. 23.5)
    battery INT DEFAULT 0,            -- Akkumulátor szint (0-100)
    last_update TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Opcionális: Teszt adatok
-- Jelszó: "password123" (Ez csak példa, élesben hash-elni kell PHP-ban password_hash()-el!)
-- INSERT INTO users (name, email, password) VALUES ('Teszt Felhasználó', 'test@example.com', '$2y$10$EXAMPLEHASH...');

-- INSERT INTO boxes (id, user_id, name, plant_name, status, moisture, light, temp, battery) 
-- VALUES ('oasis-001', 1, 'Nappali Oázis (Szerverről)', 'Monstera Deliciosa', 'online', 62, 45, 23.5, 88);
