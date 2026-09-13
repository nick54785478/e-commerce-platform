package com.omni.recommender.behavior.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.omni.recommender.behavior.application.command.LogBehaviorCommand;
import com.omni.recommender.behavior.application.port.out.MessagePublisherPort;
import com.omni.recommender.behavior.application.command.outbound.PublishEventCommand;
import com.omni.recommender.behavior.domain.behavior.aggregate.vo.BehaviorType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BehaviorApplicationServiceTest {

    private MessagePublisherPort messagePublisherPort;
    private ObjectMapper objectMapper;
    private BehaviorApplicationService applicationService;

    @BeforeEach
    void setUp() {
        // Mock Outbound Port
        messagePublisherPort = Mockito.mock(MessagePublisherPort.class);
        objectMapper = Mockito.mock(ObjectMapper.class);
        // 初始化 Application Service
        applicationService = new BehaviorApplicationService(messagePublisherPort, objectMapper);
    }

    @Test
    void shouldLogBehaviorSuccessfully() throws Exception {
        // Arrange (準備 Command)
        LogBehaviorCommand command = new LogBehaviorCommand(
                "T1001",
                "U1001",
                "S2002",
                "Item3003",
                "CLICK",
                "192.168.1.1",
                "Mozilla/5.0",
                "https://google.com",
                Map.of("position", "1"),
                Instant.now()
        );

        when(objectMapper.writeValueAsString(any())).thenReturn("{\"mock\":\"json\"}");

        // Act (執行業務邏輯)
        applicationService.execute(command);

        // Assert (驗證結果與邊界行為)
        ArgumentCaptor<PublishEventCommand> captor = ArgumentCaptor.forClass(PublishEventCommand.class);
        verify(messagePublisherPort).send(captor.capture()); // 驗證 Outbound Port 是否正確被呼叫

        PublishEventCommand pubCmd = captor.getValue();
        assertEquals("user-behavior-events", pubCmd.topic());
        assertEquals("S2002", pubCmd.routingKey());
        assertEquals("{\"mock\":\"json\"}", pubCmd.eventJson());
    }

    @Test
    void shouldThrowExceptionWhenSearchWithoutKeyword() {
        // Arrange (準備缺乏 keyword 的 SEARCH Command)
        LogBehaviorCommand command = new LogBehaviorCommand(
                "T1001",
                "U1001",
                "S2002",
                "Item3003",
                "SEARCH", 
                "192.168.1.1",
                "Mozilla/5.0",
                "https://google.com",
                Map.of("wrong_key", "value"), // 遺失了 keyword
                Instant.now()
        );

        // Act & Assert (驗證領域例外拋出)
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            applicationService.execute(command);
        });
        
        assertEquals("SEARCH behavior must have a keyword in metadata", exception.getMessage());
    }
}
