package com.example.lab10.service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.atomic.AtomicReference;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Service
public class WalletService {
    private final AtomicReference<BigDecimal> balance = new AtomicReference<>(new BigDecimal("0.00"));
    public Mono<BigDecimal> balance() { return Mono.fromSupplier(balance::get); }
    public Mono<BigDecimal> deposit(BigDecimal amount) {
        return Mono.defer(() -> {
            if (amount == null || amount.signum() <= 0)
                return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Amount must be positive"));
            final BigDecimal normalized;
            try { normalized = amount.setScale(2, RoundingMode.UNNECESSARY); }
            catch (ArithmeticException ex) {
                return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "At most 2 decimal places"));
            }
            return Mono.fromSupplier(() -> balance.updateAndGet(old -> old.add(normalized)));
        });
    }
}
