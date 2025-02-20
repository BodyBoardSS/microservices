package com.ekoservice.notification_service.events;

import com.ekoservice.notification_service.model.enums.OrdersStatus;

public record OrderEvent(String orderNumber, int itemsCount, OrdersStatus ordersStatus) {}