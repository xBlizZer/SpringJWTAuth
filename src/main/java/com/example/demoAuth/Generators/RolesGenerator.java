package com.example.demoAuth.Generators;

import com.example.demoAuth.Entities.Roles;
import com.example.demoAuth.Repositories.RolesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
public class RolesGenerator {
    @Autowired
    private RolesRepository rolesRepository;

    public void generateRoles() {
        Roles role = new Roles("ROLE_ADMIN");
        rolesRepository.save(role);

        Roles role1 = new Roles("ROLE_ADMIN_V2");
        rolesRepository.save(role1);

        Roles role2 = new Roles("ROLE_ADMIN_V3");
        rolesRepository.save(role2);

        Roles role3 = new Roles("ROLE_MEMBER");
        rolesRepository.save(role3);
    }
}