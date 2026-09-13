package com.example.demo.iface.dto.res;

import com.omni.common.api.dto.PageQueriedView;
import com.omni.product.api.dto.ProductQueriedView;

/**
 * ProductPagedListQueriedResource - ?????亥岷蝯??頛?
 * <p>
 * ?冽???典恥?嗥垢?撣嗆???鞈??????柴? * </p>
 *
 * @param code    ??隞?Ⅳ
 * @param message ??閮
 * @param data    撠???鞈??摰寧?閬??拐辣
 */
public record ProductPagedListQueriedResource(String code, String message, PageQueriedView<ProductQueriedView> data) {
}

