package org.mephi_kotlin_band.lottery.features.user.repository;

import org.mephi_kotlin_band.lottery.features.user.model.Referral;
import org.mephi_kotlin_band.lottery.features.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ReferralRepository extends JpaRepository<Referral, UUID> {
    
    /**
     * Находит рефералов по реферреру
     * @param referrer пользователь-реферрер
     * @return список рефералов
     */
    List<Referral> findByReferrer(User referrer);
    
    /**
     * Находит запись о реферале по приглашенному пользователю
     * @param referred приглашенный пользователь
     * @return запись о реферале
     */
    Referral findByReferred(User referred);
    
    /**
     * Подсчитывает количество рефералов пользователя
     * @param referrer пользователь-реферрер
     * @return количество рефералов
     */
    long countByReferrer(User referrer);
} 