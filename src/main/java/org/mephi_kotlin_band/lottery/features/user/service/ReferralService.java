package org.mephi_kotlin_band.lottery.features.user.service;

import org.mephi_kotlin_band.lottery.features.user.model.Referral;
import org.mephi_kotlin_band.lottery.features.user.model.User;

import java.util.List;
import java.util.UUID;

public interface ReferralService {
    
    /**
     * Создает реферальную связь между двумя пользователями
     * @param referrerId ID реферрера (приглашающий)
     * @param referredId ID приглашенного пользователя
     * @return созданный реферал
     */
    Referral createReferral(UUID referrerId, UUID referredId);
    
    /**
     * Начисляет бонус за реферала
     * @param referral реферальная запись
     * @param bonusAmount сумма бонуса
     * @return обновленный реферал
     */
    Referral awardBonus(Referral referral, double bonusAmount);
    
    /**
     * Получает список рефералов пользователя
     * @param userId ID пользователя
     * @return список рефералов
     */
    List<Referral> getUserReferrals(UUID userId);
    
    /**
     * Подсчитывает количество рефералов пользователя
     * @param userId ID пользователя
     * @return количество рефералов
     */
    long countUserReferrals(UUID userId);
    
    /**
     * Генерирует уникальный реферальный код для пользователя
     * @param user пользователь
     * @return реферальный код
     */
    String generateReferralCode(User user);
    
    /**
     * Находит пользователя по реферальному коду
     * @param referralCode реферальный код
     * @return найденный пользователь
     */
    User findUserByReferralCode(String referralCode);
} 