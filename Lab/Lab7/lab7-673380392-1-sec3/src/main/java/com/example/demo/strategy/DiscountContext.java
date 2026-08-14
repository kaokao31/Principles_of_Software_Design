package com.example.demo.strategy;
import org.springframework.stereotype.Component;

@Component
public class DiscountContext {

    private final NoDiscountStrategy noDiscountStrategy;
    private final StudentDiscountStrategy studentDiscountStrategy;
    private final SeasonalSaleStrategy seasonalSaleStrategy;

    public DiscountContext(NoDiscountStrategy noDiscountStrategy,
                            StudentDiscountStrategy studentDiscountStrategy,
                            SeasonalSaleStrategy seasonalSaleStrategy) {
        this.noDiscountStrategy = noDiscountStrategy;
        this.studentDiscountStrategy = studentDiscountStrategy;
        this.seasonalSaleStrategy = seasonalSaleStrategy;
    }

    public double calculatePrice(String discountType, double originalPrice) {
        DiscountStrategy strategy = resolveStrategy(discountType);
        return strategy.calculateDiscountedPrice(originalPrice);
    }

    public String getDiscountName(String discountType) {
        return switch (discountType == null ? "NONE" : discountType) {
            case "STUDENT" -> "ส่วนลดนักศึกษา (10%)";
            case "SEASONAL" -> "ส่วนลดเทศกาล (20%)";
            default -> "ราคาปกติ";
        };
    }

    private DiscountStrategy resolveStrategy(String discountType) {
        return switch (discountType == null ? "NONE" : discountType) {
            case "STUDENT" -> studentDiscountStrategy;
            case "SEASONAL" -> seasonalSaleStrategy;
            default -> noDiscountStrategy;
        };
    }
}