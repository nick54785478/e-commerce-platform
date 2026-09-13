package com.example.demo.application.service;

import com.example.demo.application.command.setting.CreateSettingCommand;
import com.example.demo.application.command.setting.GetSettingsQuery;
import com.example.demo.application.command.setting.UpdateSettingCommand;
import com.example.demo.application.domain.setting.aggregate.Setting;
import com.example.demo.application.domain.setting.aggregate.vo.SettingProfile;
import com.example.demo.application.dto.SettingGottenResult;
import com.example.demo.application.port.in.CreateSettingUseCase;
import com.example.demo.application.port.in.DeleteSettingUseCase;
import com.example.demo.application.port.in.SearchSettingsUseCase;
import com.example.demo.application.port.in.UpdateSettingUseCase;
import com.example.demo.application.port.out.SettingRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
class SettingApplicationService implements CreateSettingUseCase, UpdateSettingUseCase, DeleteSettingUseCase, SearchSettingsUseCase {

    private final SettingRepositoryPort settingRepositoryPort;

    @Override
    @Transactional
    public Long execute(CreateSettingCommand command) {
        SettingProfile profile = new SettingProfile(
                command.getTenantId(),
                command.getDataType(),
                command.getType(),
                command.getName(),
                command.getCode(),
                command.getValue(),
                command.getDescription(),
                command.getPriorityNo());

        Setting setting = Setting.create(profile);
        Setting savedSetting = settingRepositoryPort.save(setting);
        return savedSetting.getId();
    }

    @Override
    @Transactional
    public void execute(UpdateSettingCommand command) {
        Setting setting = settingRepositoryPort.findById(command.getId())
                .orElseThrow(() -> new IllegalArgumentException("Setting not found for id: " + command.getId()));

        SettingProfile profile = new SettingProfile(
                command.getTenantId(),
                command.getDataType(),
                command.getType(),
                command.getName(),
                command.getCode(),
                command.getValue(),
                command.getDescription(),
                command.getPriorityNo()
        );

        setting.update(profile, command.getActiveFlag());
        settingRepositoryPort.save(setting);
    }

    @Override
    @Transactional
    public void execute(Long id) {
        Setting setting = settingRepositoryPort.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Setting not found for id: " + id));
        setting.delete();
        settingRepositoryPort.save(setting);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SettingGottenResult> execute(GetSettingsQuery query) {
        return settingRepositoryPort.searchSettings(query);
    }
}
