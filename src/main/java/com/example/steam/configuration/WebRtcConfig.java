package com.example.steam.configuration;

import com.example.steam.rabbitmq.handler.DocWebSocketHandler;
import com.example.steam.steam.handler.KurentoHandler;
import com.example.steam.steam.service.KurentoManager;
import com.example.steam.steam.service.KurentoRegistryService;
import lombok.RequiredArgsConstructor;
import org.kurento.client.KurentoClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebRtcConfig implements WebSocketConfigurer {

    private final KurentoRegistryService registry;

    private final KurentoManager roomManager;

    @Bean
    public KurentoHandler kurentoHandler(){
        return new KurentoHandler(registry, roomManager);
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new KurentoHandler(this.registry, roomManager), "/signal")
                .setAllowedOrigins("*");

        registry.addHandler(new DocWebSocketHandler(), "ws/files")
                .setAllowedOrigins("*");

        registry.addHandler(new DocWebSocketHandler(), "ws/students")
                .setAllowedOrigins("*");
    }

    @Bean
    public KurentoClient kurentoClient(){
        return KurentoClient.create();
    }

    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer(){
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxTextMessageBufferSize(8192);
        container.setMaxBinaryMessageBufferSize(8192);
        return container;
    }
}
