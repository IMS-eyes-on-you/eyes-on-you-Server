package com.example.steam.rabbitmq.controller;

import com.example.steam.rabbitmq.dto.MessageDto;
import com.example.steam.rabbitmq.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RequiredArgsConstructor
@RestController
@RequestMapping("/send")
public class EyeDataController {
    private final MessageService messageService;
    @PostMapping( "/message")
    public ResponseEntity<String> sendMessage(@RequestBody List<MessageDto> messageDto) {
        messageService.sendMessage(messageDto);
        return ResponseEntity.ok("Message sent");
    }

}