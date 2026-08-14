package com.example.demo.strategy;
import org.springframework.stereotype.Component;

@Component
public class SeasonalSaleStrategy implements DiscountStrategy {
    @Override
    public double calculateDiscountedPrice(double originalPrice) {
        return originalPrice * 0.80; // ลด 20%
    }
}