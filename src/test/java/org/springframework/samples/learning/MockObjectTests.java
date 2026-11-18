package org.springframework.samples.learning;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.springframework.samples.petclinic.testing.IsolatedTest;
import org.springframework.samples.petclinic.testing.ServiceTest;

@Epic("Testing examples")
@Feature("Test doubles")
@io.qameta.allure.Owner("DP1-tutors")
@ServiceTest
@IsolatedTest
@ExtendWith(MockitoExtension.class)
public class MockObjectTests {

    // This will be a stub
    @Mock
    TradeRepository tradeRepository;

    // This will be a mock
    @Mock
    AuditService auditService;

    @Test
    public void testAuditLogEntryMadeForNewTrade() throws Exception {
        Trade trade = new Trade("Ref 1", "Description 1");
        when(tradeRepository.createTrade(trade)).thenReturn(anyLong());

        TradingService tradingService = new LessSimpleTradingService(tradeRepository, auditService);
        tradingService.createTrade(trade);

        verify(auditService).logNewTrade(trade);

        //verify(auditService, times(2)).logNewTrade(trade);
    }

}