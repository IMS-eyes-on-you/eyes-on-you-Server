package com.example.steam.steam.controller;

import com.example.steam.steam.dto.CreateChatRoomDto;
import com.example.steam.steam.dto.KurentoRoomDto;
import com.example.steam.steam.service.ChatServiceMain;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@CrossOrigin(origins = "*")
@RestController
@RequiredArgsConstructor
@Slf4j
public class ChatRoomController {
    private final ChatServiceMain chatServiceMain;

    @PostMapping("/chat/createroom")
    public String createRoom(@RequestBody CreateChatRoomDto createChatRoomDto){
        KurentoRoomDto room = chatServiceMain.createChatRoom(createChatRoomDto.getRoomName(),
                Integer.parseInt(createChatRoomDto.getMaxUserCnt()), createChatRoomDto.getChatType(), createChatRoomDto.getName());

        log.info("CREATE Chat Room[{}]", room);
        log.info("Host name is [{}]", room.getUserId());
        return "good";
    }

    @GetMapping("/chat/allrooms")
    public Set<String> allRooms(){
        return chatServiceMain.findAllRooms();
    }
}
