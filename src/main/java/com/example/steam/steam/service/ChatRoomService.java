package com.example.steam.steam.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Setter
@Getter
@RequiredArgsConstructor
@Service
public class ChatRoomService {
    private SseEmitter emitter;
    
    public void notifyExitClass() {
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event().data("강의가 종료되었습니다."));
                System.out.println("send");
            } catch (Exception e) {
                this.emitter = null;
            }
        }
    }
}
