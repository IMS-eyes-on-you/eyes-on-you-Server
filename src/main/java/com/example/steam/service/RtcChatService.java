package com.example.steam.service;

import com.example.steam.dto.ChatRoomMap;
import com.example.steam.dto.KurentoRoomDto;
import com.example.steam.enums.ChatType;
import com.example.steam.handler.KurentoUserSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kurento.client.KurentoClient;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class RtcChatService {
    private final KurentoClient kurento;

    public KurentoRoomDto createChatRoom(String roomName, int maxUserCnt) {

        KurentoRoomDto room = new KurentoRoomDto();
        String roomId = UUID.randomUUID().toString();
        room.setRoomInfo(roomId, roomName,ChatType.RTC, kurento);

        // 파이프라인 생성
        room.createPipeline();

        room.setParticipants(new ConcurrentHashMap<String, KurentoUserSession>());

        // map 에 채팅룸 아이디와 만들어진 채팅룸을 저장
        ChatRoomMap.getInstance().getChatRooms().put(roomName, room);

        return room;
    }
}
