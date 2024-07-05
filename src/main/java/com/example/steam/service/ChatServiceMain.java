package com.example.steam.service;

import com.example.steam.dto.KurentoRoomDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatServiceMain {
    private final KurentoManager kurentoManager;
    private final RtcChatService rtcChatService;

    public KurentoRoomDto createChatRoom(String roomName, int maxUserCnt, String chatType){
        KurentoRoomDto room;

        room = rtcChatService.createChatRoom(roomName, maxUserCnt);

        return room;
    }
}
