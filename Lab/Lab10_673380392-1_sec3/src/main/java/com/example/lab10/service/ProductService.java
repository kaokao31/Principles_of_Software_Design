package com.example.lab10.service;

import com.example.lab10.model.Product;
import com.example.lab10.repository.ProductRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ProductService {

    // ── Constructor Injection (DIP — SOLID) ─────────────
    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    // ── 1. ดึง Product 1 รายการ ──────────────────────────
    
    public Mono<Product> getById(String id) {
        return repository.findById(id).switchIfEmpty(Mono.error(
                new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.NOT_FOUND, "Product not found: " + id)));
    }

    // ── 2. ดึง Product ทั้งหมด ───────────────────────────
    
    public Flux<Product> getAll() {
        return repository.findAll();
    }

    // ── 3. บันทึก Product ────────────────────────────────
    
    // Save only after the publisher is subscribed.
    public Mono<Product> save(Product product) {
        // Defer validation, ID generation and write until subscription.
        return Mono.defer(() -> {
            if (product == null || product.getName() == null || product.getName().isBlank()
                    || product.getCategory() == null || product.getCategory().isBlank()
                    || product.getPrice() == null || !Double.isFinite(product.getPrice())
                    || product.getPrice() < 0 || product.getStock() == null || product.getStock() < 0
                    || (product.getDiscountType() != null && !java.util.Set.of(
                            "NONE", "MEMBER", "SEASONAL").contains(product.getDiscountType()))) {
                return Mono.error(new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.BAD_REQUEST, "Invalid product fields"));
            }
            if (product.getId() == null || product.getId().isBlank())
                product.setId(java.util.UUID.randomUUID().toString());
            if (product.getDiscountType() == null) product.setDiscountType("NONE");
            return repository.save(product);
        });
    }

    // ── 4. ลบ Product ────────────────────────────────────
    
    public Mono<Void> delete(String id) {
        return getById(id).flatMap(p -> repository.deleteById(p.getId()));
    }

    // ── 5. กรองตาม category ──────────────────────────────
    
    public Flux<Product> getByCategory(String category) {
        return repository.findByCategory(category);
    }

    // ── 6. คำนวณราคาหลังส่วนลด ───────────────────────────
    
    // A single numeric result; callers can continue chaining operators.
    public Mono<Double> getDiscountedPrice(String id) {
        return getById(id).map(Product::getDiscountedPrice);
    }
}
