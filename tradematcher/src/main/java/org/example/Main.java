package org.example;

import java.math.BigDecimal;
import java.time.LocalTime;

import org.example.tradematcher.model.Order;
import org.example.tradematcher.model.Side;
import org.example.tradematcher.service.MatchingEngine;


public class Main {
    public static void main(String[] args) {

        MatchingEngine matchingEngine = new MatchingEngine();

        matchingEngine.submitOrder(new Order("B1", "AAPL", Side.BUY, 100, BigDecimal.valueOf(150.00), LocalTime.of(10, 0, 0)));
        matchingEngine.submitOrder(new Order("B2", "AAPL", Side.BUY, 50, BigDecimal.valueOf(151.00), LocalTime.of(10, 0, 5)));
        matchingEngine.submitOrder(new Order("B3", "GOOGL", Side.BUY, 75, BigDecimal.valueOf(2800.00), LocalTime.of(10, 0, 10)));

        matchingEngine.submitOrder(new Order("S1", "AAPL", Side.SELL, 80, BigDecimal.valueOf(150.50), LocalTime.of(10, 0, 2)));
        matchingEngine.submitOrder(new Order("S2", "AAPL", Side.SELL, 30, BigDecimal.valueOf(149.00), LocalTime.of(10, 0, 7)));
        matchingEngine.submitOrder(new Order("S3", "GOOGL", Side.SELL, 100, BigDecimal.valueOf(2795.00), LocalTime.of(10, 0, 12)));
        

        matchingEngine.match().displayResults();



    }
}