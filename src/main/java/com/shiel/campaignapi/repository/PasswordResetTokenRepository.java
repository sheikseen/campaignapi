package com.shiel.campaignapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.shiel.campaignapi.entity.PasswordResetToken;
import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Integer> {
    Optional<PasswordResetToken> findByToken(String token);

    //void deleteByUserId(Integer userId);
}