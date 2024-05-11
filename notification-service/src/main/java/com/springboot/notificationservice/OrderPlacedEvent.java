package com.springboot.notificationservice;

import lombok.*;

@Getter
@Setter
public class OrderPlacedEvent {
    private String orderNumber;
}