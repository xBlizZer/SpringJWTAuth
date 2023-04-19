package com.example.demoAuth.Controller;


import com.example.demoAuth.DTO.LoginRequestDTO;
import com.example.demoAuth.DTO.RegisterRequestDTO;
import com.example.demoAuth.Entities.RefreshToken;
import com.example.demoAuth.Repositories.RefreshTokenRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthControllerTest extends BaseControllerTest {
    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @Test
    public void RegisterSuccessfully() throws Exception {
        RegisterRequestDTO request = new RegisterRequestDTO();
        request.setUsername("UserAccount");
        request.setPassword("Password");

        mockMvc.perform(post("/auth/register").contentType("application/json").content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("UserAccount"));
    }

    @Test
    public void RegisterUsernameExists() throws Exception {
        RegisterRequestDTO request = new RegisterRequestDTO();
        request.setUsername("Jonas");
        request.setPassword("Password");

        mockMvc.perform(post("/auth/register").contentType("application/json").content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Sorry, this username is already taken. Please choose a different one."));
    }

    @Test
    public void RegisterUsernameNotValid() throws Exception {
        RegisterRequestDTO request = new RegisterRequestDTO();
        request.setUsername("Jon as");
        request.setPassword("Password");

        mockMvc.perform(post("/auth/register").contentType("application/json").content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Username can only contain letters and numbers."));
    }

    @Test
    public void RegisterUsernameNotValidEmpty() throws Exception {
        RegisterRequestDTO request = new RegisterRequestDTO();
        request.setUsername("");
        request.setPassword("Password");

        mockMvc.perform(post("/auth/register").contentType("application/json").content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("You must provide a username."));
    }

    @Test
    public void RegisterPasswordNotValidEmpty() throws Exception {
        RegisterRequestDTO request = new RegisterRequestDTO();
        request.setUsername("User");
        request.setPassword("");

        mockMvc.perform(post("/auth/register").contentType("application/json").content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("You must provide a password."));
    }
    @Test
    public void LoginSuccessfully() throws Exception {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setUsername("Jonas");
        request.setPassword("Test");

        mockMvc.perform(post("/auth/login").contentType("application/json").content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("Jonas"));
    }
    @Test
    public void LoginWrongPasswordFailure() throws Exception {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setUsername("Jonas");
        request.setPassword("WrongPassword");

        mockMvc.perform(post("/auth/login").contentType("application/json").content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("The password you entered is incorrect. Please try again."));
    }
    @Test
    public void LoginWrongUsernameFailure() throws Exception {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setUsername("WrongUsername");
        request.setPassword("WrongPassword");

        mockMvc.perform(post("/auth/login").contentType("application/json").content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("The user account you are trying to access does not exist."));
    }

    @Test
    public void RefreshTokenSuccessFully() throws Exception {
        RefreshToken request = new RefreshToken();
        request.setRefreshToken(refreshTokenRepository.findAll().get(0).getRefreshToken());

        mockMvc.perform(post("/auth/refreshtoken").contentType("application/json").content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").value(request.getRefreshToken()));
    }

    @Test
    public void RefreshTokenFailure() throws Exception {
        RefreshToken request = new RefreshToken();
        request.setRefreshToken(UUID.randomUUID().toString());

        mockMvc.perform(post("/auth/refreshtoken").contentType("application/json").content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Refresh token is not valid or expired!"));
    }

    @Test
    public void LogoutSuccessfully() throws Exception {
        RefreshToken request = new RefreshToken();
        request.setRefreshToken(refreshTokenRepository.findAll().get(0).getRefreshToken());

        mockMvc.perform(post("/auth/logout").contentType("application/json").content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("You have been successfully logged out."));
    }

    @Test
    public void LogoutFailure() throws Exception {
        RefreshToken request = new RefreshToken();
        request.setRefreshToken(UUID.randomUUID().toString());

        mockMvc.perform(post("/auth/logout").contentType("application/json").content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("Sorry, it seems that you are unable to log out at the moment. Please try again!"));
    }

    @Test
    @WithAnonymousUser
    public void getRolesFailure() throws Exception {
        mockMvc.perform(get("/auth/roles"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
        public void getRolesWithRoleAdmin() throws Exception {
        mockMvc.perform(get("/auth/roles"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = "ROLE_MEMBER")
    public void getRolesWithRoleMember() throws Exception {
        mockMvc.perform(get("/auth/roles"))
                .andExpect(status().isForbidden());
    }
}
