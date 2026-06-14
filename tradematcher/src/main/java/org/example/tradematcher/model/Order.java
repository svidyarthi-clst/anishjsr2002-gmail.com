package org.example.tradematcher.model;

import java.math.BigDecimal;
import java.time.LocalTime;

public class Order {

    private final String orderId;
    private final String instrument;
    private final Side side;
    private int quantity;
    private final int originalQuantity;
    private final BigDecimal price;
    private final LocalTime timeStamp;


    public Order(String orderId, String instrument, Side side, int quantity, BigDecimal price,
            LocalTime timeStamp) {
        if (quantity <=0){
            throw new IllegalArgumentException("Quantity must be greate than zero, quantity: "+ quantity);
        }

        this.orderId = orderId;
        this.instrument = instrument;
        this.side = side;
        this.quantity = quantity;
        this.originalQuantity = quantity;
        this.price = price;
        this.timeStamp = timeStamp;
    }


    public void reduceQuantity(int matchingQuantity){
        if(matchingQuantity <=0 || matchingQuantity > quantity){
            throw new IllegalArgumentException("cannot reduce by " + matchingQuantity + " , remaining quantity: "+ quantity);
        }

        this.quantity = this.quantity - matchingQuantity;
    }


    public String getOrderId() {
        return orderId;
    }


    public String getInstrument() {
        return instrument;
    }


    public Side getSide() {
        return side;
    }


    public int getQuantity() {
        return quantity;
    }


    public int getOriginalQuantity() {
        return originalQuantity;
    }


    public BigDecimal getPrice() {
        return price;
    }


    public LocalTime getTimeStamp() {
        return timeStamp;
    }

    public boolean isfullFilled(){
        return quantity == 0;
    }


    @Override
    public String toString() {
        return "Order [orderId=" + orderId + ", instrument=" + instrument + ", side=" + side + ", quantity=" + quantity
                + ", originalQuantity=" + originalQuantity + ", price=" + price + ", timeStamp=" + timeStamp + "]";
    }


    


}