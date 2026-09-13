package com.omni.product.api.dto;

import java.util.List;

/**
 * ProductPageQueriedView - 商品分頁查詢結果視圖
 */
public record ProductPageQueriedView(
    List<ProductQueriedView> content,
    long totalElements,
    int totalPages,
    int currentPage
) {}
