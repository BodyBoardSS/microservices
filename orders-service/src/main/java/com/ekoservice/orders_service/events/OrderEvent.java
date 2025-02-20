package com.ekoservice.orders_service.events;

import com.ekoservice.orders_service.model.enums.OrdersStatus;

public record OrderEvent (String orderNumber, int itemsCount, OrdersStatus ordersStatus){
}
