package org.springframework.samples.learning;

import java.util.Collection;

public class SimpleTradingService implements TradingService {
    TradeRepository repository;

    public SimpleTradingService(TradeRepository tradeRepository) {
        this.repository = tradeRepository;
    }

    @Override
    public Price priceTrade(Trade trade) {
        return repository.getPriceForTrade(trade);
    }

    @Override
    public Price getTotalPriceForTrades(Collection<Trade> trades) {
        Price totalPrice = new Price();
        for (Trade trade : trades) {
            Price tradePrice = repository.getPriceForTrade(trade);
            totalPrice = totalPrice.add(tradePrice);
        }
        return totalPrice;
    }

    @Override
    public Trade getTradeById(Long id) {
        return repository.getTradeById(id);
    }

    @Override
    public Long createTrade(Trade trade) {
        return repository.createTrade(trade);
    }
}