package com.example.demoAuth.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequestDTO {
    @NotBlank(message = "You must provide a username.")
    private String username;

    @NotBlank(message = "You must provide a password.")
    private String password;
}
