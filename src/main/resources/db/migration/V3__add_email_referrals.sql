-- Добавляем поле email в таблицу users
ALTER TABLE users ADD COLUMN IF NOT EXISTS email VARCHAR(255) UNIQUE;

-- Создаем таблицу для рефералов
CREATE TABLE IF NOT EXISTS referrals (
    id UUID PRIMARY KEY,
    referrer_id UUID NOT NULL,
    referred_id UUID NOT NULL,
    created_at TIMESTAMP NOT NULL,
    bonus_rewarded BOOLEAN DEFAULT FALSE,
    bonus_amount DOUBLE PRECISION,
    CONSTRAINT fk_referrer FOREIGN KEY (referrer_id) REFERENCES users(id),
    CONSTRAINT fk_referred FOREIGN KEY (referred_id) REFERENCES users(id),
    CONSTRAINT unique_referred UNIQUE (referred_id)
); 