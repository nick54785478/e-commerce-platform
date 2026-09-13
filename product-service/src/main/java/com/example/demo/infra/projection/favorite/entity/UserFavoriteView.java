package com.example.demo.infra.projection.favorite.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "user_favorite_view", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"tenant_id", "user_id", "product_id"})
})
public class UserFavoriteView {

    @Id
    @Column(length = 64)
    private String favoriteId;

    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "product_id", nullable = false)
    private String productId;

    private LocalDateTime createdAt;
}
