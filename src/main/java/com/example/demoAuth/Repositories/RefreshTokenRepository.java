package com.example.demoAuth.Repositories;

import com.example.demoAuth.Entities.RefreshToken;
import com.example.demoAuth.Entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByRefreshToken(String token);

    @Modifying
    int deleteByUser(Users user);

}