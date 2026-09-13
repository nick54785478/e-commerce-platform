package com.omni.recommender.behavior.application.service;

import com.omni.recommender.behavior.application.command.LogBehaviorCommand;
import com.omni.recommender.behavior.application.port.in.GetRecentBehaviorsUseCase;
import com.omni.recommender.behavior.application.port.in.LogBehaviorUseCase;

import com.omni.recommender.behavior.application.port.out.MessagePublisherPort;
import com.omni.recommender.behavior.application.command.outbound.PublishEventCommand;
import com.omni.recommender.behavior.domain.behavior.aggregate.root.UserBehavior;
import com.omni.recommender.behavior.domain.behavior.aggregate.vo.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <h2>用戶行為 Application Service (應用服務層)</h2>
 * <p>
 * 在 Clean Architecture 中扮演 Use Case 的實作角色 (Inbound Adapter)，
 * 負責協調領域模型 (Domain Model) 與基礎設施 (Infrastructure)。
 * 目前系統採用事件驅動架構 (EDA)，主要職責為接收使用者的操作日誌，
 * 將其封裝為 {@link UserBehavior} 領域實體，並透過 Kafka 非同步發送至外部事件總線。
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BehaviorApplicationService implements LogBehaviorUseCase, GetRecentBehaviorsUseCase {

    private final MessagePublisherPort messagePublisherPort;
    private final ObjectMapper objectMapper;

    /**
     * 執行行為打點的 Use Case。
     * <p>
     * 處理流程：
     * <ol>
     *     <li>將輸入的 Command 轉換為強型別的 Domain Value Objects。</li>
     *     <li>透過 Factory Method 建立 Aggregate Root ({@link UserBehavior})，確保業務規則。</li>
     *     <li>將領域事件序列化為 JSON，並以 {@code sessionId} 或 {@code userId} 作為 Routing Key，
     *         透過 {@link MessagePublisherPort} 發布至 Kafka，達到非同步解耦與削峰填谷。</li>
     * </ol>
     * </p>
     *
     * @param command 包含原始用戶行為資料的指令物件
     */
    @Override
    public void execute(LogBehaviorCommand command) {
        // 1. 轉換 Command 為 Domain Value Objects
        TenantId tenantId = command.tenantId() != null ? new TenantId(command.tenantId()) : null;
        UserId userId = command.userId() != null ? new UserId(command.userId()) : null;
        SessionId sessionId = command.sessionId() != null ? new SessionId(command.sessionId()) : null;
        ItemId itemId = command.itemId() != null ? new ItemId(command.itemId()) : null;
        BehaviorType type = BehaviorType.valueOf(command.behaviorType().toUpperCase());
        DeviceInfo deviceInfo = new DeviceInfo(command.clientIp(), command.userAgent());
        BehaviorContext context = new BehaviorContext(command.metadata());
        
        // 2. 建立 Aggregate Root
        UserBehavior behavior = UserBehavior.log(
            tenantId, userId, sessionId, itemId, type, deviceInfo, 
            command.referrerUrl(), context, command.timestamp()
        );
        
        // 3. Dual-Write: 發送事件至 Kafka (取代本地日誌)
        try {
            String eventJson = objectMapper.writeValueAsString(behavior);
            String routingKey = sessionId != null ? sessionId.value() : (userId != null ? userId.value() : behavior.getBehaviorId());
            messagePublisherPort.send(new PublishEventCommand("user-behavior-events", routingKey, eventJson));
        } catch (Exception e) {
            log.error("Failed to serialize UserBehavior event", e);
        }
        
    }
    
    /**
     * 獲取使用者近期的行為紀錄。
     * <p>
     * <b>注意：</b> 原本依賴 HBase 的實作已於系統現代化過程 (遷移至 EDA/MinIO/Redis) 中被淘汰與移除。
     * 未來若需查詢歷史行為，應從 Data Lake (如 Iceberg) 或極速快取 (Redis) 中獲取。
     * 目前此方法僅為維持介面相容性，固定回傳空列表。
     * </p>
     *
     * @param userId 目標使用者 ID
     * @param limit  最大回傳筆數
     * @return 永遠回傳空列表 {@link List#of()}
     * @deprecated 因 HBase 淘汰而廢棄，預計在下一版本的架構清理中完全移除此 Use Case。
     */
    @Deprecated
    public List<UserBehavior> getRecentBehaviors(String userId, int limit) {
        // HBase has been deprecated and removed. Recent behaviors should be fetched from Data Lake or Redis in the future.
        return List.of();
    }
}
