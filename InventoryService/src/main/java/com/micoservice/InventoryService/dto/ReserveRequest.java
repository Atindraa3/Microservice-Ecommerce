package com.micoservice.InventoryService.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public class ReserveRequest {

    @NotEmpty
    private List<ReserveItem> items;

    public List<ReserveItem> getItems() {
        return items;
    }

    public void setItems(List<ReserveItem> items) {
        this.items = items;
    }
}
