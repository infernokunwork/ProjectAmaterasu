package com.infernokun.amaterasu.services;

import com.infernokun.amaterasu.exceptions.AuthFailedException;
import com.infernokun.amaterasu.exceptions.WrongPasswordException;
import com.infernokun.amaterasu.models.dto.LoginResponseDTO;
import com.infernokun.amaterasu.models.entities.RefreshToken;
import com.infernokun.amaterasu.models.entities.User;
import com.infernokun.amaterasu.repositories.UserRepository;
import com.infernokun.amaterasu.services.alt.TokenService;
import com.infernokun.amaterasu.services.base.BaseService;
import jakarta.transaction.Transactional;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
public class AuthenticationService extends BaseService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;

    public AuthenticationService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, TokenService tokenService, RefreshTokenService refreshTokenService, UserService userService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.refreshTokenService = refreshTokenService;
        this.userService = userService;
    }

    public boolean registerUser(User user) {
        if (user == null || user.getUsername() == null || user.getPassword() == null) {
            throw new AuthFailedException("Username and password required!");
        }

        if (userService.existsByUsername(user)) {
            throw new AuthFailedException("Username already exists!");
        }

        String encodedPassword = this.passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        userRepository.save(user);

        LOGGER.info("User registered: {}", user.getUsername());
        return true;
    }

    public LoginResponseDTO loginUser(String username, String password) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            UserDetails userDetails = (UserDetails) auth.getPrincipal();
            String token = tokenService.generateJwt(userDetails);
            return new LoginResponseDTO(token);
        } catch (BadCredentialsException e) {
            throw new WrongPasswordException("Invalid username or password");
        } catch (AuthenticationException e) {
            throw new AuthFailedException("Authentication failed");
        }
    }

    public LoginResponseDTO revalidateToken(String oldToken) {
        LOGGER.info("old token: {}", oldToken);
        Optional<RefreshToken> oldRefreshToken = this.refreshTokenService.findByToken(oldToken);
        LOGGER.info("OLD REFRESH TOKEN - Attempting!");
        if (oldRefreshToken.isPresent()) {
            LOGGER.info("OLD REFRESH TOKEN - Found!");
            if (Objects.equals(oldRefreshToken.get().getToken(), oldToken)) {
                LOGGER.info("OLD REFRESH TOKEN - Matches database for user {}!", oldRefreshToken.get().getUser().getId());
                Optional<User> user = this.userRepository.findById(oldRefreshToken.get().getUser().getId());
                if (user.isPresent()) {
                    LOGGER.info("OLD REFRESH TOKEN - Token replaced!");
                    String token = this.tokenService.generateJwt(user.get());
                    return new LoginResponseDTO(token);
                }
            }
        }
        return null;
    }
}
