package com.example.demo.iface.dto.res;

/**
 * ProductCreatedResource - 商品建立結果回傳載體
 * <p>
 * 用於向外部客戶端回傳商品建立操作的結果。
 * </p>
 *
 * @param code      回應代碼 (例如 "200")
 * @param message   回應訊息
 * @param productId 建立成功後的商品唯一識別碼
 */
public record ProductCreatedResource(String code, String message, String productId) {
}
