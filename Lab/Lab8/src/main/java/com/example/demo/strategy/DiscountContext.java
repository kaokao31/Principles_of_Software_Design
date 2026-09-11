package com.example.demo.strategy;

public class DiscountContext {

    private final DiscountStrategy strategy;

    public DiscountContext(DiscountStrategy strategy) {
        this.strategy = strategy;
    }

    public double calculatePrice(double originalPrice) {
        return strategy.applyDiscount(originalPrice);
    }
}
