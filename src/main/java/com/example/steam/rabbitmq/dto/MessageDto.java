package com.example.steam.rabbitmq.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor

public class MessageDto {
    @JsonProperty("xRatio")
    private Double xRatio;
    @JsonProperty("yRatio")
    private Double yRatio;
    @JsonProperty("xPosition")
    private int xPosition;
    @JsonProperty("yPosition")
    private int yPosition;

}