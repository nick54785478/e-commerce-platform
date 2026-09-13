package com.example.demo.iface.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateSettingResource {

    @NotBlank
    private String tenantId;

    @NotBlank
    private String dataType;

    @NotBlank
    private String type;

    @NotBlank
    private String name;

    @NotBlank
    private String code;

    private String value;

    private String description;

    @NotNull
    private Integer priorityNo;
}
