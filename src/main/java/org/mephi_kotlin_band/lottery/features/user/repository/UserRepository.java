package org.mephi_kotlin_band.lottery.features.user.repository;

import org.mephi_kotlin_band.lottery.features.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);
    boolean existsByRole(User.Role role);
    @Query("SELECT u FROM User u WHERE u.role <> 'ADMIN'")
    List<User> findAllNonAdminUsers();
}