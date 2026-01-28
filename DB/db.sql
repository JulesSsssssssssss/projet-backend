SET session_replication_role = 'replica';

DROP TABLE IF EXISTS user_cards;
DROP TABLE IF EXISTS cards;
DROP TABLE IF EXISTS rarity;
DROP TABLE IF EXISTS users;

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


CREATE TABLE rarity (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

INSERT INTO rarity (name) VALUES
  ('COMMON'),
  ('UNCOMMON'),
  ('RARE'),
  ('EPIC'),
  ('LEGENDARY');


CREATE TABLE cards (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    rarity_id BIGINT NOT NULL,
    attack INTEGER NOT NULL,
    defense INTEGER NOT NULL,
    image_url VARCHAR(512),

    CONSTRAINT fk_cards_rarity
        FOREIGN KEY (rarity_id)
        REFERENCES rarity(id)
        ON DELETE RESTRICT
);

INSERT INTO cards (name, rarity_id, attack, defense, image_url)
VALUES
  (
    'Forest Guardian',
    (SELECT id FROM rarity WHERE name = 'COMMON'),
    40,
    60,
    'https://example.com/cards/forest-guardian.png'
  ),
  (
    'Shadow Assassin',
    (SELECT id FROM rarity WHERE name = 'RARE'),
    75,
    40,
    'https://example.com/cards/shadow-assassin.png'
  ),
  (
    'Water Mage',
    (SELECT id FROM rarity WHERE name = 'EPIC'),
    65,
    50,
    'https://example.com/cards/water-mage.png'
  ),
  (
    'Fire Dragon',
    (SELECT id FROM rarity WHERE name = 'LEGENDARY'),
    90,
    70,
    'https://example.com/cards/fire-dragon.png'
  );


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
