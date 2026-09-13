package com.omni.payment.infra.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.omni.payment.infra.projection.payment.PaymentView;

@Repository
public interface PaymentViewRepository extends JpaRepository<PaymentView, String> {
	List<PaymentView> findByOrderId(String orderId);
}
