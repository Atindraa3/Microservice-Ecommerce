package com.micoservice.InventoryService.repo;

import com.micoservice.InventoryService.model.Product;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findBySkuCode(String skuCode);

    @Modifying
    @Query("UPDATE Product p SET p.quantity = p.quantity - :quantity WHERE p.skuCode = :skuCode AND p.quantity >= :quantity")
    int reserveStock(@Param("skuCode") String skuCode, @Param("quantity") int quantity);
}
