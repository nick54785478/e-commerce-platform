package com.example.demo.application.domain.product.query;

/**
 * FindProductsPagedQuery - 分頁查詢商品命令
 * <p>
 * 用於 CQRS 架構中依照名稱模糊搜尋，並取得分頁資料。
 * </p>
 * 
 * @param tenantId 租戶識別碼
 * @param name     商品名稱關鍵字 (模糊查詢)
 * @param type     商品種類過濾條件
 * @param subType  商品次要種類過濾條件
 * @param status   商品狀態過濾條件 (ACTIVE, INACTIVE)
 * @param page     頁碼 (從 0 開始)
 * @param size     每頁顯示筆數
 */
public record FindProductsPagedQuery(String tenantId, String name, String type, String subType, String status, int page, int size) {
}
