package com.WebSocket.ChatAppWithPostgres.Auth;

import com.WebSocket.ChatAppWithPostgres.Config.JwtService;
import com.WebSocket.ChatAppWithPostgres.Model.User.Role;
import com.WebSocket.ChatAppWithPostgres.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import com.WebSocket.ChatAppWithPostgres.Model.User.User;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    public AuthenticationResponse register(RegisterRequest request) {
        try {
            var user = User.builder()
                    .userName(request.getUserName())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .role(Role.USER)
                    .build();

            userRepo.save(user);

            var jwtToken = jwtService.generateToken(user);
            return AuthenticationResponse
                    .builder()
                    .token(jwtToken)
                    .build();
        } catch (Exception e) {
            return AuthenticationResponse
                    .builder()
                    .token(String.valueOf(e))
                    .build();
        }
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUserName(),
                        request.getPassword()
                )
        );
        //maybe will be error here
        var user = userRepo.findByUserName(request.getUserName()).orElseThrow();

        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse
                .builder()
                .token(jwtToken)
                .build();
    }
}
