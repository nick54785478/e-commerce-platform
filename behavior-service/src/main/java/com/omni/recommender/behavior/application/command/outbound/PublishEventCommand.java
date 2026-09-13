package com.omni.recommender.behavior.application.command.outbound;

/**
 * 外部發布事件指令
 *
 * @param topic      Kafka Topic 名稱
 * @param routingKey 路由 Key (用於保證同一個 Key 的訊息被寫入同一個 Partition)
 * @param eventJson  序列化後的事件 JSON 字串
 */
public record PublishEventCommand(
        String topic,
        String routingKey,
        String eventJson
) {
}
