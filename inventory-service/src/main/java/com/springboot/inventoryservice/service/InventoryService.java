package com.springboot.inventoryservice.service;

import com.springboot.inventoryservice.dto.InventoryResponse;
import com.springboot.inventoryservice.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    @Transactional(readOnly = true)
    public List<InventoryResponse> isInStock(List<String> skuCode) {
//        log.info("wait started");
//        try {
//            Thread.sleep(10000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        log.info("wait ended");

        return inventoryRepository.findBySkuCodeIn(skuCode).stream().map(inventory -> InventoryResponse.builder()
                .skuCode(inventory.getSkuCode())
                .isInStock(inventory.getQuantity() > 0)
                .build()).toList();
    }
}
