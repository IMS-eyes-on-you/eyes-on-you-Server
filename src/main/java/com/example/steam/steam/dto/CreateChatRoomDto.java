package com.example.steam.steam.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateChatRoomDto {
    private String name;
    private String maxUserCnt;
    private String chatType;
}
