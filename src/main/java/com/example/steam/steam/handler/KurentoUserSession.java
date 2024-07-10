package com.example.steam.steam.handler;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kurento.client.*;
import org.kurento.jsonrpc.JsonUtils;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@RequiredArgsConstructor
@Slf4j
@Getter
public class KurentoUserSession implements Closeable {
    private final String name;
    private final String roomName;
    private final WebSocketSession session;
    private final MediaPipeline pipeline;

    //나의 webRtcEndPoint 객체
    private final WebRtcEndpoint outgoingMedia;

    private final ConcurrentMap<String, WebRtcEndpoint> incomingMedia = new ConcurrentHashMap<>();

    public KurentoUserSession( String name, String roomName, WebSocketSession session, MediaPipeline pipeline){
        this.pipeline = pipeline;
        this.name = name;
        this.session = session;
        this.roomName = roomName;

        this.outgoingMedia = new WebRtcEndpoint.Builder(pipeline).build();

        this.outgoingMedia.addIceCandidateFoundListener(new EventListener<IceCandidateFoundEvent>() {
            @Override
            public void onEvent(IceCandidateFoundEvent event) {
                JsonObject response = new JsonObject();
                response.addProperty("id", "iceCandidate");
                response.addProperty("name", name);
                response.add("candidate", JsonUtils.toJsonObject(event.getCandidate()));

                try{
                    synchronized (session){
                        session.sendMessage(new TextMessage(response.toString()));
                    }
                }
                catch (IOException e){
                    log.info(e.getMessage());
                }
            }
        });
    }

    public WebRtcEndpoint getOutgoingWebRtcPeer(){
        return outgoingMedia;
    }

    public ConcurrentMap<String, WebRtcEndpoint> getIncomingMedia(){
        return incomingMedia;
    }
    public WebSocketSession getSession(){
        return session;
    }

    public String getName(){
        return name;
    }

    public String getRoomName(){
        return this.roomName;
    }

    public void receiveVideoFrom(KurentoUserSession sender, String sdpOffer) throws IOException {
        //room에 들어왔을 때 알림
        log.info("USER{}:connecting with {} is {}", this.name, sender.getName(), this.roomName);

        //유저가 sdp 제안
        log.info("USER{} : SdpOffer for {} is {}", this.name, sender.getName(), sdpOffer);

        final String ipSdpAnswer = this.getEndpointForUser(sender).processOffer(sdpOffer);
        final JsonObject scParams = new JsonObject();
        scParams.addProperty("id", "receiveVideoAnswer");
        scParams.addProperty("name", sender.getName());
        scParams.addProperty("sdpAnswer", ipSdpAnswer);

        log.info("USER{} : SdpAnswer for {} is {}", this.name, sender.getName(), ipSdpAnswer);
        this.sendMessage(scParams);
        this.getEndpointForUser(sender).gatherCandidates(new Continuation<Void>() {
            @Override
            public void onSuccess(Void result) throws Exception {
                log.info("USER {} : gatherCandidates Success for {}", sender.getName(), sender.getName());
            }

            @Override
            public void onError(Throwable cause) throws Exception {
                log.info("USER {} : gatherCandidates fail for {}", sender.getName(), sender.getName());
                cause.printStackTrace();
            }
        });
    }

    private WebRtcEndpoint getEndpointForUser(final KurentoUserSession sender){

        //sender가 현재 user랑 일치할 때 loop가 돈다.
        if(sender.getName().equals(name)){
            log.info("PARTICIPANT {}: configuring", this.name);
            return outgoingMedia;
        }

        log.info("PARTICIPANT {}: receiveing video from {}", this.name, sender.getName());

        //sender의 이름으로 나의 incomingMedia에서 sender의 webrtcEndpoint 객체를 가져옴
        WebRtcEndpoint incoming = incomingMedia.get(sender.getName());

        //내가 갖고 있는 incomingMedia에 sender의 webrtcEndPoint 객체가 없다면
        if(incoming == null){
            //endpoint 만드는 것 확인
            log.info("PARTICIPANT {}: creating new endpoint for {}", this.name, sender.getName());

            //새거 만들기
            incoming = new WebRtcEndpoint.Builder(pipeline).build();

            incoming.addIceCandidateFoundListener(new EventListener<IceCandidateFoundEvent>() {
                @Override
                public void onEvent(IceCandidateFoundEvent event) {
                    JsonObject response = new JsonObject();
                    response.addProperty("id", "iceCandidate");
                    response.addProperty("name", sender.getName());
                    response.add("candidate", JsonUtils.toJsonObject(event.getCandidate()));

                    try{
                        synchronized (session){
                            session.sendMessage(new TextMessage(response.toString()));
                        }
                    }
                    catch (IOException e){
                        log.info(e.getMessage());
                    }
                }
            });

            //유저명과 새로 생성된 endpoint 객체 넣기
            incomingMedia.put(sender.getName(), incoming);
        }
        log.info("PARTICIPANT {}: obtained endpoint for {}", this.name, sender.getName());

        //기존에 갖고 있던 webRtcEndPoint와 새로 생성된 Incoming을 연결
        sender.getOutgoingWebRtcPeer().connect(incoming);
        return incoming;

    }

    public void cancelVideoFrom(final KurentoUserSession sender){
        this.cancelVideoFrom(sender.getName());
    }

    public void cancelVideoFrom(final String senderName){
        log.info("PARTICIPANT {}: cancel video from {}", this.name, senderName);
        final WebRtcEndpoint incoming = incomingMedia.remove(senderName);
        log.info("PARTICIPANT {}: remove endpoint from {}", this.name, senderName);
        incoming.release(new Continuation<Void>() {
            @Override
            public void onSuccess(Void result) throws Exception {
                log.info("Success");
            }

            @Override
            public void onError(Throwable cause) throws Exception {
                log.info("not release incoming");
            }
        });
    }

    @Override
    public void close(){
        log.info("PARTICIPANT {}: Releasing resources", this.name);
        for (final String remoteParticipantName: incomingMedia.keySet()) {
            log.info("PARTICIPANT {}: Released incoming for {}", this.name, remoteParticipantName);

            final WebRtcEndpoint ep = this.incomingMedia.get(remoteParticipantName);

            ep.release();

            outgoingMedia.release();
        }
    }

    public void sendMessage(JsonObject message) throws IOException {
        log.info("USER {}: Sending message {}", this.name, message);
        synchronized (session){
            try{
                session.sendMessage(new TextMessage(message.toString()));
            }
            catch (Exception e){
                log.error("bug", e);
            }

        }
    }

    public void addCandidate(IceCandidate candidate, String name){
        if(this.name.compareTo(name) == 0){
            log.info("USER {} : outgoingMedia.addIceCandidate : {} ", this.name, name);
            outgoingMedia.addIceCandidate(candidate);

        }
        else{
            WebRtcEndpoint webRtc = incomingMedia.get(name);
            if(webRtc != null){
                webRtc.addIceCandidate(candidate);
            }
        }
    }


    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof KurentoUserSession)) {
            return false;
        }
        KurentoUserSession other = (KurentoUserSession) obj;
        boolean eq = name.equals(other.name);
        eq &= roomName.equals(other.roomName);
        return eq;
    }


    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + name.hashCode();
        result = 31 * result + roomName.hashCode();
        return result;
    }


}
