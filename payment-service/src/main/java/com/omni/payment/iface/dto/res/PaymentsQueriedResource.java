package com.omni.payment.iface.dto.res;
import java.util.List;
import com.omni.payment.application.domain.payment.aggregate.Payment;
import com.omni.payment.api.dto.PaymentQueriedView;

public record PaymentsQueriedResource(String code, String message, List<PaymentQueriedView> data) {}
