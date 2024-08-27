package com.example.steam.steam.service;

import com.example.steam.steam.dto.ChatRoomMap;
import com.example.steam.steam.dto.KurentoRoomDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatServiceMain {
    private final KurentoManager kurentoManager;
    private final RtcChatService rtcChatService;

    public KurentoRoomDto createChatRoom(String roomName, int maxUserCnt, String chatType, String name){
        KurentoRoomDto room;

        room = rtcChatService.createChatRoom(roomName, maxUserCnt, name);

        return room;
    }

    public Set<String> findAllRooms() {

        log.info("{}",ChatRoomMap.getInstance().getChatRooms().keySet());
        return ChatRoomMap.getInstance().getChatRooms().keySet();
    }
}
