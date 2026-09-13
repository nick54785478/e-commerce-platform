package com.example.demo.iface.dto.res;

import com.example.demo.application.dto.SettingGottenResult;

import java.util.List;

public record SettingListSearchedResource(String code, String message, List<SettingGottenResult> data) {
}
