package com.springboot.orderservice.service;


import com.springboot.orderservice.dto.InventoryResponse;
import com.springboot.orderservice.dto.OrderLineItemsDto;
import com.springboot.orderservice.model.OrderLineItems;
import com.springboot.orderservice.dto.OrderRequest;
import com.springboot.orderservice.model.Order;
import com.springboot.orderservice.repository.OrderRepository;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;

    public String placeOrder(OrderRequest orderRequest) throws IllegalAccessException {
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

        List<String> skuCodes = order.getOrderLineItemsList().stream().map(OrderLineItems::getSkuCode).toList();

        //call the inventory service and place the order if product is in stock
        InventoryResponse[] resultArray  = webClientBuilder.build().get()
                            .uri("http://inventory-service:8082/api/inventory",
                                    uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
                            .retrieve()
                            .bodyToMono(InventoryResponse[].class)
                            .block(); //for made it synchronous

        Boolean result = Arrays.stream(resultArray).allMatch(InventoryResponse->InventoryResponse.isInStock());
        if(Boolean.TRUE.equals(result)){
            orderRepository.save(order);
            return "Order placed successfully";
        }else {
            throw new IllegalArgumentException("Product is out of stock. please try again later.");
        }
    }
}
