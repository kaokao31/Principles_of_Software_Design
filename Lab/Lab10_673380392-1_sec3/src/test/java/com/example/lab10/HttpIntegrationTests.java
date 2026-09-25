package com.example.lab10;

import com.example.lab10.client.ProductWebClient;
import com.example.lab10.model.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HttpIntegrationTests {
    @Autowired WebTestClient http;
    @LocalServerPort int port;
    @Test void productLifecycleThroughRealHttp() {
        ProductWebClient client = new ProductWebClient("http://localhost:" + port);
        Product p = new Product("http-test", "HTTP test", "Testing", "Lab", 1, 200.0, "SEASONAL");
        StepVerifier.create(client.createProduct(p)
                .flatMap(saved -> client.getProductById(saved.getId())))
            .expectNextMatches(saved -> saved.getName().equals("HTTP test")).verifyComplete();
        StepVerifier.create(client.getByCategory("Testing")).expectNextMatches(x -> x.getId().equals("http-test")).verifyComplete();
        StepVerifier.create(client.getAllProducts().filter(x -> x.getId().equals("http-test"))).expectNextCount(1).verifyComplete();
        StepVerifier.create(client.getDiscountedPrice("http-test")).expectNext(160.0).verifyComplete();
        StepVerifier.create(client.deleteProduct("http-test")).verifyComplete();
        StepVerifier.create(client.getNameOrDefault("http-test")).expectNext("Product not found").verifyComplete();
    }
    @Test void statusAndValidation() {
        http.get().uri("/products/missing").exchange().expectStatus().isNotFound();
        http.delete().uri("/products/missing").exchange().expectStatus().isNotFound();
        http.post().uri("/products").header("Content-Type", "application/json")
            .bodyValue("{\"name\":\"Bad\",\"category\":\"Test\",\"price\":-1,\"stock\":1}")
            .exchange().expectStatus().isBadRequest();
        http.get().uri("/products/category/NoSuchCategory").exchange().expectStatus().isOk().expectBody().json("[]");
        http.post().uri("/wallet/deposits").header("Content-Type", "application/json")
            .bodyValue("{\"amount\":25.50}").exchange().expectStatus().isOk().expectBody().jsonPath("$.balance").isEqualTo(25.50);
        http.post().uri("/wallet/deposits").header("Content-Type", "application/json")
            .bodyValue("{\"amount\":-1}").exchange().expectStatus().isBadRequest();
    }
}
