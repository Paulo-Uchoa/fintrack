package com.paulouchoa.fintrack.auth;

import com.paulouchoa.fintrack.auth.dto.AuthResponse;
import com.paulouchoa.fintrack.auth.dto.LoginRequest;
import com.paulouchoa.fintrack.auth.dto.RegisterRequest;
import com.paulouchoa.fintrack.common.BusinessException;
import com.paulouchoa.fintrack.security.AppUserDetails;
import com.paulouchoa.fintrack.security.JwtService;
import com.paulouchoa.fintrack.user.Role;
import com.paulouchoa.fintrack.user.User;
import com.paulouchoa.fintrack.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessException("Email already registered");
        }
        User user = new User(request.name(), request.email(),
                passwordEncoder.encode(request.password()), Role.USER);
        userRepository.save(user);
        return buildResponse(new AppUserDetails(user));
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        return buildResponse((AppUserDetails) authentication.getPrincipal());
    }

    private AuthResponse buildResponse(AppUserDetails principal) {
        String token = jwtService.generateToken(principal);
        return new AuthResponse(token, "Bearer", jwtService.getExpirationMinutes(),
                principal.getUser().getName(), principal.getUsername());
    }
}
