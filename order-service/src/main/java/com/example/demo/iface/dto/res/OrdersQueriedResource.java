package com.example.demo.iface.dto.res;
import java.util.List;
import com.example.demo.application.domain.order.aggregate.Order;
import com.omni.order.api.dto.OrderQueriedView;

public record OrdersQueriedResource(String code, String message, List<OrderQueriedView> data) {}
