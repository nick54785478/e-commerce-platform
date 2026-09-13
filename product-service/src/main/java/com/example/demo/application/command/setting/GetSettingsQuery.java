package com.example.demo.application.command.setting;

public record GetSettingsQuery(
        String tenantId,
        String dataType,
        String type,
        String name,
        String activeFlag){}

