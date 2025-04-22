CREATE TABLE referrals (
                           id UUID PRIMARY KEY,
                           user_id UUID NOT NULL,
                           referred_user_id UUID NOT NULL,
                           referral_code VARCHAR(255) NOT NULL,
                           created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
                           CONSTRAINT fk_referrer FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                           CONSTRAINT fk_referred FOREIGN KEY (referred_user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Индекс для ускорения поиска по коду
CREATE INDEX idx_referrals_code ON referrals(referral_code);

-- Уникальность: один и тот же пользователь не может дважды пригласить одного и того же
ALTER TABLE referrals ADD CONSTRAINT uq_referral_unique_pair UNIQUE (user_id, referred_user_id);