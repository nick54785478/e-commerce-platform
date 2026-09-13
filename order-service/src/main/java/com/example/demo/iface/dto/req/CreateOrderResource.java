package com.example.demo.iface.dto.req;
import java.util.List;
public record CreateOrderResource(List<CreateOrderItemResource> items) {}
