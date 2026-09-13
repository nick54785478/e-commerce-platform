package com.omni.recommender.behavior.application.port.out;

import com.omni.recommender.behavior.application.command.outbound.PublishEventCommand;

/**
 * <h2>Message Publisher Port (外部輸出埠 - 訊息發布合約)</h2>
 */
public interface MessagePublisherPort {

    /**
     * 根據指令發送非同步整合事件至外部訊息總線。
     *
     * @param command 發布事件指令物件
     */
    void send(PublishEventCommand command);
}
