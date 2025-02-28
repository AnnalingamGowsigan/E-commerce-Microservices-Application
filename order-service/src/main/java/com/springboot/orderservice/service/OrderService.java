package com.springboot.orderservice.service;


import com.springboot.orderservice.dto.InventoryResponse;
import com.springboot.orderservice.event.OrderPlacedEvent;
import com.springboot.orderservice.model.OrderLineItems;
import com.springboot.orderservice.dto.OrderRequest;
import com.springboot.orderservice.model.Order;
import com.springboot.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OrderService {


    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;
    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    public String placeOrder(OrderRequest orderRequest) throws IllegalAccessException {
        log.info("inside placeOrder method");
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

        log.info("Fetching inventory information for the products: {}" + skuCodes);
        //call the inventory service and place the order if product is in stock
        InventoryResponse[] resultArray  = webClientBuilder.build().get()
                            .uri("http://inventory-service:8082/api/inventory",
                                    uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
                            .retrieve()
                            .bodyToMono(InventoryResponse[].class)
                            .block(); //for made it synchronous

        Boolean result = Arrays.stream(resultArray).allMatch(InventoryResponse->InventoryResponse.isInStock());

        log.info("result: {}"  + result);
        if(Boolean.TRUE.equals(result)){
            orderRepository.save(order);
            // publish Order Placed Event
            kafkaTemplate.send("notificationTopic", new OrderPlacedEvent(order.getOrderNumber()));
            return "Order placed successfully";
        }else {
            throw new IllegalArgumentException("Product is out of stock. please try again later.");
        }
    }
}
