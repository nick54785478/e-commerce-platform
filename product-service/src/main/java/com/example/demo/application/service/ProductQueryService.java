package com.example.demo.application.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.stereotype.Service;

import com.example.demo.application.domain.product.query.FindAllProductsQuery;
import com.example.demo.application.domain.product.query.FindProductsPagedQuery;
import com.example.demo.application.domain.product.query.GetProductQuery;
import com.omni.product.api.dto.ProductPageQueriedView;
import com.omni.product.api.dto.ProductQueriedView;

import lombok.RequiredArgsConstructor;

import com.example.demo.application.port.in.FindAllProductsUseCase;
import com.example.demo.application.port.in.FindProductByIdUseCase;
import com.example.demo.application.port.in.FindProductsPagedUseCase;

/**
 * ProductQueryService - 商品查詢應用服務
 *
 * <p>
 * 屬於 Application Layer，作為查詢端的門面 (Facade)。
 * </p>
 * <p>
 * 負責將表現層的查詢請求轉發至 {@link org.axonframework.queryhandling.QueryBus}，
 * 並將查詢結果封裝為 DTO 回傳給 Interface Layer。
 * </p>
 *
 * <h3>設計特色：</h3>
 * <ul>
 * <li><b>透過 QueryGateway：</b> 隱藏了底層資料庫讀取實作，QueryHandler 可能存在於任何微服務中。</li>
 * <li><b>非同步回傳：</b> 所有方法皆回傳 {@link CompletableFuture}，支援高併發與非阻塞架構。</li>
 * <li><b>型別安全：</b> 使用 Axon 的 ResponseTypes 來保證回傳的 DTO 型別一致。</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
class ProductQueryService implements FindAllProductsUseCase, FindProductByIdUseCase, FindProductsPagedUseCase {

	/**
	 * Axon 的查詢通訊閘道，負責將查詢路由至正確的處理實體
	 */
	private final QueryGateway queryGateway;

	/**
	 * 查詢系統中所有商品的摘要
	 *
	 * <p>
	 * 派發 {@link FindAllProductsQuery}，並預期回傳多筆 DTO。
	 * </p>
	 *
	 * @return 包含多筆 {@link ProductQueriedView} 的非同步結果
	 */
	@Override
	public CompletableFuture<List<ProductQueriedView>> findAll(String tenantId) {
		// 使用 multipleInstancesOf 告訴 Axon 預期回傳的是一個集合
		return queryGateway.query(new FindAllProductsQuery(tenantId),
				ResponseTypes.multipleInstancesOf(ProductQueriedView.class));
	}

	/**
	 * 根據商品 ID 獲取單一商品詳細資料
	 *
	 * <p>
	 * 用於商品詳細頁面或業務邏輯檢查。
	 * </p>
	 *
	 * @param productId 商品唯一識別碼
	 * @return 包含單筆 {@link ProductQueriedView} 的非同步結果，若找不到會回傳 null
	 */
	@Override
	public CompletableFuture<ProductQueriedView> findById(String tenantId, String productId) {
		// 使用 instanceOf 預期回傳單一物件
		return queryGateway.query(new GetProductQuery(tenantId, productId), ResponseTypes.instanceOf(ProductQueriedView.class));
	}

	/**
	 * 分頁模糊查詢商品
	 *
	 * <p>
	 * 派發 {@link FindProductsPagedQuery} 進行模糊搜尋。為了避免 Axon 在跨節點序列化時
	 * 遇到泛型擦除問題，這裡特別定義了 {@link ProductPageQueriedView} 包裝分頁資訊。
	 * </p>
	 *
	 * @param name 商品名稱關鍵字，若為空則查詢所有
	 * @param type 商品種類，若為空則查詢所有
	 * @param subType 商品次要種類，若為空則查詢所有
	 * @param status 商品狀態 (ACTIVE, INACTIVE)，若為空則查詢所有
	 * @param page 頁碼 (從 0 開始)
	 * @param size 每頁顯示筆數
	 * @return 包含分頁資訊與資料列的非同步結果
	 */
	@Override
	public CompletableFuture<ProductPageQueriedView> findPaged(String tenantId, String name, String type, String subType, String status, int page, int size) {
		return queryGateway.query(new FindProductsPagedQuery(tenantId, name, type, subType, status, page, size),
				ResponseTypes.instanceOf(ProductPageQueriedView.class));
	}
}

