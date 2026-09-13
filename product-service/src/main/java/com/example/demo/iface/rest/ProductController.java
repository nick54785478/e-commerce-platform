package com.example.demo.iface.rest;

import java.util.concurrent.CompletableFuture;

import com.example.demo.application.port.in.CreateProductUseCase;
import com.example.demo.application.port.in.UpdateProductUseCase;
import com.example.demo.application.port.in.ChangeProductStatusUseCase;
import com.example.demo.application.port.in.AdjustStockUseCase;
import com.example.demo.application.port.in.FindAllProductsUseCase;
import com.example.demo.application.port.in.FindProductByIdUseCase;
import com.example.demo.application.port.in.FindProductsPagedUseCase;
import com.omni.inventory.api.command.AdjustStockCommand;
import org.axonframework.modelling.command.ConcurrencyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.application.command.product.CreateProductCommand;
import com.example.demo.application.command.product.UpdateProductCommand;
import com.example.demo.application.command.product.ChangeProductStatusCommand;
import com.example.demo.application.port.out.ProductImageStoragePort;
import com.example.demo.application.port.out.BehaviorServicePort;
import com.example.demo.iface.dto.req.CreateProductResource;
import com.example.demo.iface.dto.req.UpdateProductResource;
import com.example.demo.iface.dto.req.ChangeProductStatusResource;
import com.example.demo.iface.dto.res.ProductCreatedResource;
import com.example.demo.iface.dto.res.ProductListQueriedResource;
import com.omni.product.api.dto.ProductPageQueriedView;
import com.omni.product.api.dto.ProductQueriedView;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * ProductController - 商品管理 API
 */
