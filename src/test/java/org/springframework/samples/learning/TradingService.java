package org.springframework.samples.learning;

import java.util.Collection;

public interface TradingService {

    public Price priceTrade(Trade trade);
    public Price getTotalPriceForTrades(Collection<Trade> trades);
	public Trade getTradeById(Long id);
	public Long createTrade(Trade trade);
}
