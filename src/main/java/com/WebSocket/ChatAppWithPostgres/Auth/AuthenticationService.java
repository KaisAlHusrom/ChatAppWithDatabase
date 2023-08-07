package com.WebSocket.ChatAppWithPostgres.Auth;

import com.WebSocket.ChatAppWithPostgres.Config.JwtService;
import com.WebSocket.ChatAppWithPostgres.Model.Token.Token;
import com.WebSocket.ChatAppWithPostgres.Model.Token.TokenType;
import com.WebSocket.ChatAppWithPostgres.Model.User.Role;
import com.WebSocket.ChatAppWithPostgres.Repository.TokenRepository;
import com.WebSocket.ChatAppWithPostgres.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import com.WebSocket.ChatAppWithPostgres.Model.User.User;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepo;
    private final TokenRepository tokenRepository;
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

            var savedUser = userRepo.save(user);


            var jwtToken = jwtService.generateToken(user);
            saveUserToken(savedUser, jwtToken);

            return AuthenticationResponse
                    .builder()
                    .response(Map.of(
                            "Success", true,
                            "Message", "Registered Successfully",
                            "Token", jwtToken
                    ))
                    .build();
        } catch (Exception e) {
            return AuthenticationResponse
                    .builder()
                    .response(Map.of(
                            "Success", false,
                            "Message", "Registered Failed",
                            "Reason", e.getMessage()
                    ))
                    .build();
        }

    }



    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUserName(),
                            request.getPassword()
                    )
            );
            //maybe will be error here
            var user = userRepo.findByUserName(request.getUserName()).orElseThrow();

            revokeAllUserTokens(user);

            var jwtToken = jwtService.generateToken(user);

            saveUserToken(user, jwtToken);

            return AuthenticationResponse
                    .builder()
                    .response(Map.of(
                            "Success", true,
                            "Message", "Logged in Successfully",
                            "Token", jwtToken,
                            "UserInfo", user
                    ))
                    .build();
        } catch (Exception e) {
            return AuthenticationResponse
                    .builder()
                    .response(Map.of(
                            "Success", false,
                            "Message", "Login Failed",
                            "Reason", e.getMessage()
                    ))
                    .build();
        }
    }

    private void revokeAllUserTokens(User user) {
        var validTokens = tokenRepository.findAllValidTokensByUser(user.getId());
        if (validTokens.isEmpty()) return;

        validTokens.forEach(token -> {
            token.setRevoked(true);
            token.setExpired(true);
        });

        tokenRepository.saveAll(validTokens);
    }

    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .revoked(false)
                .expired(false)
                .build();

        tokenRepository.save(token);
    }
}
