package com.example.steam.steam.dto;

import com.example.steam.steam.enums.ChatType;
import com.example.steam.steam.handler.KurentoUserSession;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.kurento.client.KurentoClient;
import org.kurento.client.MediaPipeline;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Getter
@Setter
@NoArgsConstructor
@Slf4j
public class KurentoRoomDto{
    private KurentoClient kurento;
    private MediaPipeline pipeline;
    private String userId;
    private String roomId;
    private String roomName;
    private int userCount = 0;
    private int maxUserCnt;
    private ChatType chatType;
    private boolean isHost = false;

    private ConcurrentMap<String, KurentoUserSession> participants;
    public void setRoomInfo(String roomId, String roomName, ChatType chatType, KurentoClient kurento, String userId){
        this.userId = userId;
        this.kurento = kurento;
        this.roomId = roomId;
        this.chatType = chatType;
        this.roomName = roomName;
        this.participants = new ConcurrentHashMap<>();
    }

    public void createPipeline(){
        this.pipeline = this.kurento.createMediaPipeline();
        log.info("pipline : {} ",this.pipeline);
    }

    public KurentoUserSession join(String name, WebSocketSession session, String roomName) throws IOException {
        final KurentoUserSession participant = new KurentoUserSession(name, roomName, session, this.pipeline);

        joinRoom(participant);

        participants.put(participant.getName(), participant);

        sendParticipantNames(participant);
        userCount++;

        return participant;
    }

    public void leave(KurentoUserSession user) throws IOException {
        log.info("PARTICIPANT {}: Leaving room {}", user.getName(), this.roomId);
        this.removeParticipant(user.getName());

        user.close();
    }

    public void close(){
        for (final KurentoUserSession user : participants.values()) {
            // 유저 close
            user.close();
        } // for 문 끝

        // 유저 정보를 담은 map - participants - 초기화
        participants.clear();

        pipeline.release();
    }

    public Collection<String> joinRoom(KurentoUserSession newParticipant){

        final JsonObject newParticipantMsg = new JsonObject();
        newParticipantMsg.addProperty("id", "newParticipantArrived");
        JsonObject joNewParticipant = new JsonObject();
        joNewParticipant.addProperty("name", newParticipant.getName());
        newParticipantMsg.add("data", joNewParticipant);

        final List<String> participantsList = new ArrayList<>(participants.values().size());
        log.debug("ROOM {}: 다른 참여자들에게 새로운 참여자가 들어왔음을 알림 {} :: {}", roomId,
                newParticipant.getName(), newParticipant.getRoomName());

        for (final KurentoUserSession participant: participants.values()) {
            try{
                participant.sendMessage(newParticipantMsg);
            }
            catch (IOException e){
                log.error("ROOM {}: participant {} could not be notified", roomId, participant.getName(), e);
            }

            participantsList.add(participant.getName());
        }
        return participantsList;


    }

    private void removeParticipant(String name) throws IOException {

        // participants map 에서 제거된 유저 - 방에서 나간 유저 - 를 제거함
        participants.remove(name);

        log.debug("ROOM {}: notifying all users that {} is leaving the room", this.roomId, name);

        // String list 생성
        final List<String> unNotifiedParticipants = new ArrayList<>();

        // json 객체 생성
        final JsonObject participantLeftJson = new JsonObject();

        // json 객체에 유저가 떠났음을 알리는 jsonObject
        // newParticipantMsg : { "id" : "participantLeft", "name" : "참여자 유저명"}
        participantLeftJson.addProperty("id", "participantLeft");
        participantLeftJson.addProperty("name", name);

        // participants 의 value 로 for 문 돌림
        for (final KurentoUserSession participant : participants.values()) {
            try {
                // 나간 유저의 video 를 cancel 하기 위한 메서드
                participant.cancelVideoFrom(name);

                // 다른 유저들에게 현재 유저가 나갔음을 알리는 jsonMsg 를 전달
                participant.sendMessage(participantLeftJson);

            } catch (final IOException e) {
                unNotifiedParticipants.add(participant.getName());
            }
        }

        // 만약 unNotifiedParticipants 가 비어있지 않다면
        if (!unNotifiedParticipants.isEmpty()) {
            log.debug("ROOM {}: The users {} could not be notified that {} left the room", this.roomId,
                    unNotifiedParticipants, name);
        }

    }

    public void sendParticipantNames(KurentoUserSession user) throws IOException {
        final JsonObject existingParticipantsMsg = new JsonObject();

        final JsonArray participantsArray = new JsonArray();

        for (final KurentoUserSession participant: this.getParticipants().values()) {
            if(!participant.getName().equals(user.getName())){
                JsonObject exisingUser  = new JsonObject();
                exisingUser.addProperty("name", participant.getName());

                participantsArray.add(exisingUser);
            }
        }

        existingParticipantsMsg.addProperty("id", "existingParticipants");
        existingParticipantsMsg.add("data", participantsArray);
        log.debug("PARTICIPANT {}: sending a list of {} participants", user.getName(), participantsArray.size());

        // user 에게 existingParticipantsMsg 전달
        user.sendMessage(existingParticipantsMsg);
    }

    public void sendHostIsOut(){
        final JsonObject msg = new JsonObject();
        msg.addProperty("id", "hostExit");
        msg.addProperty("name", "name");
        for (final KurentoUserSession participant : participants.values()) {
            try {
                // 다른 유저들에게 현재 유저가 나갔음을 알리는 jsonMsg 를 전달
                participant.sendMessage(msg);

            } catch (final IOException e) {
                log.info("host exit error");
            }
        }
    }




}
