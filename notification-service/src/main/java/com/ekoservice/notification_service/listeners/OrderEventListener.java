package com.ekoservice.notification_service.listeners;

import com.ekoservice.notification_service.events.OrderEvent;
import com.ekoservice.notification_service.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OrderEventListener {

    @KafkaListener (topics = "orders-topic")
    public void handleOrdersNotifications(String message){
        try {
            var orderEvent = JsonUtils.fromJson(message, OrderEvent.class);
            //Send email to customer, send SMS to customer, etc
            log.info("Order {} event received for order: {} with {} items", orderEvent.ordersStatus(), orderEvent.orderNumber(), orderEvent.itemsCount());
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }
}
