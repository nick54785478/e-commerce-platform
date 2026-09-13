package com.omni.payment.iface.dto.req;
import java.math.BigDecimal;
public record ProcessPaymentResource(String orderId, BigDecimal amount) {}
