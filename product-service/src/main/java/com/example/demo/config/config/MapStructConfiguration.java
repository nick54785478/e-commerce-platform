package com.example.demo.config.config;

import org.mapstruct.MapperConfig;

import com.example.demo.infra.mapper.BaseDataTransformMapper;

/**
 * MapStruct 全域設定，提供給所有 Mapper 繼承。只需在 Mapper 的 config 參數指定此 Config 即可。
 */
@MapperConfig(componentModel = "spring", uses = { BaseDataTransformMapper.class })
public interface MapStructConfiguration {
}
