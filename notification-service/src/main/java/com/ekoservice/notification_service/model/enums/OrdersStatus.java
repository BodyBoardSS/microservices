package com.ekoservice.notification_service.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum OrdersStatus {
    PLACED,
    SHIPPED,
    DELIVERED,
    CANCELLED;

    @JsonCreator
    public static OrdersStatus fromValue(String value) {
        return OrdersStatus.valueOf(value.toUpperCase());
    }

    @JsonValue
    public String toValue() {
        return this.name().toLowerCase();
    }
}