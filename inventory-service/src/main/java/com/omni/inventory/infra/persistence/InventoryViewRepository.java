package com.omni.inventory.infra.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.omni.inventory.infra.projection.InventoryView;

@Repository
public interface InventoryViewRepository extends JpaRepository<InventoryView, String> {
}
