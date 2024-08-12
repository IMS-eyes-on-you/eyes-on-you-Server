package com.example.steam.auth.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.*;

@Builder
@Setter
@Getter
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@AllArgsConstructor
@NoArgsConstructor
public class SignUpDto {
    private String id;
    private String password;
    private String email;
    private String role;
    private String name;
}
