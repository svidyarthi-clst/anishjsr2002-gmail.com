package org.example.tradematcher.model;

import java.util.List;

public class MatchResult {
    
    private final List<Match> matches;
    private final List<Order> unfilledOrders;

    public MatchResult(List<Match> matches, List<Order> unfilledOrders) {
        this.matches = matches;
        this.unfilledOrders = unfilledOrders;
    }

    public List<Match> getMatches() {
        return matches;
    }

    public List<Order> getUnfilledOrders() {
        return unfilledOrders;
    }

    public void displayResults(){
        System.out.println("=== Matches (" + matches.size() + ") ===");
        for (int count = 0; count < matches.size(); count++){
            Match match = matches.get(count);
            System.out.printf(" %d. Instrument: %s, BuyOrder: %s, SellOrder: %s, Qty: %d, Price: $%s%n",
                count+1, match.getInstrument(), match.getBuyOrderId(), match.getSellOrderId(), match.getMatchedQuantity(), match.getExecutionPrice()
            );
        }


        System.out.println("\n=== Unfilled Orders (" + unfilledOrders.size() + ") ===");
        for(int count = 0; count < unfilledOrders.size(); count++){
            Order order = unfilledOrders.get(count);
            System.out.printf("%d. %s: %s, Instrument: %s, RemainingQty: %d, Price: $%s%n", 
                count+1, order.getOrderId(), order.getSide(), order.getInstrument(), order.getQuantity(), order.getPrice()
            );
        }

    }

}
