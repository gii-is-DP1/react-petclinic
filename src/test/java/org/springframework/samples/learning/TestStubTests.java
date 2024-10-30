package org.springframework.samples.learning;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.jupiter.api.Test;

public class TestStubTests {

    public Collection<Trade> getTrades() {
        Collection<Trade> list = new ArrayList<Trade>();
        list.add(new Trade(1L));
        list.add(new Trade(2L));
        list.add(new Trade(3L));
        return list;
    }

    //This is a responder example
    @Test
    public void testGetHighestPricedTrade() throws Exception {
        Price price1 = new Price(10);
        Price price2 = new Price(15);
        Price price3 = new Price(25);

        TradeRepository tradeRepository = mock(TradeRepository.class);
        when(tradeRepository.getPriceForTrade(any(Trade.class))).thenReturn(price1, price2, price3);

        TradingService service = new SimpleTradingService(tradeRepository);
        Price totalPrice = service.getTotalPriceForTrades(getTrades());

        assertEquals(10+15+25, totalPrice.getAmount());
    }

    //This is a saboteur example
    @Test
    public void testInvalidTrade() throws Exception {

        Trade trade = new Trade(1L);
        TradeRepository tradeRepository = mock(TradeRepository.class);

        when(tradeRepository.getTradeById(anyLong())).thenThrow(new TradeNotFoundException());

        TradingService tradingService = new SimpleTradingService(tradeRepository);
        assertThrows(TradeNotFoundException.class, () -> tradingService.getTradeById(trade.getId()));   
    }

}