package com.example.lab10.client;

import com.example.lab10.model.Product;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class ProductWebClient {

    // HTTP client targeting this application by default.
    private final WebClient client;

    public ProductWebClient(@org.springframework.beans.factory.annotation.Value(
            "${product.api.base-url:http://localhost:${server.port:8080}}") String baseUrl) {
        this.client = WebClient.builder().baseUrl(baseUrl).build();
    }

    // HTTP 404 is an error, not an empty Mono. Convert only 404 before defaultIfEmpty.
    public Mono<String> getNameOrDefault(String id) {
        return getProductById(id).map(Product::getName)
            .onErrorResume(org.springframework.web.reactive.function.client.WebClientResponseException.NotFound.class,
                error -> Mono.empty())
            .defaultIfEmpty("Product not found");
    }

    public Mono<Product> getProductById(String id) {
        return client.get()
                .uri("/products/{id}", id)
                .retrieve()
                .bodyToMono(Product.class);
    }

    public Flux<Product> getAllProducts() {
        return client.get().uri("/products").retrieve().bodyToFlux(Product.class);
    }

    public Mono<Product> createProduct(Product product) {
        return client.post().uri("/products").bodyValue(product)
                .retrieve().bodyToMono(Product.class);
    }

    public Mono<Void> deleteProduct(String id) {
        return client.delete().uri("/products/{id}", id).retrieve().bodyToMono(Void.class);
    }

    public Flux<Product> getByCategory(String category) {
        return client.get().uri("/products/category/{category}", category)
                .retrieve().bodyToFlux(Product.class);
    }

    // A single numeric result; callers can continue chaining operators.
    public Mono<Double> getDiscountedPrice(String id) {
        return client.get().uri("/products/{id}/price", id).retrieve()
                .bodyToMono(Double.class); // Caller may chain operators without blocking.
    }
}
