package com.example.demoAuth.Repositories;

import com.example.demoAuth.Entities.Roles;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolesRepository extends JpaRepository<Roles, Long> {
    Roles findByName(String name);
}
