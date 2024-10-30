package org.springframework.samples.learning;

import java.util.Collection;

public class LessSimpleTradingService implements TradingService {

    TradeRepository tradeRepository;
    AuditService auditService;
   
    public LessSimpleTradingService(TradeRepository tradeRepository, 
                                AuditService auditService)
    {
      this.tradeRepository = tradeRepository;
      this.auditService = auditService;
    }
  
    public Long createTrade(Trade trade) throws CreateTradeException {
    Long id = tradeRepository.createTrade(trade);
    auditService.logNewTrade(trade);
    return id;
  }

  @Override
    public Price priceTrade(Trade trade) {
        return tradeRepository.getPriceForTrade(trade);
    }

    @Override
    public Price getTotalPriceForTrades(Collection<Trade> trades) {
        Price totalPrice = new Price();
        for (Trade trade : trades) {
            Price tradePrice = tradeRepository.getPriceForTrade(trade);
            totalPrice = totalPrice.add(tradePrice);
        }
        return totalPrice;
    }

    @Override
    public Trade getTradeById(Long id) {
        return tradeRepository.getTradeById(id);
    }
}