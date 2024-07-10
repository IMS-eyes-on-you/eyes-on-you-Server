package com.example.steam.rabbitmq.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Float xRatio;

    private Float yRatio;
    private Float xPosition;
    private Float yPosition;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Float getxRatio() {
        return xRatio;
    }

    public void setxRatio(Float xRatio) {
        this.xRatio = xRatio;
    }

    public Float getyRatio() {
        return yRatio;
    }

    public void setyRatio(Float yRatio) {
        this.yRatio = yRatio;
    }

    public Float getxPosition() {
        return xPosition;
    }

    public void setxPosition(Float xPosition) {
        this.xPosition = xPosition;
    }

    public Float getyPosition() {
        return yPosition;
    }

    public void setyPosition(Float yPosition) {
        this.yPosition = yPosition;
    }

}