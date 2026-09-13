package com.example.demo.iface.dto.req;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.List;

public record CreateProductResource(
        String name,
        String description,
        @NotBlank
        String type,
        String subType,
        BigDecimal price,
        List<String> imageUrls,
        List<String> tags
) {
}
