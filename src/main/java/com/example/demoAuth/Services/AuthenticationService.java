package com.example.demoAuth.Services;

import com.example.demoAuth.Response.AuthResponse;
import com.example.demoAuth.DTO.LoginRequestDTO;
import com.example.demoAuth.Response.RefreshTokenResponse;
import com.example.demoAuth.DTO.RegisterRequestDTO;
import com.example.demoAuth.Entities.RefreshToken;
import com.example.demoAuth.Entities.Roles;
import com.example.demoAuth.Entities.Users;
import com.example.demoAuth.Exceptions.BadRequestException;
import com.example.demoAuth.Exceptions.ForbiddenException;
import com.example.demoAuth.Exceptions.TokenRefreshException;
import com.example.demoAuth.Exceptions.UserNotFoundException;
import com.example.demoAuth.Repositories.RefreshTokenRepository;
import com.example.demoAuth.Repositories.UsersRepository;
import com.example.demoAuth.Security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthenticationService {
    @Autowired
    private UsersRepository usersRepository;

   @Autowired
   private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private JwtUtils jwtUtils;

    public AuthResponse register(RegisterRequestDTO registerDTO){
        if (usersRepository.findByUsername(registerDTO.getUsername()) != null) {
            throw new BadRequestException("Sorry, this username is already taken. Please choose a different one.");
        } else {
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

            Users registeredUser = new Users();
            registeredUser.setUsername(registerDTO.getUsername());
            registeredUser.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
            registeredUser.setEmail(registerDTO.getEmail());

            usersRepository.save(registeredUser);

            String token = jwtUtils.generateJwtToken(registeredUser);
            RefreshToken refreshToken = jwtUtils.createRefreshToken(registeredUser);

            List<String> roleNames = registeredUser.getRoles().stream().map(Roles::getName).collect(Collectors.toList());

            return new AuthResponse(token,refreshToken.getRefreshToken(),registeredUser.getId(),registeredUser.getUsername(),registeredUser.getEmail(), roleNames);
        }
    }

    public AuthResponse login(LoginRequestDTO loginRequestDTO) {
        Users loginUser = usersRepository.findByUsername(loginRequestDTO.getUsername());
        if (loginUser == null) {
            throw new UserNotFoundException("The user account you are trying to access does not exist.");
        }
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if (!passwordEncoder.matches(loginRequestDTO.getPassword(), loginUser.getPassword())) {
            throw new ForbiddenException("The password you entered is incorrect. Please try again.");
        }

        String token = jwtUtils.generateJwtToken(loginUser);
        RefreshToken refreshToken = jwtUtils.createRefreshToken(loginUser);

        List<String> roleNames = loginUser.getRoles().stream().map(Roles::getName).collect(Collectors.toList());

        return new AuthResponse(token,refreshToken.getRefreshToken(),loginUser.getId(),loginUser.getUsername(),loginUser.getEmail(), roleNames);
    }

    public boolean logout(String token){
        RefreshToken toDeletetoken = refreshTokenRepository.findByRefreshToken(token).orElse(null);
        if(toDeletetoken != null) {
            refreshTokenRepository.delete(toDeletetoken);
            return refreshTokenRepository.findById(toDeletetoken.getId()).isEmpty();
        }
        return false;
    }

    public RefreshTokenResponse refreshToken(String token){
       return refreshTokenRepository.findByRefreshToken(token)
                .map(jwtUtils::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String jwtToken = jwtUtils.generateJwtToken(user);
                    return new RefreshTokenResponse(jwtToken, token);
                })
                .orElseThrow(() -> new TokenRefreshException("Refresh token is not valid or expired!"));
    }
}
