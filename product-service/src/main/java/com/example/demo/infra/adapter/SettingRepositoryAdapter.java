package com.example.demo.infra.adapter;

import com.example.demo.application.command.setting.GetSettingsQuery;
import com.example.demo.application.domain.setting.aggregate.Setting;
import com.example.demo.application.dto.SettingGottenResult;
import com.example.demo.application.port.out.SettingRepositoryPort;
import com.example.demo.infra.persistence.SettingRepository;
import com.example.demo.infra.spec.GetSettingsSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
class SettingRepositoryAdapter implements SettingRepositoryPort {

    private final SettingRepository settingRepository;

    @Override
    public Setting save(Setting setting) {
        return settingRepository.save(setting);
    }

    @Override
    public Optional<Setting> findById(Long id) {
        return settingRepository.findById(id);
    }

    @Override
    public List<SettingGottenResult> searchSettings(GetSettingsQuery query) {
        GetSettingsSpecification spec = new GetSettingsSpecification(
                query.tenantId(),
                query.dataType(),
                query.type(),
                query.name(),
                query.activeFlag()
        );

        List<Setting> entities = settingRepository.findAll(spec.toSpecification());
        return entities.stream().map(this::toView).collect(Collectors.toList());
    }

    /**
     * Setting -> SettingGottenResult
     *
     * @param entity {@link Setting}
     * @return {@link SettingGottenResult}
     */
    private SettingGottenResult toView(Setting entity) {
        return new SettingGottenResult(entity.getId(), entity.getTenantId(), entity.getDataType(),
                entity.getType(), entity.getName(), entity.getCode(), entity.getValue(), entity.getDescription(),
                entity.getPriorityNo(), entity.getActiveFlag());
    }
}
