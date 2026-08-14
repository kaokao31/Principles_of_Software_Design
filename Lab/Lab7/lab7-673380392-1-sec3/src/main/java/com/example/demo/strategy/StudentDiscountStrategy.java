package com.example.demo.strategy;
import org.springframework.stereotype.Component;

@Component
public class StudentDiscountStrategy implements DiscountStrategy {
    @Override
    public double calculateDiscountedPrice(double originalPrice) {
        return originalPrice * 0.90; // ลด 10%
    }
}