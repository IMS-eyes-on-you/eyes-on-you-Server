package com.example.steam.handler;


import com.example.steam.dto.Message;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class SignalingHandler extends TextWebSocketHandler {
    private static final Set<WebSocketSession> sessions = Collections.synchronizedSet(new HashSet<>());


    //연결 시작
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info(">>> [ws] 클라이언트 접속 : 세션 - {}", session);
        sessions.add(session);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        log.info(">>> [ws] 클라이언트 송신 : 세션 - {} 내용 -{}", session,message);
        for (WebSocketSession ws : sessions) {
            if (ws.isOpen() && !ws.getId().equals(session.getId())) {
                ws.sendMessage(message);
            }
        }
    }


    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        super.handleTransportError(session, exception);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info(">>> [ws] 클라이언트 종료 : 세션 - {}", session);
        sessions.remove(session);
    }


}
