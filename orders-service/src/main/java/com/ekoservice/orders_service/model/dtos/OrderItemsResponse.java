package com.ekoservice.orders_service.model.dtos;

public record OrderItemsResponse(Long id, String sku, Double price, Long quantity) {
}
