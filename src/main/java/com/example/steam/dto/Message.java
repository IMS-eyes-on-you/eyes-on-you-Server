package com.example.steam.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Message {
    private String sender;
    private String type;
    private String receiver;
    private String room;
    private Object allUsers;
    private Object sdp;
    private Object candidate;
}
