package com.example.steam.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserDto {
    public String email;
    public String role;
    public String name;
}
