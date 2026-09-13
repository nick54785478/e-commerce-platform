package com.example.demo.iface.dto.res;

import com.omni.order.api.dto.OrderQueriedView;

public record OrderQueriedResource(String code, String message, OrderQueriedView data) {
}
