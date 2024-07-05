package com.example.steam.dto;

import com.example.steam.enums.ChatType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateChatRoomDto {
    private String name;
    private String maxUserCnt;
    private String chatType;
}
