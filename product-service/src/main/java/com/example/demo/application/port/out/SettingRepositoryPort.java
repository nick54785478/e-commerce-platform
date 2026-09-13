package com.example.demo.application.port.out;

import java.util.List;
import java.util.Optional;

import com.example.demo.application.command.setting.GetSettingsQuery;
import com.example.demo.application.domain.setting.aggregate.Setting;
import com.example.demo.application.dto.SettingGottenResult;

/**
 * Outbound Port - 設定儲存庫介面
 * <p>
 * 遵循 Hexagonal Architecture (六角架構) 中的 Secondary Port (Outbound Port)。
 * 負責定義與持久層 (Persistence Layer) 互動的合約，
 * 使得 Application Layer 可以存取設定資料，而不需要依賴具體的資料庫實作 (如 JPA, JDBC)。
 * </p>
 */
public interface SettingRepositoryPort {
    /**
     * 儲存設定實體
     * <p>
     * 如果設定 ID 已存在則更新，否則建立新設定。
     * </p>
     * @param setting 設定實體
     * @return 儲存後的設定實體
     */
    Setting save(Setting setting);

    /**
     * 根據 ID 查詢設定實體
     * @param id 設定實體 ID
     * @return 包含設定實體的 Optional
     */
    Optional<Setting> findById(Long id);
    
    /**
     * 根據查詢條件搜尋設定集合
     * @param query 搜尋設定的條件
     * @return 搜尋結果的列表
     */
    List<SettingGottenResult> searchSettings(GetSettingsQuery query);
}
