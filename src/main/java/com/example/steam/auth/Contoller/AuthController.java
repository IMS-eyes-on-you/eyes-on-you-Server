package com.example.steam.auth.Contoller;

import com.example.steam.auth.Service.AuthService;
import com.example.steam.auth.dto.GeneratedToken;
import com.example.steam.auth.dto.SignInDto;
import com.example.steam.auth.dto.SignUpDto;
import com.example.steam.auth.filter.JwtUtil;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;


    @PostMapping("/signup")
    public void SignUp(@RequestBody SignUpDto signUpDto){
        authService.saveInfo(signUpDto);

    }

    @PostMapping("/signin")
    public GeneratedToken SignIn(@RequestBody SignInDto signInDto){
        GeneratedToken tokens = authService.signIn(signInDto);
        return tokens;
    }

}
