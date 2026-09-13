package com.example.demo.application.dto;

import com.example.demo.application.domain.shared.vo.YesNo;


public record SettingGottenResult(Long id, String tenantId, String dataType, String type,
                                  String name, String code, String value, String description, Integer priorityNo,
                                  YesNo activeFlag) {
}
