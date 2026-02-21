package com.micoservice.OrderService.dto;

import java.util.List;

public class InventoryReserveRequest {

    private List<InventoryReserveItem> items;

    public InventoryReserveRequest() {
    }

    public InventoryReserveRequest(List<InventoryReserveItem> items) {
        this.items = items;
    }

    public List<InventoryReserveItem> getItems() {
        return items;
    }

    public void setItems(List<InventoryReserveItem> items) {
        this.items = items;
    }
}
