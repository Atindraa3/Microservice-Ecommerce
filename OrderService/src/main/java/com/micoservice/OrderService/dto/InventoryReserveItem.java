package com.micoservice.OrderService.dto;

public class InventoryReserveItem {

    private String skuCode;
    private Integer quantity;

    public InventoryReserveItem() {
    }

    public InventoryReserveItem(String skuCode, Integer quantity) {
        this.skuCode = skuCode;
        this.quantity = quantity;
    }

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
