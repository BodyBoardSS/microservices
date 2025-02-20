package com.ekoservice.orders_service.services;

import com.ekoservice.orders_service.events.OrderEvent;
import com.ekoservice.orders_service.model.dtos.*;
import com.ekoservice.orders_service.model.entities.Order;
import com.ekoservice.orders_service.model.entities.OrderItems;
import com.ekoservice.orders_service.model.enums.OrdersStatus;
import com.ekoservice.orders_service.repositories.OrderRepository;
import com.ekoservice.orders_service.utils.JsonUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public OrderResponse placeOrder(OrderRequest orderRequest){

        //Check for inventory
        BaseResponse result = this.webClientBuilder.build()
                .post()
                .uri("lb://inventory-service/api/inventory/in-stock")
                .bodyValue(orderRequest.getOrderItems())
                .retrieve()
                .bodyToMono(BaseResponse.class)
                .block();
        if(result != null && !result.hasErrors()){
            Order order = new Order();
            order.setOrderNumber(UUID.randomUUID().toString());
            order.setOrderItems(orderRequest.getOrderItems().stream()
                    .map(orderItemRequest -> mapOrderItemRequestToOrderItem(orderItemRequest, order))
                    .toList());

            var savedOrder = this.orderRepository.save(order);

            System.out.println(JsonUtils.toJson(
                    new OrderEvent(savedOrder.getOrderNumber(), savedOrder.getOrderItems().size(), OrdersStatus.PLACED)
            ));

            //TODO: Send message to order topic
            this.kafkaTemplate.send("orders-topic", JsonUtils.toJson(
                    new OrderEvent(savedOrder.getOrderNumber(), savedOrder.getOrderItems().size(), OrdersStatus.PLACED)
            ));

            return mapOrderResponse(savedOrder);
        } else
            throw new IllegalArgumentException("Some of products are no in stock");
    }

    public List<OrderResponse> getAllOrders(){
        List<Order> orders = this.orderRepository.findAll();

        return orders.stream().map(this::mapOrderResponse).toList();
    }

    private OrderResponse mapOrderResponse(Order order) {
        return new OrderResponse(order.getId(), order.getOrderNumber(),
                order.getOrderItems().stream().map(this::mapToOrderItemRequest).toList());
    }

    private OrderItemsResponse mapToOrderItemRequest(OrderItems orderItems) {
        return new OrderItemsResponse(orderItems.getId(), orderItems.getSku(), orderItems.getPrice(), orderItems.getQuantity());
    }

    private OrderItems mapOrderItemRequestToOrderItem(OrderItemRequest orderItemRequest, Order order) {
        return OrderItems.builder()
                .id(orderItemRequest.getId())
                .sku(orderItemRequest.getSku())
                .price(orderItemRequest.getPrice())
                .quantity(orderItemRequest.getQuantity())
                .order(order)
                .build();
    }
}
