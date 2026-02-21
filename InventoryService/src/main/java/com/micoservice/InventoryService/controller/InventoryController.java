package com.micoservice.InventoryService.controller;
import com.micoservice.InventoryService.dto.ProductRequest;
import com.micoservice.InventoryService.dto.ReserveRequest;
import com.micoservice.InventoryService.model.Product;
import com.micoservice.InventoryService.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {
    @Autowired
    private  InventoryService inventoryService;

    @GetMapping("/{sku-code}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public boolean isInStock(@PathVariable("sku-code") String skuCode, @RequestParam Integer quantity) {
        return inventoryService.isInStock(skuCode, quantity);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public Product addProduct(@Valid @RequestBody ProductRequest request) {
        return inventoryService.addProduct(request);
    }

    @PostMapping("/reserve")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public void reserve(@Valid @RequestBody ReserveRequest request) {
        inventoryService.reserveStock(request);
    }
}
