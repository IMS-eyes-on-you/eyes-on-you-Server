package com.example.steam.steam.service;

import com.example.steam.steam.dto.ChatRoomMap;
import com.example.steam.steam.dto.KurentoRoomDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentMap;

@Service
@Slf4j
public class KurentoManager {
    private final ConcurrentMap<String, ?> rooms = ChatRoomMap.getInstance().getChatRooms();

    public KurentoRoomDto getRoom(String roomId){
        log.info("Search room{}", roomId);


        KurentoRoomDto room = (KurentoRoomDto) rooms.get(roomId);

        if(room == null){
            room = new KurentoRoomDto();
        }
        return room;
    }

    public void removeRoom(KurentoRoomDto room) {
        // rooms 에서 room 객체 삭제 => 이때 room 의 Name 을 가져와서 조회 후 삭제
        this.rooms.remove(room.getRoomId());


        room.close();

        log.info("Room {} removed and closed", room.getRoomId());
    }
}
