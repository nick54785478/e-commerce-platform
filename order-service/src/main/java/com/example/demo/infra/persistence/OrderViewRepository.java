package com.example.demo.infra.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.infra.projection.order.OrderView;

@Repository
public interface OrderViewRepository extends JpaRepository<OrderView, String> {
}
