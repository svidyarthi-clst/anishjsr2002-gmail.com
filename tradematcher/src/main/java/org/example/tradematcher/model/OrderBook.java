package org.example.tradematcher.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

public class OrderBook {
    

    private final String instrument;

    private final TreeMap<BigDecimal, LinkedList<Order>> bids = new TreeMap<>(Collections.reverseOrder());

    private final TreeMap<BigDecimal, LinkedList<Order>> asks = new TreeMap<>();

    public OrderBook(String instrument) {
        this.instrument = instrument;
    }

    public String getInstrument() {
        return instrument;
    }


    public void addBid(Order order) {
        bids.computeIfAbsent(order.getPrice(), p -> new LinkedList<>()).addLast(order);
    }

    public void addAsk(Order order) {
        asks.computeIfAbsent(order.getPrice(), p -> new LinkedList<>()).addLast(order);
    }
    
    public Order peekBestBid(){
        if(bids.isEmpty()){
            return null;
        }
        return bids.firstEntry().getValue().peekFirst();
    }

    public Order peekBestAsk(){
        if(asks.isEmpty()){
            return null;
        }
        return asks.firstEntry().getValue().peekFirst();
    }

    public Order pollBestBid(){
        if(bids.isEmpty()){
            return null;
        }
        Entry<BigDecimal, LinkedList<Order>> entry = bids.firstEntry();
        Order order = entry.getValue().pollFirst();
        if(entry.getValue().isEmpty()){
            bids.pollFirstEntry();
        }
        return order;
    }

    public Order pollBestAsk(){
        if(asks.isEmpty()){
            return null;
        }
        Entry<BigDecimal, LinkedList<Order>> entry = asks.firstEntry();
        Order order = entry.getValue().pollFirst();
        if(entry.getValue().isEmpty()){
            asks.pollFirstEntry();
        }
        return order;
    }

    public void reInsertBid(Order order) {
        bids.computeIfAbsent(order.getPrice(), p -> new LinkedList<>()).addFirst(order);
    }

    public void reInsertAsk(Order order) {
        asks.computeIfAbsent(order.getPrice(), p -> new LinkedList<>()).addFirst(order);
    }

    public List<Order> unFilledBids() {
        List<Order> result = new ArrayList<>();
        for (LinkedList<Order> order : bids.values()){
            result.addAll(order);
        }
        return result;
    }

    public List<Order> unFilledAsks() {
        List<Order> result = new ArrayList<>();
        for (LinkedList<Order> order : asks.values()){
            result.addAll(order);
        }
        return result;
    }

    public boolean hasBids(){
        return !bids.isEmpty();
    }

    public boolean hasAsks(){
        return !asks.isEmpty();
    }




}
