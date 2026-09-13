package com.example.demo.infra.projection.product.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.infra.projection.product.entity.ProductView;
import com.omni.product.api.enums.ProductStatus;

/**
 * ProductViewRepository - 商品查詢視圖資料庫操作介面
 * <p>
 * 使用 Spring Data JPA 管理查詢端的 Read Model (ProductView)。
 * </p>
 */
public interface ProductViewRepository extends JpaRepository<ProductView, String> {

	java.util.Optional<ProductView> findByTenantIdAndProductId(String tenantId, String productId);

	/**
	 * 根據名稱進行模糊查詢
	 */
	Page<ProductView> findByTenantIdAndNameContainingIgnoreCase(String tenantId, String name, Pageable pageable);

	/**
	 * 根據名稱與種類進行查詢
	 */
	Page<ProductView> findByTenantIdAndNameContainingIgnoreCaseAndType(String tenantId, String name, String type, Pageable pageable);

	/**
	 * 根據種類進行查詢
	 */
	Page<ProductView> findByTenantIdAndType(String tenantId, String type, Pageable pageable);

	/**
	 * 查詢分頁的所有商品
	 */
	Page<ProductView> findAllByTenantId(String tenantId, Pageable pageable);

	/**
	 * 靈活查詢，支援名稱模糊搜尋、種類過濾、狀態過濾
	 */
	@Query("SELECT p FROM ProductView p WHERE p.tenantId = :tenantId " +
	       "AND (:name = '' OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
	       "AND (:type = '' OR p.type = :type) " +
	       "AND (:subType = '' OR p.subType = :subType) " +
	       "AND (:status IS NULL OR p.status = :status)")
	Page<ProductView> findByFilters(@Param("tenantId") String tenantId, 
	                                @Param("name") String name, 
	                                @Param("type") String type, 
	                                @Param("subType") String subType, 
	                                @Param("status") ProductStatus status, 
	                                Pageable pageable);
}
