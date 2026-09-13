package com.example.demo.iface.rest;

import com.example.demo.application.command.setting.CreateSettingCommand;
import com.example.demo.application.command.setting.GetSettingsQuery;
import com.example.demo.application.command.setting.UpdateSettingCommand;
import com.example.demo.application.dto.SettingGottenResult;
import com.example.demo.application.port.in.CreateSettingUseCase;
import com.example.demo.application.port.in.DeleteSettingUseCase;
import com.example.demo.application.port.in.SearchSettingsUseCase;
import com.example.demo.application.port.in.UpdateSettingUseCase;
import com.example.demo.iface.dto.req.CreateSettingResource;
import com.example.demo.iface.dto.req.UpdateSettingResource;
import com.example.demo.iface.dto.res.SettingListSearchedResource;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/settings")
@RequiredArgsConstructor
public class SettingController {

    private final CreateSettingUseCase createSettingUseCase;
    private final UpdateSettingUseCase updateSettingUseCase;
    private final DeleteSettingUseCase deleteSettingUseCase;
    private final SearchSettingsUseCase getSettingsUseCase;

    @GetMapping
    public ResponseEntity<SettingListSearchedResource> search(
            @RequestParam(required = false) String tenantId,
            @RequestParam(required = false) String dataType,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String activeFlag) {

        GetSettingsQuery query = new GetSettingsQuery(tenantId
                , dataType
                , type
                , name
                , activeFlag);

        List<SettingGottenResult> data = getSettingsUseCase.execute(query);
        return ResponseEntity.ok(new SettingListSearchedResource("200", "查詢成功", data));
    }

    @PostMapping
    public ResponseEntity<Long> createSetting(@Validated @RequestBody CreateSettingResource request) {
        CreateSettingCommand command = CreateSettingCommand.builder()
                .tenantId(request.getTenantId())
                .dataType(request.getDataType())
                .type(request.getType())
                .name(request.getName())
                .code(request.getCode())
                .value(request.getValue())
                .description(request.getDescription())
                .priorityNo(request.getPriorityNo())
                .build();

        Long id = createSettingUseCase.execute(command);
        return ResponseEntity.ok(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateSetting(
            @PathVariable Long id,
            @Validated @RequestBody UpdateSettingResource request) {

        UpdateSettingCommand command = UpdateSettingCommand.builder()
                .id(id)
                .tenantId(request.getTenantId())
                .dataType(request.getDataType())
                .type(request.getType())
                .name(request.getName())
                .code(request.getCode())
                .value(request.getValue())
                .description(request.getDescription())
                .priorityNo(request.getPriorityNo())
                .activeFlag(request.getActiveFlag())
                .build();

        updateSettingUseCase.execute(command);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSetting(@PathVariable Long id) {
        deleteSettingUseCase.execute(id);
        return ResponseEntity.ok().build();
    }
}
