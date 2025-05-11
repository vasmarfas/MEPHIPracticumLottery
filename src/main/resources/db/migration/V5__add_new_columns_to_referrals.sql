-- Добавляем поля bonus_rewarded, bonus_amount в таблицу referrals
ALTER TABLE referrals
    ADD COLUMN bonus_rewarded BOOLEAN DEFAULT FALSE,
    ADD COLUMN bonus_amount DOUBLE PRECISION;
