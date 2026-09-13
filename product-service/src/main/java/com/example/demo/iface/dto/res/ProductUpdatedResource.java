package com.example.demo.iface.dto.res;

/**
 * ProductUpdatedResource - 商品更新結果回傳載體
 * <p>
 * 用於向外部客戶端回傳商品更新操作的結果。
 * </p>
 *
 * @param code      回應代碼
 * @param message   回應訊息
 * @param productId 被更新的商品唯一識別碼
 */
public record ProductUpdatedResource(String code, String message, String productId) {
}
