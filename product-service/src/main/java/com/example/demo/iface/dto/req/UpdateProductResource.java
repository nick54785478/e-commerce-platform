package com.example.demo.iface.dto.req;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.List;

public record UpdateProductResource(
        Long version,
        String name,
        String description,
        @NotBlank(message = "分類不可為空")
        String type,
        String subType,
        BigDecimal price,
        List<String> imageUrls,
        List<String> tags
) {
}
