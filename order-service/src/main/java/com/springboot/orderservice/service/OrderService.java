package com.springboot.orderservice.service;


import com.springboot.orderservice.dto.OrderLineItemsDto;
import com.springboot.orderservice.model.OrderLineItems;
import com.springboot.orderservice.dto.OrderRequest;
import com.springboot.orderservice.model.Order;
import com.springboot.orderservice.repository.OrderRepository;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public void placeOrder(OrderRequest orderRequest) {
        // Place order logic
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItems> orderLineItemsList = orderRequest.getOrderLineItemsDtoList().stream().map(orderLineItemsDto -> OrderLineItems.builder()
                    .id(orderLineItemsDto.getId())
                    .skuCode(orderLineItemsDto.getSkuCode())
                    .quantity(orderLineItemsDto.getQuantity())
                    .price(orderLineItemsDto.getPrice())
                    .build()).toList();

        order.setOrderLineItemsList(orderLineItemsList);

        orderRepository.save(order);
    }
}
