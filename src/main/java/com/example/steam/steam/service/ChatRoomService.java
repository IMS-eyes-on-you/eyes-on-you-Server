package com.example.steam.steam.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;



@RequiredArgsConstructor
@Service
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
                emitter.send(SseEmitter.event().data("exit"));
                System.out.println("강의종료");
            } catch (Exception e) {
                this.emitter = null;
            }
        }
    }
}
