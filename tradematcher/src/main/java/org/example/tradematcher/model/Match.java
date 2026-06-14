package org.example.tradematcher.model;

import java.math.BigDecimal;

public class Match {
    
    private final String buyOrderId;
    private final String sellOrderId;
    private final String instrument;
    private final int matchedQuantity;
    private final BigDecimal executionPrice;
    public Match(String buyOrderId, String sellOrderId, String instrument, int matchedQuantity,
            BigDecimal executionPrice) {
        this.buyOrderId = buyOrderId;
        this.sellOrderId = sellOrderId;
        this.instrument = instrument;
        this.matchedQuantity = matchedQuantity;
        this.executionPrice = executionPrice;
    }
    public String getBuyOrderId() {
        return buyOrderId;
    }
    public String getSellOrderId() {
        return sellOrderId;
    }
    public String getInstrument() {
        return instrument;
    }
    public int getMatchedQuantity() {
        return matchedQuantity;
    }
    public BigDecimal getExecutionPrice() {
        return executionPrice;
    }

    @Override
    public String toString() {
        return "Match [buyOrderId=" + buyOrderId + ", sellOrderId=" + sellOrderId + ", instrument=" + instrument
                + ", matchedQuantity=" + matchedQuantity + ", executionPrice=" + executionPrice + "]";
    }

}
