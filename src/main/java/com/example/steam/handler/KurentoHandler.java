package com.example.steam.handler;

import com.example.steam.dto.KurentoRoomDto;
import com.example.steam.service.KurentoManager;
import com.example.steam.service.KurentoRegistryService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kurento.client.IceCandidate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class KurentoHandler extends TextWebSocketHandler {
    private static final Gson gson = new GsonBuilder().create();


    private final KurentoRegistryService registry;

    private final KurentoManager roomManger;


    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        final JsonObject jsonMessage = gson.fromJson(message.getPayload(), JsonObject.class);
        KurentoUserSession user = null;
        try{
            user = registry.getBySession(session);
        }
        catch (Exception e){
            if(user != null){
                log.info("Incoming message from user '{}': {}", user.getName(), jsonMessage);
            }
            else{
                log.info("Incoming message from new user '{}'", jsonMessage);
            }
        }

        switch (jsonMessage.get("id").getAsString()){
            case "joinRoom": // value : joinRoom 인 경우
                joinRoom(jsonMessage, session); // joinRoom 메서드를 실행
                break;

            case "receiveVideoFrom": // receiveVideoFrom 인 경우
                try {
                    // sender 명 - 사용자명 - 과
                    final String senderName = jsonMessage.get("sender").getAsString();
                    // 유저명을 통해 session 값을 가져온다
                    final KurentoUserSession sender = registry.getByName(senderName);
                    // jsonMessage 에서 sdpOffer 값을 가져온다
                    final String sdpOffer = jsonMessage.get("sdpOffer").getAsString();

                    // 이후 receiveVideoFrom 실행 => 아마도 특정 유저로부터 받은 비디오를 다른 유저에게 넘겨주는게 아닌가...?
                    user.receiveVideoFrom(sender, sdpOffer);
                } catch (Exception e){
                    connectException(user, e);
                }
                break;

            case "leaveRoom": // 유저가 나간 경우
                leaveRoom(user);
                break;

            case "onIceCandidate": // 유저에 대해 IceCandidate 프로토콜을 실행할 때
                JsonObject candidate = jsonMessage.get("candidate").getAsJsonObject();

                if (user != null) {
                    IceCandidate cand = new IceCandidate(candidate.get("candidate").getAsString(),
                            candidate.get("sdpMid").getAsString(), candidate.get("sdpMLineIndex").getAsInt());
                    user.addCandidate(cand, jsonMessage.get("name").getAsString());
                }
                break;

            default:
                break;
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        KurentoUserSession user = registry.removeBySession(session);
        this.leaveRoom(user);
    }



    private void joinRoom(JsonObject params, WebSocketSession session) throws IOException {
        final String roomName = params.get("room").getAsString();
        final String name = params.get("name").getAsString();
        log.info("participant '{}': trying to join room {}", name, roomName);

        KurentoRoomDto room = roomManger.getRoom(roomName);

        final KurentoUserSession user = room.join(name, session);

        registry.register(user);
    }

    private void leaveRoom(KurentoUserSession user) throws IOException {
        final KurentoRoomDto room = roomManger.getRoom(user.getRoomName());
        room.leave(user);
        room.setUserCount(room.getUserCount() - 1);
    }

    private void connectException(KurentoUserSession user, Exception e) throws IOException {
        JsonObject message = new JsonObject();
        message.addProperty("id", "ConnectionFail");
        message.addProperty("data", e.getMessage());

        user.sendMessage(message);

    }
}
