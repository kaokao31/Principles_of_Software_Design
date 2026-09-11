package com.example.demo.strategy;

import org.springframework.stereotype.Component;

@Component
public class SeasonalSaleStrategy implements DiscountStrategy {

    private static final double DISCOUNT_RATE = 0.20;

    @Override
    public double applyDiscount(double originalPrice) {
        return originalPrice * (1 - DISCOUNT_RATE);
    }
}