@Slf4j
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

	private final CreateProductUseCase createProductUseCase;
	private final UpdateProductUseCase updateProductUseCase;
	private final ChangeProductStatusUseCase changeProductStatusUseCase;
	private final AdjustStockUseCase adjustStockUseCase;
	private final FindAllProductsUseCase findAllProductsUseCase;
	private final FindProductByIdUseCase findProductByIdUseCase;
	private final FindProductsPagedUseCase findProductsPagedUseCase;
	private final ProductImageStoragePort productImageStoragePort;
	private final BehaviorServicePort behaviorPort;

	/**
	 * API: 增加庫存
	 * 將 AddStockCommand 拋向 inventory-service 處理
	 */
	@PostMapping("/{productId}/stock/add")
	public CompletableFuture<ResponseEntity<ProductCreatedResource>> addStock(@PathVariable String productId,
			@RequestParam Integer quantity) {
		
		AdjustStockCommand command = new AdjustStockCommand(
				"INV-" + productId, 
				quantity,
				"MANUAL_ADD"
		);
		
		return adjustStockUseCase.adjustStock(command)
				.thenApply(result -> ResponseEntity.ok(new ProductCreatedResource("200", "庫存增加成功", productId)))
				.exceptionally(ex -> ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body(new ProductCreatedResource("400", "庫存增加失敗: " + ex.getCause().getMessage(), null)));
	}

	/**
	 * API: 減少庫存
	 * 將 ReduceStockCommand 拋向 inventory-service 處理
	 */
	@PostMapping("/{productId}/stock/reduce")
	public CompletableFuture<ResponseEntity<ProductCreatedResource>> reduceStock(@PathVariable String productId,
			@RequestParam Integer quantity) {
		
		AdjustStockCommand command = new AdjustStockCommand(
				"INV-" + productId, 
				-Math.abs(quantity), // ensure negative
				"MANUAL_REDUCE"
		);
		
		return adjustStockUseCase.adjustStock(command)
				.thenApply(result -> ResponseEntity.ok(new ProductCreatedResource("200", "庫存減少成功", productId)))
				.exceptionally(ex -> ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body(new ProductCreatedResource("400", "庫存減少失敗: " + ex.getCause().getMessage(), null)));
	}

	/**
	 * API: 建立商品
	 */
	@PostMapping("")
	public CompletableFuture<ResponseEntity<ProductCreatedResource>> createProduct(
			@RequestHeader("X-Tenant-ID") String tenantId, 
			@RequestBody CreateProductResource resource) throws Exception {
		// MapStruct mapping bypassed to inject tenantId manually
		CreateProductCommand command = new CreateProductCommand(
				java.util.UUID.randomUUID().toString(), 
				tenantId, 
				resource.name(),
				resource.description(),
				resource.type(),
				resource.subType(),
				resource.price(),
				resource.imageUrls(),
				resource.tags()
		);
		String productId = command.productId();
		
		// 透過 CommandGateway 派發，Axon 會自動路由給 ProductCommandService 處理
		return createProductUseCase.create(command)
				.thenApply(result -> ResponseEntity.status(HttpStatus.CREATED)
						.body(new ProductCreatedResource("200", "商品建立成功", productId)))
				.exceptionally(ex -> ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body(new ProductCreatedResource("400", "建立失敗: " + ex.getCause().getMessage(), null)));
	}

	/**
	 * API: 更新商品資訊
	 */
	@PutMapping("/{productId}")
	public CompletableFuture<ResponseEntity<ProductCreatedResource>> updateProduct(@PathVariable String productId,
			@RequestHeader("X-Tenant-ID") String tenantId,
			@RequestBody UpdateProductResource resource) {

		UpdateProductCommand command = new UpdateProductCommand(
				productId, 
				tenantId, 
				resource.version(), 
				resource.name(),
				resource.description(),
				resource.type(),
				resource.subType(),
				resource.price(), 
				resource.imageUrls(),
				resource.tags()
		);

		return updateProductUseCase.update(command)
				.thenApply(result -> ResponseEntity.ok().body(new ProductCreatedResource("200", "商品更新成功", productId)))
				.exceptionally(ex -> {
					Throwable cause = (ex.getCause() != null) ? ex.getCause() : ex;
					log.error("[API] 更新失敗: {}", cause.getMessage());

					if (cause instanceof ConcurrencyException) {
						return ResponseEntity.status(HttpStatus.CONFLICT)
								.body(new ProductCreatedResource("409", "更新衝突：該資源已被其他人修改，請重新整理", productId));
					}

					return ResponseEntity.status(HttpStatus.BAD_REQUEST)
							.body(new ProductCreatedResource("400", "更新失敗: " + cause.getMessage(), productId));
				});
	}

	/**
	 * API: 變更商品狀態 (上下架)
	 */
	@PutMapping("/{productId}/status")
	public CompletableFuture<ResponseEntity<ProductCreatedResource>> changeStatus(@PathVariable String productId,
			@RequestHeader("X-Tenant-ID") String tenantId,
			@RequestBody ChangeProductStatusResource resource) {

		ChangeProductStatusCommand command = new ChangeProductStatusCommand(
				productId,
				tenantId,
				resource.version(),
				resource.status()
		);

		return changeProductStatusUseCase.changeStatus(command)
				.thenApply(result -> ResponseEntity.ok().body(new ProductCreatedResource("200", "狀態變更成功", productId)))
				.exceptionally(ex -> {
					Throwable cause = (ex.getCause() != null) ? ex.getCause() : ex;
					log.error("[API] 狀態變更失敗: {}", cause.getMessage());

					if (cause instanceof ConcurrencyException) {
						return ResponseEntity.status(HttpStatus.CONFLICT)
								.body(new ProductCreatedResource("409", "狀態變更衝突：該資源已被其他人修改，請重新整理", productId));
					}

					return ResponseEntity.status(HttpStatus.BAD_REQUEST)
							.body(new ProductCreatedResource("400", "狀態變更失敗: " + cause.getMessage(), productId));
				});
	}

	/**
	 * API: 查詢所有商品清單
	 */
	@GetMapping("")
	public CompletableFuture<ResponseEntity<ProductListQueriedResource>> listAll(@RequestHeader("X-Tenant-ID") String tenantId) {
		return findAllProductsUseCase.findAll(tenantId)
				.thenApply(result -> ResponseEntity.ok().body(new ProductListQueriedResource("200", "查詢成功", result)));
	}

	/**
	 * API: 查詢商品詳情
	 */
	@GetMapping("/{productId}")
	public CompletableFuture<ResponseEntity<ProductQueriedView>> getProduct(
	        @RequestHeader(value = "X-Tenant-ID", defaultValue = "default-tenant") String tenantId, 
	        @RequestHeader(value = "X-User-Id", required = false) String userId,
	        @RequestHeader(value = "X-Session-Id", required = false) String sessionId,
	        @PathVariable String productId) {
		return findProductByIdUseCase.findById(tenantId, productId)
				.thenApply(data -> {
				    if (data != null) {
				        behaviorPort.logBehavior(userId, sessionId, productId, "VIEW", tenantId);
				        return ResponseEntity.ok(data);
				    }
				    return ResponseEntity.notFound().build();
				});
	}

	/**
	 * API: 分頁查詢商品 (GET /products?name=關鍵字&page=0&size=10)
	 */
	@GetMapping("/summary")
	public CompletableFuture<ResponseEntity<ProductPageQueriedView>> getProducts(
			@RequestHeader("X-Tenant-ID") String tenantId,
			@RequestParam(required = false) String name, 
			@RequestParam(required = false) String type, 
			@RequestParam(required = false) String subType, 
			@RequestParam(required = false) String status, 
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		return findProductsPagedUseCase.findPaged(tenantId, name, type, subType, status, page, size).thenApply(ResponseEntity::ok);
	}

	/**
	 * API: 上傳商品圖片至 MinIO
	 */
	@PostMapping("/upload-image")
	public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
		if (file.isEmpty()) {
			return ResponseEntity.badRequest().body("檔案不能為空");
		}
		String imageUrl = productImageStoragePort.uploadImage(file);
		return ResponseEntity.ok(imageUrl);
	}
}
