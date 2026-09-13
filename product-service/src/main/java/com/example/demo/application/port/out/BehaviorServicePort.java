package com.example.demo.application.port.out;

/**
 * Outbound Port - 使用者行為日誌傳送介面
 * <p>
 * 遵循 Hexagonal Architecture (六角架構) 中的 Secondary Port (Outbound Port)。
 * 負責定義傳送使用者行為日誌到外部系統的合約，
 * 使得 Application Layer (如 BehaviorTrackingService) 能夠記錄行為，
 * 而不需依賴底層日誌系統的具體實作 (如 Kafka Producer 或 Elasticsearch)。
 * </p>
 */
public interface BehaviorServicePort {

    /**
     * 傳送使用者行為日誌
     *
     * @param userId       使用者 ID
     * @param sessionId    Session ID
     * @param itemId       商品 ID
     * @param behaviorType 行為類型 (如 VIEW, FAVORITE)
     * @param tenantId     租戶 ID
     */
    void logBehavior(String userId, String sessionId, String itemId, String behaviorType, String tenantId);
}
