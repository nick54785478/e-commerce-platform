package com.example.demo.infra.processor.product;

import java.util.List;

import org.axonframework.queryhandling.QueryHandler;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.example.demo.application.domain.product.query.FindAllProductsQuery;
import com.example.demo.application.domain.product.query.FindProductsPagedQuery;
import com.example.demo.application.domain.product.query.GetProductQuery;
import com.omni.product.api.dto.ProductPageQueriedView;
import com.omni.product.api.dto.ProductQueriedView;
import com.example.demo.infra.mapper.ProductMapper;
import com.example.demo.infra.projection.product.repository.ProductViewRepository;
import com.example.demo.infra.projection.product.entity.ProductView;
import com.omni.product.api.enums.ProductStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

/**
 * ProductQueryHandler - 商品查詢處理器
 *
 * <p>
 * 在 CQRS 架構中扮演查詢端的接口（Query Adapter）。負責接收 Query Bus 上路由來的查詢請求，
 * 串接 {@link ProductViewRepository} 進行資料檢索，並將實體轉換為 DTO 輸出。
 * </p>
 *
 * <h3>設計特徵：</h3>
 * <ul>
 * <li><b>讀寫分離：</b> 負責查詢 Projection Model (ProductView)，與寫入端的 Aggregate 徹底隔離。</li>
 * <li><b>分頁效能：</b> 依賴 Spring Data JPA 進行資料庫層級的分頁，避免記憶體溢出。</li>
 * <li><b>非同步化：</b> 透過 Axon Query Bus，實作與 API 呼叫端解耦。</li>
 * </ul>
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductQueryHandler {

	private final ProductViewRepository repository;
	private final ProductMapper mapper;

	/**
	 * 查詢系統中的所有商品
	 *
	 * @param query 查詢指令 (FindAllProductsQuery)
	 * @return 轉換後的商品 DTO 清單
	 */
	@QueryHandler
	public List<ProductQueriedView> handle(FindAllProductsQuery query) {
		log.info("[Query] 接收到 FindAllProductsQuery，查詢所有商品");
		return repository.findAll().stream().filter(p -> query.tenantId().equals(p.getTenantId())).map(mapper::toQueriedView).toList();
	}

	/**
	 * 根據商品 ID 查詢詳細資訊
	 *
	 * @param query 包含商品 ID 的查詢物件(GetProductQuery)
	 * @return 單一商品 DTO；若找不到會回傳 null (Axon 會自動包裝處理)
	 */
	@QueryHandler
	public ProductQueriedView handle(GetProductQuery query) {
		log.info("[Query] 接收到 GetProductQuery，查詢目標ID: {}", query.productId());
		return repository.findByTenantIdAndProductId(query.tenantId(), query.productId()).map(mapper::toQueriedView).orElse(null);
	}

	/**
	 * 根據名稱模糊查詢商品
	 *
	 * @param query 包含過濾條件的查詢物件(FindProductsPagedQuery)
	 * @return 封裝了分頁資訊的商品 DTO (ProductPageQueriedView)
	 */
	@QueryHandler
	public ProductPageQueriedView handle(FindProductsPagedQuery query) {
		log.info("[Query] 接收到分頁查詢請求，名稱={}, 種類={}, 次種類={}, 狀態={}, 頁碼={}, 每頁筆數={}", query.name(), query.type(), query.subType(), query.status(), query.page(), query.size());

		Pageable pageable = PageRequest.of(query.page(), query.size());
		
		ProductStatus statusEnum = null;
		if (query.status() != null && !query.status().isBlank()) {
			try {
				statusEnum = ProductStatus.valueOf(query.status());
			} catch (IllegalArgumentException e) {
				log.warn("無法解析商品狀態: {}", query.status());
			}
		}
		
		String nameFilter = (query.name() != null && !query.name().isBlank()) ? query.name() : "";
		String typeFilter = (query.type() != null && !query.type().isBlank()) ? query.type() : "";
		String subTypeFilter = (query.subType() != null && !query.subType().isBlank()) ? query.subType() : "";

		Page<ProductView> resultPage = repository.findByFilters(
			query.tenantId(), 
			nameFilter, 
			typeFilter, 
			subTypeFilter,
			statusEnum, 
			pageable
		);

		List<ProductQueriedView> content = resultPage.getContent().stream().map(mapper::toQueriedView).toList();

		log.debug("[Query] 分頁查詢完成，總筆數: {}, 總頁數: {}", resultPage.getTotalElements(), resultPage.getTotalPages());

		return new ProductPageQueriedView(content, resultPage.getTotalElements(), resultPage.getTotalPages(),
				resultPage.getNumber());
	}
}

