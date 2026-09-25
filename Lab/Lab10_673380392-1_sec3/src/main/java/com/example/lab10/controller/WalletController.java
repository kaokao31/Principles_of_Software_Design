package com.example.lab10.controller;
import java.math.BigDecimal;
import com.example.lab10.service.WalletService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/wallet")
public class WalletController {
    public record Deposit(BigDecimal amount) {}
    public record Balance(BigDecimal balance) {}
    private final WalletService service;
    public WalletController(WalletService service) { this.service = service; }
    @GetMapping
    public Mono<Balance> balance() { return service.balance().map(Balance::new); }
    @PostMapping("/deposits")
    public Mono<Balance> deposit(@RequestBody Mono<Deposit> request) {
        return request.flatMap(d -> service.deposit(d.amount())).map(Balance::new);
    }
}
