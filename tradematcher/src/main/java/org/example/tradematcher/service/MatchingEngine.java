package org.example.tradematcher.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.example.tradematcher.model.Match;
import org.example.tradematcher.model.MatchResult;
import org.example.tradematcher.model.Order;
import org.example.tradematcher.model.OrderBook;
import org.example.tradematcher.model.Side;

public class MatchingEngine {
    
    private final Map<String, OrderBook> instrumentVsOrderBook = new HashMap<>();

    public void submitOrder(Order order){
        if(order.getQuantity() <=0) {
            throw new IllegalArgumentException("Quantity must be greate than zero, quantity: "+ order.getQuantity());
        }
        
         OrderBook orderBook = instrumentVsOrderBook.computeIfAbsent(order.getInstrument(), OrderBook::new);
         
         if(order.getSide() == Side.BUY){
            orderBook.addBid(order);
         } else {
            orderBook.addAsk(order);
         }
    }


    public MatchResult match(){

        List<Match> matches = new ArrayList<>();
        List<Order> unfilled = new ArrayList<>();

        for (OrderBook orderBook : instrumentVsOrderBook.values()){
            matchOrderBook (orderBook, matches);
            unfilled.addAll(orderBook.unFilledBids());
            unfilled.addAll(orderBook.unFilledAsks());
        }

        return new MatchResult(matches, unfilled);
    }


    private void matchOrderBook(OrderBook orderBook, List<Match> matches) {
        
        while (orderBook.hasBids() && orderBook.hasAsks()){
            Order bidOrder = orderBook.peekBestBid();
            Order askOrder = orderBook.peekBestAsk();


            if(bidOrder.getPrice().compareTo(askOrder.getPrice()) < 0){
                break;
            }

            orderBook.pollBestBid();
            orderBook.pollBestAsk();

            int matchQuantity = Math.min(bidOrder.getQuantity(), askOrder.getQuantity());

            matches.add(new Match(bidOrder.getOrderId(), askOrder.getOrderId(), orderBook.getInstrument(), matchQuantity, askOrder.getPrice()));

            bidOrder.reduceQuantity(matchQuantity);
            askOrder.reduceQuantity(matchQuantity);

            if(!bidOrder.isfullFilled()){
                orderBook.reInsertBid(bidOrder);
            }

            if(!askOrder.isfullFilled()){
                orderBook.reInsertAsk(askOrder);
            }

        }

    }

}
