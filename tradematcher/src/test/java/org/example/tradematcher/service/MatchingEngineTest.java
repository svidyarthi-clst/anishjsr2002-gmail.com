package org.example.tradematcher.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

import org.example.tradematcher.model.Match;
import org.example.tradematcher.model.MatchResult;
import org.example.tradematcher.model.Order;
import org.example.tradematcher.model.Side;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MatchingEngineTest {

    private MatchingEngine matchingEngine;
    
    @BeforeEach
    public void setup(){
        matchingEngine = new MatchingEngine();
    }

    @Test
    public void testFullFill(){
        matchingEngine.submitOrder(new Order("B1", "AAPL", Side.BUY, 100, BigDecimal.valueOf(150.00), LocalTime.of(10, 0, 0)));
        matchingEngine.submitOrder(new Order("S1", "AAPL", Side.SELL, 100, BigDecimal.valueOf(150.00), LocalTime.of(10, 0, 1)));

        MatchResult result = matchingEngine.match();
        assertEquals(1, result.getMatches().size());
        Match m = result.getMatches().get(0);
        assertEquals("B1", m.getBuyOrderId());
        assertEquals("S1", m.getSellOrderId());
        assertEquals(100, m.getMatchedQuantity());
        assertEquals(BigDecimal.valueOf(150.00), m.getExecutionPrice());

    }

    @Test
    public void testPartialFill_OneBuyMultipleSells(){
        matchingEngine.submitOrder(new Order("B1", "AAPL", Side.BUY, 200, BigDecimal.valueOf(155.00), LocalTime.of(10, 0, 0)));
        matchingEngine.submitOrder(new Order("S1", "AAPL", Side.SELL, 80, BigDecimal.valueOf(150.00), LocalTime.of(10, 0, 1)));
        matchingEngine.submitOrder(new Order("S2", "AAPL", Side.SELL, 70, BigDecimal.valueOf(151.00), LocalTime.of(10, 0, 2)));

        MatchResult result = matchingEngine.match();
        assertEquals(2, result.getMatches().size());

        int totalQuantityMatched = result.getMatches().stream().mapToInt(Match::getMatchedQuantity).sum();
        assertEquals(150, totalQuantityMatched);

        List<Order> unfilled = result.getUnfilledOrders();
        assertEquals(1, unfilled.size());
        assertEquals("B1", unfilled.get(0).getOrderId());
        assertEquals(50, unfilled.get(0).getQuantity());

    }

    @Test
    public void testNoMatch_PriceMismatch(){
        matchingEngine.submitOrder(new Order("B1", "AAPL", Side.BUY, 100, BigDecimal.valueOf(148.00), LocalTime.of(10, 0, 0)));
        matchingEngine.submitOrder(new Order("S1", "AAPL", Side.SELL, 100, BigDecimal.valueOf(150.00), LocalTime.of(10, 0, 1)));
        

        MatchResult result = matchingEngine.match();
        assertTrue(result.getMatches().isEmpty());
        assertEquals(2, result.getUnfilledOrders().size());     

    }

    @Test
    public void testMultipleInstruments(){
        matchingEngine.submitOrder(new Order("B1", "AAPL", Side.BUY, 100, BigDecimal.valueOf(150.00), LocalTime.of(10, 0, 0)));
        matchingEngine.submitOrder(new Order("S1", "AAPL", Side.SELL, 100, BigDecimal.valueOf(149.00), LocalTime.of(10, 0, 1)));
        matchingEngine.submitOrder(new Order("B2", "GOOGL", Side.BUY, 50, BigDecimal.valueOf(2800.00), LocalTime.of(10, 0, 2)));
        matchingEngine.submitOrder(new Order("S2", "GOOGL", Side.SELL, 50, BigDecimal.valueOf(2795.00), LocalTime.of(10, 0, 3)));

        matchingEngine.submitOrder(new Order("B3", "ABC", Side.BUY, 200, BigDecimal.valueOf(70.00), LocalTime.of(10, 0, 2)));

        MatchResult result = matchingEngine.match();
        assertEquals(2, result.getMatches().size());

        boolean aaplMatched = result.getMatches().stream().anyMatch(m -> "AAPL".equalsIgnoreCase(m.getInstrument()));
        boolean googleMatched = result.getMatches().stream().anyMatch(m -> "GOOGL".equalsIgnoreCase(m.getInstrument()));
        assertTrue(aaplMatched);
        assertTrue(googleMatched);

        List<Order> unfilled = result.getUnfilledOrders();
        assertEquals(1, unfilled.size());
        assertEquals("B3", unfilled.get(0).getOrderId());

    }


    @Test
    public void testNoBuys(){
        matchingEngine.submitOrder(new Order("S1", "AAPL", Side.SELL, 100, BigDecimal.valueOf(150.00), LocalTime.of(10, 0, 1)));
        

        MatchResult result = matchingEngine.match();
        assertTrue(result.getMatches().isEmpty());
        assertEquals(1, result.getUnfilledOrders().size());     

    }    


    @Test
    public void testZeroQuantityRejected(){
        assertThrows(IllegalArgumentException.class, () -> {
            new Order("S1", "AAPL", Side.SELL, 0, BigDecimal.valueOf(150.00), LocalTime.of(10, 0, 1));
        });
    } 

    @Test
    public void testSimilarPriceAndTimestamp(){

        matchingEngine.submitOrder(new Order("S1", "AAPL", Side.SELL, 50, BigDecimal.valueOf(150.00), LocalTime.of(10, 0, 0)));
        matchingEngine.submitOrder(new Order("S2", "AAPL", Side.SELL, 50, BigDecimal.valueOf(150.00), LocalTime.of(10, 0, 0)));
        matchingEngine.submitOrder(new Order("B1", "AAPL", Side.BUY, 50, BigDecimal.valueOf(150.00), LocalTime.of(10, 0, 0)));

        

        MatchResult result = matchingEngine.match();
        assertEquals(1, result.getMatches().size());

       
        assertEquals(50, result.getMatches().get(0).getMatchedQuantity());
        assertEquals(1, result.getUnfilledOrders().size());

    }



    @Test
    public void testLargeQuantityDifference(){
        
        matchingEngine.submitOrder(new Order("B1", "AAPL", Side.BUY, 10000, BigDecimal.valueOf(200.00), LocalTime.of(10, 0, 0)));

        matchingEngine.submitOrder(new Order("S1", "AAPL", Side.SELL, 100, BigDecimal.valueOf(199.00), LocalTime.of(10, 0, 1)));
        matchingEngine.submitOrder(new Order("S2", "AAPL", Side.SELL, 100, BigDecimal.valueOf(199.00), LocalTime.of(10, 0, 2)));
        matchingEngine.submitOrder(new Order("S3", "AAPL", Side.SELL, 100, BigDecimal.valueOf(199.00), LocalTime.of(10, 0, 3)));
        matchingEngine.submitOrder(new Order("S4", "AAPL", Side.SELL, 100, BigDecimal.valueOf(199.00), LocalTime.of(10, 0, 4)));
        matchingEngine.submitOrder(new Order("S5", "AAPL", Side.SELL, 100, BigDecimal.valueOf(199.00), LocalTime.of(10, 0, 5)));

        

        MatchResult result = matchingEngine.match();
        assertEquals(5, result.getMatches().size());

        List<Order> unfilled = result.getUnfilledOrders();
        assertEquals(1, unfilled.size());
        assertEquals("B1", unfilled.get(0).getOrderId());
        assertEquals(9500, unfilled.get(0).getQuantity());
        
    }

    @Test
    public void testMultipleBuyAndSell(){
        

        matchingEngine.submitOrder(new Order("B1", "AAPL", Side.BUY, 100, BigDecimal.valueOf(150.00), LocalTime.of(10, 0, 0)));
        matchingEngine.submitOrder(new Order("B2", "AAPL", Side.BUY, 50, BigDecimal.valueOf(151.00), LocalTime.of(10, 0, 5)));
        matchingEngine.submitOrder(new Order("B3", "GOOGL", Side.BUY, 75, BigDecimal.valueOf(2800.00), LocalTime.of(10, 0, 10)));

        matchingEngine.submitOrder(new Order("S1", "AAPL", Side.SELL, 80, BigDecimal.valueOf(150.50), LocalTime.of(10, 0, 2)));
        matchingEngine.submitOrder(new Order("S2", "AAPL", Side.SELL, 30, BigDecimal.valueOf(149.00), LocalTime.of(10, 0, 7)));
        matchingEngine.submitOrder(new Order("S3", "GOOGL", Side.SELL, 100, BigDecimal.valueOf(2795.00), LocalTime.of(10, 0, 12)));

        MatchResult result = matchingEngine.match();
        assertEquals(3, result.getMatches().size());
        
        int totalAppleQuantity = result.getMatches().stream().filter(m -> "AAPL".equalsIgnoreCase(m.getInstrument())).mapToInt(Match::getMatchedQuantity).sum();
        assertEquals(50, totalAppleQuantity);

        int totalGoogleQuantity = result.getMatches().stream().filter(m -> "GOOGL".equalsIgnoreCase(m.getInstrument())).mapToInt(Match::getMatchedQuantity).sum();
        assertEquals(75, totalGoogleQuantity);

        List<Order> unfilled = result.getUnfilledOrders();
        assertEquals(3, unfilled.size());
        
        
        
    }


}
