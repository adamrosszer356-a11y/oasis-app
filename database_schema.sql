-- Ajánlott: egy külön adatbázis
CREATE DATABASE IF NOT EXISTS plantbox
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_hungarian_ci;

USE plantbox;

-- 1) Users tábla
CREATE TABLE IF NOT EXISTS users (
  id        INT UNSIGNED NOT NULL AUTO_INCREMENT,
  username  VARCHAR(50)  NOT NULL,
  pass      VARCHAR(255) NOT NULL,      -- jelszó hash (pl. password_hash)
  email     VARCHAR(255) NOT NULL,
  name      VARCHAR(100) NOT NULL,

  PRIMARY KEY (id),
  UNIQUE KEY uq_users_username (username),
  UNIQUE KEY uq_users_email (email)
) ENGINE=InnoDB;

-- 2) Boxes tábla
CREATE TABLE IF NOT EXISTS boxes (
  box_id     INT UNSIGNED NOT NULL AUTO_INCREMENT,  -- doboz azonosító
  owner_id   INT UNSIGNED NOT NULL,                 -- hivatkozás: users.id

  name       VARCHAR(100) NOT NULL,                 -- doboz neve
  plant      VARCHAR(100) NULL,                     -- növény fajtája

  szarassag  FLOAT NULL,
  feny       FLOAT NULL,
  ho         FLOAT NULL,
  para       FLOAT NULL,
  legnyomas  FLOAT NULL,
  vizszint   FLOAT NULL,

  PRIMARY KEY (box_id),
  KEY idx_boxes_owner_id (owner_id),

  CONSTRAINT fk_boxes_owner
    FOREIGN KEY (owner_id) REFERENCES users(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
) ENGINE=InnoDB;
