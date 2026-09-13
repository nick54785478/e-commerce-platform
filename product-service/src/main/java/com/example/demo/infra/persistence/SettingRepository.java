package com.example.demo.infra.persistence;

import java.util.List;

import com.example.demo.application.domain.setting.aggregate.Setting;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.application.domain.shared.vo.YesNo;

public interface SettingRepository extends JpaRepository<Setting, Long> {

	List<Setting> findByDataTypeAndActiveFlag(String dataType, YesNo activeFlag);

	List<Setting> findByDataTypeAndTypeAndActiveFlag(String dataType, String type, YesNo activeFlag);

	List<Setting> findAll(Specification<Setting> specification);

	Setting findByCode(String code);
}
