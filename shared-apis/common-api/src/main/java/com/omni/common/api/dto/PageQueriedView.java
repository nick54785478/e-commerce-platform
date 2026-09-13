package com.omni.common.api.dto;

import java.util.List;

/**
 * PageQueriedView - 分頁查詢結果泛型視圖
 */
public record PageQueriedView<T>(List<T> content, long totalElements, int totalPages, int currentPage) {
}
