package com.example.rabbitmq.config;


import com.example.rabbitmq.handler.DocWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new DocWebSocketHandler(), "/ws/files")
                .setAllowedOrigins("*");

        registry.addHandler(new DocWebSocketHandler(), "/ws/students")
                .setAllowedOrigins("*");
    }
}
