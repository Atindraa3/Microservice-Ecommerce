package com.micoservice.InventoryService.service;
import com.micoservice.InventoryService.dto.ProductRequest;
import com.micoservice.InventoryService.dto.ReserveItem;
import com.micoservice.InventoryService.dto.ReserveRequest;
import com.micoservice.InventoryService.exception.OutOfStockException;
import com.micoservice.InventoryService.model.Product;
import com.micoservice.InventoryService.repo.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InventoryService {
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public boolean isInStock(String skuCode, Integer quantity) {
        return productRepository.findBySkuCode(skuCode)
                .map(product -> product.getQuantity() >= quantity)
                .orElse(false);
    }

    @Transactional
    public Product addProduct(ProductRequest request) {
        Product product = new Product();
        product.setSkuCode(request.getSkuCode());
        product.setQuantity(request.getQuantity());
        return productRepository.save(product);
    }

    @Transactional
    public void reserveStock(ReserveRequest request) {
        for (ReserveItem item : request.getItems()) {
            int updated = productRepository.reserveStock(item.getSkuCode(), item.getQuantity());
            if (updated == 0) {
                throw new OutOfStockException("Insufficient stock for sku " + item.getSkuCode());
            }
        }
    }
}
