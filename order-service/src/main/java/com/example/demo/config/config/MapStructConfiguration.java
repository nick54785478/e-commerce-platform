package com.example.demo.config.config;

import org.mapstruct.MapperConfig;

import com.example.demo.infra.mapper.BaseDataTransformMapper;

/**
 * MapStruct 配置，用於設定全域的 Mapper，只需要在 Mapper 的 config 屬性指定此 Config 即可
 */
@MapperConfig(componentModel = "spring", uses = { BaseDataTransformMapper.class })
public interface MapStructConfiguration {
}
