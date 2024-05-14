package com.springboot.notificationservice.event;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderPlacedEvent {
    private String orderNumber;
}