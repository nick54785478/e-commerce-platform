package com.example.demo.infra.projection.product.entity;

import java.math.BigDecimal;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.omni.product.api.enums.ProductStatus;

/**
 * ProductView - 商品查詢視圖 (Read Model)
 *
 * <p>
 * 此實體類別專門為查詢端 (Query Side) 提供優化過的資料庫映射結構。
 * 它是對 `Product` 聚合根在特定時間點的投影 (Projection) 結果。
 * </p>
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "product_view")
public class ProductView {

	@Id
	@Column(length = 64)
	private String productId;

	@Column(name = "tenant_id")
	private String tenantId;

	private String name;

	@Column(columnDefinition = "TEXT")
	private String description;

	@Column(length = 100)
	private String type;

	@Column(length = 100)
	private String subType;

	private BigDecimal price;

	private Integer stock;

	@Enumerated(EnumType.STRING)
	@Column(length = 20)
	private ProductStatus status;

	@ElementCollection
	@CollectionTable(name = "product_images", joinColumns = @JoinColumn(name = "product_id"))
	@Column(name = "image_url")
	private List<String> imageUrls;

	@ElementCollection
	@CollectionTable(name = "product_tags", joinColumns = @JoinColumn(name = "product_id"))
	@Column(name = "tag")
	private List<String> tags;

	private Long version;

	// ##### 狀態變更方法 (State Mutators) #####

	public void markCreated(String name, String description, String type, String subType, BigDecimal price, Long version) {
		this.name = name;
		this.description = description;
		this.type = type;
		this.subType = subType;
		this.price = price;
		this.stock = 0;
		this.version = version;
	}

	public void updateInfo(String name, String description, String type, String subType, BigDecimal price, Long version) {
		this.name = name;
		this.description = description;
		this.type = type;
		this.subType = subType;
		this.price = price;
		this.version = version;
	}

	public void reduceStock(Integer quantity, Long version) {
		this.stock = (this.stock != null) ? Math.max(0, this.stock - quantity) : 0;
		this.version = version;
	}

	public void addStock(Integer quantity, Long version) {
		this.stock = (this.stock != null) ? this.stock + quantity : quantity;
		this.version = version;
	}

	// ##### 業務邏輯判斷 (Business Predicates) #####

	public boolean isAvailable() {
		return this.status == ProductStatus.ACTIVE && this.stock != null && this.stock > 0;
	}
}
