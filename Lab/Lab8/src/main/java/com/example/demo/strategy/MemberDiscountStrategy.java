package com.example.demo.strategy;

import org.springframework.stereotype.Component;

@Component
public class MemberDiscountStrategy implements DiscountStrategy {

    private static final double DISCOUNT_RATE = 0.10;

    @Override
    public double applyDiscount(double originalPrice) {
        return originalPrice * (1 - DISCOUNT_RATE);
    }
}
