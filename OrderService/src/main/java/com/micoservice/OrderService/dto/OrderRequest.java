package com.micoservice.OrderService.dto;


import java.util.List;

public class OrderRequest {
    private List<OrderLineItemsDto> orderLineItemsDtoList;

    public OrderRequest(List<OrderLineItemsDto> orderLineItemsDtoList) {
        this.orderLineItemsDtoList = orderLineItemsDtoList;
    }

    public OrderRequest() {
    }

    public List<OrderLineItemsDto> getOrderLineItemsDtoList() {
        return this.orderLineItemsDtoList;
    }

    public void setOrderLineItemsDtoList(List<OrderLineItemsDto> orderLineItemsDtoList) {
        this.orderLineItemsDtoList = orderLineItemsDtoList;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof OrderRequest)) return false;
        final OrderRequest other = (OrderRequest) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$orderLineItemsDtoList = this.getOrderLineItemsDtoList();
        final Object other$orderLineItemsDtoList = other.getOrderLineItemsDtoList();
        if (this$orderLineItemsDtoList == null ? other$orderLineItemsDtoList != null : !this$orderLineItemsDtoList.equals(other$orderLineItemsDtoList))
            return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof OrderRequest;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $orderLineItemsDtoList = this.getOrderLineItemsDtoList();
        result = result * PRIME + ($orderLineItemsDtoList == null ? 43 : $orderLineItemsDtoList.hashCode());
        return result;
    }

    public String toString() {
        return "OrderRequest(orderLineItemsDtoList=" + this.getOrderLineItemsDtoList() + ")";
    }
}