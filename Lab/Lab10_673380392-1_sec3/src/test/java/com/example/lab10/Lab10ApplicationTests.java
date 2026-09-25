package com.example.lab10;
import com.example.lab10.model.Product;
import com.example.lab10.repository.ProductRepository;
import com.example.lab10.service.ProductService;
import com.example.lab10.service.WalletService;
import org.junit.jupiter.api.*;
import reactor.test.StepVerifier;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class Lab10ApplicationTests {
    ProductRepository repository;
    ProductService service;
    @BeforeEach void setup() { repository = new ProductRepository(); service = new ProductService(repository); }
    Product sample() { return new Product(null, "Test", "Books", "Demo", 2, 100.0, "MEMBER"); }
    @Test void testFindById_found() {
        StepVerifier.create(repository.findById("1")).expectNextMatches(p -> p.getName().contains("673380392-1")).verifyComplete();
    }
    @Test void testFindById_notFound() { StepVerifier.create(repository.findById("999")).verifyComplete(); }
    @Test void testFindAll() { StepVerifier.create(repository.findAll()).expectNextCount(3).verifyComplete(); }
    @Test void testFindByCategory() { StepVerifier.create(repository.findByCategory("electronics")).expectNextCount(3).verifyComplete(); }
    @Test void emptyCategory() { StepVerifier.create(repository.findByCategory("Books")).verifyComplete(); }
    @Test void testSave() {
        Product p = sample();
        StepVerifier.create(service.save(p).flatMap(saved -> repository.findById(saved.getId())))
            .expectNextMatches(saved -> saved.getId() != null && saved.getName().equals("Test")).verifyComplete();
    }
    @Test void saveIsLazy() {
        Product p = sample(); p.setId("lazy"); var pending = repository.save(p);
        StepVerifier.create(repository.findById("lazy")).verifyComplete();
        StepVerifier.create(pending).expectNext(p).verifyComplete();
    }
    @Test void deleteIsLazy() {
        var pending = repository.deleteById("1");
        StepVerifier.create(repository.findById("1")).expectNextCount(1).verifyComplete();
        StepVerifier.create(pending).verifyComplete();
        StepVerifier.create(repository.findById("1")).verifyComplete();
    }
    @Test void discounts() {
        StepVerifier.create(service.getDiscountedPrice("1")).expectNext(35910.0).verifyComplete();
        StepVerifier.create(service.getDiscountedPrice("2")).expectNext(49900.0).verifyComplete();
        StepVerifier.create(service.getDiscountedPrice("3")).expectNext(23920.0).verifyComplete();
    }
    @Test void missingProduct() { StepVerifier.create(service.getById("999")).verifyError(org.springframework.web.server.ResponseStatusException.class); }
    @Test void invalidPrice() { Product p=sample(); p.setPrice(-1.0); StepVerifier.create(service.save(p)).verifyError(); }
    @Test void deposit() {
        WalletService w = new WalletService(); var pending=w.deposit(new BigDecimal("10.25"));
        StepVerifier.create(w.balance()).expectNext(new BigDecimal("0.00")).verifyComplete();
        StepVerifier.create(pending).expectNext(new BigDecimal("10.25")).verifyComplete();
        StepVerifier.create(w.deposit(new BigDecimal("0"))).verifyError();
        StepVerifier.create(w.deposit(new BigDecimal("1.001"))).verifyError();
        StepVerifier.create(w.balance()).expectNext(new BigDecimal("10.25")).verifyComplete();
    }
}
