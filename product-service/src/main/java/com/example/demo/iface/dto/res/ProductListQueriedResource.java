package com.example.demo.iface.dto.res;

import java.util.List;

import com.omni.product.api.dto.ProductQueriedView;

/**
 * ProductListQueriedResource - ??皜?亥岷蝯??頛?
 * <p>
 * ?冽???典恥?嗥垢?憭??????? * </p>
 *
 * @param code    ??隞?Ⅳ
 * @param message ??閮
 * @param data    ??鞈?皜??
 */
public record ProductListQueriedResource(String code, String message, List<ProductQueriedView> data) {
}

