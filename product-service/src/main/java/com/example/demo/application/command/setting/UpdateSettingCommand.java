package com.example.demo.application.command.setting;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateSettingCommand {
    private Long id;
    private String tenantId;
    private String dataType;
    private String type;
    private String name;
    private String code;
    private String value;
    private String description;
    private Integer priorityNo;
    private String activeFlag;
}
