package com.springboot.orderservice.event;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderPlacedEvent{
    private String orderNumber;
}