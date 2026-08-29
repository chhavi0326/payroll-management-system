package com.chhavi.payroll.security;

import com.chhavi.payroll.dto.LoginRequest;
import com.chhavi.payroll.dto.LoginResponse;
import com.chhavi.payroll.entity.AppUser;
import com.chhavi.payroll.repository.AppUserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final AppUserRepository appUserRepository;
    private final JwtService jwtService;

    public AuthService(
            AuthenticationManager authenticationManager,
            AppUserRepository appUserRepository,
            JwtService jwtService) {

        this.authenticationManager = authenticationManager;
        this.appUserRepository = appUserRepository;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest request) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getUsername(),
                                request.getPassword()
                        )
                );

        AppUser user =
                appUserRepository.findByUsername(
                        authentication.getName()
                ).orElseThrow();

        String token =
                jwtService.generateToken(
                        (org.springframework.security.core.userdetails.UserDetails)
                                authentication.getPrincipal()
                );

        return new LoginResponse(
                user.getUsername(),
                user.getRole().name(),
                token,
                "Authentication successful"
        );
    }
}