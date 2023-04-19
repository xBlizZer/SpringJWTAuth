package com.example.demoAuth.Repositories;

import com.example.demoAuth.Entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepository extends JpaRepository<Users, Long> {
    Users findByUsername(String username);
}
