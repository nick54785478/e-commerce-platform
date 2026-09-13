package com.omni.recommender.behavior.domain.behavior.aggregate.vo;

/**
 * 用戶行為類型
 */
public enum BehaviorType {
    VIEW,              // 瀏覽商品詳情或頁面
    CLICK,             // 點擊推薦卡片或廣告
    FAVORITE,          // 加入最愛 / 收藏 (替換原本的 LIKE，更符合電商情境)
    UNFAVORITE,        // 移除最愛
    ADD_TO_CART,       // 加入購物車
    REMOVE_FROM_CART,  // 從購物車移除
    CHECKOUT,          // 進入結帳流程
    PURCHASE,          // 購買成功
    SEARCH,            // 搜尋關鍵字
    RATE,              // 評價 / 評分
    SHARE              // 分享商品
}
