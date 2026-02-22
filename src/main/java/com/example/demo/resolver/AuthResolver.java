package com.example.demo.resolver;

import com.example.demo.dto.LoginResponse;
import com.example.demo.security.JwtService;
import com.example.demo.service.UserService;
import com.example.demo.dto.RegisterRequest;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class AuthResolver {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthResolver(UserService userService,
                        AuthenticationManager authenticationManager,
                        JwtService jwtService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @MutationMapping
    public LoginResponse login(@Argument String username, @Argument String password) {
        Authentication auth = new UsernamePasswordAuthenticationToken(username, password);
        Authentication result = authenticationManager.authenticate(auth);

        List<String> roles = result.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .toList();

        String token = jwtService.generateToken(result.getName(), roles);
        return new LoginResponse(token);
    }

    @MutationMapping
    public String register(@Argument String username, @Argument String password) {
        RegisterRequest request = new RegisterRequest();
        request.setUsername(username);
        request.setPassword(password);
        userService.register(request);
        return "User registered successfully";
    }
}
