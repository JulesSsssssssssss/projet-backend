SET session_replication_role = 'replica';

-- Drop existing tables if they exist
DROP TABLE IF EXISTS trade_requests;
DROP TABLE IF EXISTS user_cards;
DROP TABLE IF EXISTS cards;
DROP TABLE IF EXISTS rarity;
DROP TABLE IF EXISTS users;

-- Users table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(255) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT true,
    last_gacha TIMESTAMP
);

INSERT INTO users (username, password, role, enabled)
VALUES
  ('admin', '', 'ROLE_ADMIN', true),
  ('user', '', 'ROLE_USER', true);

-- Rarity table
CREATE TABLE rarity (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

INSERT INTO rarity (name) VALUES
  ('COMMON'),
  ('RARE'),
  ('EPIC'),
  ('LEGENDARY');

-- Cards table
CREATE TABLE cards (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    rarity_id BIGINT NOT NULL,
    attack INTEGER NOT NULL,
    defense INTEGER NOT NULL,
    image_url VARCHAR(512),
    description TEXT,
    CONSTRAINT fk_cards_rarity
        FOREIGN KEY (rarity_id)
        REFERENCES rarity(id)
        ON DELETE RESTRICT
);

INSERT INTO cards (name, rarity_id, attack, defense, image_url, description)
VALUES
  -- COMMON cards (rarity_id = 1)
  ('Goblin', 1, 50, 30, 'https://via.placeholder.com/150', 'Un goblin basique'),
  ('Slime', 1, 40, 40, 'https://via.placeholder.com/150', 'Une créature gluante'),
  ('Rat géant', 1, 45, 35, 'https://via.placeholder.com/150', 'Un rat de taille inhabituelle'),
  ('Squelette', 1, 55, 25, 'https://via.placeholder.com/150', 'Un guerrier mort-vivant'),
  -- RARE cards (rarity_id = 3)
  ('Chevalier', 3, 100, 80, 'https://via.placeholder.com/150', 'Un noble chevalier'),
  ('Mage', 3, 120, 60, 'https://via.placeholder.com/150', 'Un lanceur de sorts'),
  ('Archer', 3, 110, 70, 'https://via.placeholder.com/150', 'Un tireur d''élite'),
  -- EPIC cards (rarity_id = 4)
  ('Dragon Jeune', 4, 200, 150, 'https://via.placeholder.com/150', 'Un dragon en apprentissage'),
  ('Paladin', 4, 180, 180, 'https://via.placeholder.com/150', 'Un guerrier sacré'),
  ('Sorcier', 4, 220, 130, 'https://via.placeholder.com/150', 'Un maître des arcanes'),
  -- LEGENDARY cards (rarity_id = 5)
  ('Dragon Ancien', 5, 400, 300, 'https://via.placeholder.com/150', 'Le plus puissant des dragons'),
  ('Archmage', 5, 450, 250, 'https://via.placeholder.com/150', 'Maître suprême de la magie'),
  ('Titan', 5, 500, 400, 'https://via.placeholder.com/150', 'Une force de la nature');

-- User <-> Card pivot table
CREATE TABLE user_cards (
    user_id BIGINT NOT NULL,
    card_id BIGINT NOT NULL,
    obtained_at TIMESTAMP NOT NULL DEFAULT NOW(),
    PRIMARY KEY (user_id, card_id),
    CONSTRAINT fk_user_cards_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_user_cards_card
        FOREIGN KEY (card_id)
        REFERENCES cards(id)
        ON DELETE CASCADE
);

-- Trade Requests table
CREATE TABLE trade_requests (
    id BIGSERIAL PRIMARY KEY,
    from_user_id BIGINT NOT NULL,
    to_user_id BIGINT NOT NULL,
    offered_card_id BIGINT NOT NULL,
    requested_card_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_trade_from_user
        FOREIGN KEY (from_user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_trade_to_user
        FOREIGN KEY (to_user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_trade_offered_card
        FOREIGN KEY (offered_card_id)
        REFERENCES cards(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_trade_requested_card
        FOREIGN KEY (requested_card_id)
        REFERENCES cards(id)
        ON DELETE CASCADE
);
