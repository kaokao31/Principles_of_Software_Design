package com.example.lab10.controller;
import com.example.lab10.client.ProductWebClient;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/client-demo")
public class ClientDemoController {
    private final ProductWebClient client;
    public ClientDemoController(ProductWebClient client) { this.client = client; }
    // Spring subscribes to this returned publisher; no manual subscribe in controllers.
    @GetMapping("/products/{id}/name")
    public Mono<String> name(@PathVariable String id) { return client.getNameOrDefault(id); }
}
