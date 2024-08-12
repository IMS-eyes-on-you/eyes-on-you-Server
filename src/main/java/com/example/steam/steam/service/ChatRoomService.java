package com.example.steam.steam.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public class ChatRoomService {
    private SseEmitter emitter;

    public SseEmitter getEmitter() {
        return emitter;
    }

    public void setEmitter(SseEmitter emitter) {
        this.emitter = emitter;
    }

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
