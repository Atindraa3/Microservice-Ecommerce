package com.micoservice.OrderService.client;

import com.micoservice.OrderService.config.FeignConfig;
import com.micoservice.OrderService.dto.InventoryReserveRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "inventory-service", configuration = FeignConfig.class)
public interface InventoryClient {
    @GetMapping("/api/inventory/{sku-code}")
    boolean checkStock(@PathVariable("sku-code") String skuCode , @RequestParam int quantity);

    @PostMapping("/api/inventory/reserve")
    void reserve(@RequestBody InventoryReserveRequest request);
}
