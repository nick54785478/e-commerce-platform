package com.example.demo.infra.projection.favorite.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.infra.projection.favorite.entity.UserFavoriteView;

@Repository
public interface UserFavoriteViewRepository extends JpaRepository<UserFavoriteView, String> {
    
    List<UserFavoriteView> findByTenantIdAndUserId(String tenantId, String userId);

    Optional<UserFavoriteView> findByTenantIdAndUserIdAndProductId(String tenantId, String userId, String productId);
}
