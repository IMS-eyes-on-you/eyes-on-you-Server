package com.example.steam.auth.Service;

import com.example.steam.auth.Entity.User;
import com.example.steam.auth.Repository.UserRepository;
import com.example.steam.auth.dto.GeneratedToken;
import com.example.steam.auth.dto.SignUpDto;
import com.example.steam.auth.dto.SignInDto;
import com.example.steam.auth.filter.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public GeneratedToken signIn(SignInDto signInDto){
        User user = userRepository.findById(signInDto.getId()).orElse(User.builder().build());
        if(user.getId() == null || !passwordEncoder.matches(signInDto.getPassword(), user.getPassword())){
            return GeneratedToken.builder().build();
        }
        return jwtUtil.generateToken(user.getEmail(), user.getRole());
    }

    public void saveInfo(SignUpDto signUpDto){
        userRepository.save(
                User.builder()
                        .id(signUpDto.getId())
                        .password(passwordEncoder.encode(signUpDto.getPassword()))
                        .email(signUpDto.getEmail())
                        .role(signUpDto.getRole())
                        .build()
        );

    }
}
