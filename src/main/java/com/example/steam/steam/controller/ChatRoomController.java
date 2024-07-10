package com.example.steam.steam.controller;

import com.example.steam.steam.dto.CreateChatRoomDto;
import com.example.steam.steam.dto.KurentoRoomDto;
import com.example.steam.steam.service.ChatServiceMain;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequiredArgsConstructor
@Slf4j
public class ChatRoomController {
    private final ChatServiceMain chatServiceMain;

    @PostMapping("/chat/createroom")
    public String createRoom(@RequestBody CreateChatRoomDto createChatRoomDto){
        KurentoRoomDto room = chatServiceMain.createChatRoom(createChatRoomDto.getName(),
                Integer.parseInt(createChatRoomDto.getMaxUserCnt()), createChatRoomDto.getChatType());


        log.info("CREATE Chat Room[{}]", room);
        return "good";
    }
}
