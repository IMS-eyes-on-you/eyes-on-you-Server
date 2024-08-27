package com.example.steam.steam.controller;

import com.example.steam.steam.service.ChatRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
public class ClassRoomSSE {
    private ChatRoomService chatRoomService;

    @Autowired
    public ClassRoomSSE(ChatRoomService chatRoomService) {
        this.chatRoomService = chatRoomService;
    }
    @GetMapping("/alert")
    public SseEmitter subscribeToAlerts() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        this.chatRoomService.setEmitter(emitter);

        emitter.onCompletion(() -> this.chatRoomService.setEmitter(null));
        emitter.onTimeout(() -> this.chatRoomService.setEmitter(null));
        emitter.onError((e) -> this.chatRoomService.setEmitter(null));

        return emitter;
    }
}
