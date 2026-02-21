package com.micoservice.OrderService.service;


import com.micoservice.OrderService.client.InventoryClient;
import com.micoservice.OrderService.dto.InventoryReserveItem;
import com.micoservice.OrderService.dto.InventoryReserveRequest;
import com.micoservice.OrderService.dto.OrderLineItemsDto;
import com.micoservice.OrderService.dto.OrderRequest;
import com.micoservice.OrderService.model.Order;
import com.micoservice.OrderService.model.OrderLineItems;
import com.micoservice.OrderService.repo.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final InventoryClient inventoryClient;

    public void placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList()
                .stream()
                .map(this::mapToDto)
                .toList();

        order.setOrderLineItemsList(orderLineItems);

        InventoryReserveRequest reserveRequest = new InventoryReserveRequest(
                order.getOrderLineItemsList().stream()
                        .map(item -> new InventoryReserveItem(item.getSkuCode(), item.getQuantity()))
                        .toList()
        );

        try {
            inventoryClient.reserve(reserveRequest);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Product is not in stock, please try again later");
        }

        orderRepository.save(order);
    }

    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
        return orderLineItems;
    }
}
