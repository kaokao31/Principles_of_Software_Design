package com.example.lab10.repository;

import com.example.lab10.model.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProductRepository {

    // ── In-memory storage ────────────────────────────────
    private final Map<String, Product> store = new ConcurrentHashMap<>();

    // ── Constructor: ใส่ข้อมูลตัวอย่าง ──────────────────
    public ProductRepository() {
        store.put("1", new Product("1", "iPhone 15 Pro (นายกานดิทัต นามสุดตา 673380392-1 SEC 3)",
                "Electronics", "Apple", 50, 39900.0, "MEMBER"));
        store.put("2", new Product("2", "MacBook Air M3",
                "Electronics", "Apple", 20, 49900.0, "NONE"));
        store.put("3", new Product("3", "Samsung Galaxy S24",
                "Electronics", "Samsung", 30, 29900.0, "SEASONAL"));
    }

    // ── 1. หา Product 1 รายการ ───────────────────────────
    
    // Read at subscription time; missing key completes without a value.
    public Mono<Product> findById(String id) {
        return Mono.defer(() -> Mono.justOrEmpty(store.get(id)));
    }

    // ── 2. หา Product ทั้งหมด ────────────────────────────
    
    // Publish current in-memory products lazily.
    public Flux<Product> findAll() {
        return Flux.defer(() -> Flux.fromIterable(store.values()));
    }

    // ── 3. บันทึก Product ────────────────────────────────
    
    // Save only after the publisher is subscribed.
    public Mono<Product> save(Product product) {
        return Mono.fromSupplier(() -> {
            store.put(product.getId(), product);
            return product;
        });
    }

    // ── 4. ลบ Product ────────────────────────────────────
    
    // Completion-only publisher for the deferred side effect.
    public Mono<Void> deleteById(String id) {
        return Mono.fromRunnable(() -> store.remove(id));
    }

    // ── 5. กรองตาม category ──────────────────────────────
    
    // filter keeps matching elements without collecting or blocking.
    public Flux<Product> findByCategory(String category) {
        return findAll().filter(p -> category.equalsIgnoreCase(p.getCategory()));
    }
}
