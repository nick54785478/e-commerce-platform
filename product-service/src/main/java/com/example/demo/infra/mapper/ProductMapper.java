package com.example.demo.infra.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.demo.application.command.product.CreateProductCommand;
import com.omni.product.api.dto.ProductQueriedView;
import com.example.demo.iface.dto.req.CreateProductResource;
import com.example.demo.infra.projection.product.entity.ProductView;

/**
 * ProductMapper - 商品模組的物件轉換映射器
 * <p>
 * 負責不同層級之間的資料轉換。在 CQRS 架構中，通常負責外部 Resource (DTO) 與 Command，
 * 以及 Projection (View) 與 DTO 之間的映射。
 * </p>
 */
@Mapper(componentModel = "spring")
public interface ProductMapper {

	// ##### 查詢端 (Query Side) #####

	/**
	 * 將資料庫查詢出來的 Projection View 轉換為對外輸出的 DTO
	 * 
	 * @param view 商品讀取模型
	 * @return 供前端顯示用的 DTO
	 */
	ProductQueriedView toQueriedView(ProductView view);

	/**
	 * 將多筆實體轉換為 DTO 列表
	 */
	List<ProductQueriedView> transformProjectionList(List<ProductView> views);

	// ##### 命令端 (Command Side) #####

	/**
	 * 防腐層轉換 (ACL) - 將前端傳來的請求 Resource 轉換為內部業務的 Command
	 * 
	 * @param resource 建立商品請求資料載體
	 * @return 建立商品命令
	 */
	@Mapping(target = "productId", ignore = true) // ID 會在 Command 發布前由 Aggregate 產生
	@Mapping(target = "tenantId", ignore = true) // tenantId 會由 Controller 注入
	CreateProductCommand toCommand(CreateProductResource resource);
}
