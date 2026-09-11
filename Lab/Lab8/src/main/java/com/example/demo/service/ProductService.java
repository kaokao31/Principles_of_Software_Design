package com.example.demo.service;

import com.example.demo.model.Product;
import com.example.demo.model.ProductDetail;
import com.example.demo.model.Review;
import com.example.demo.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public Product saveNewProduct(Product product) {
        linkDetail(product);
        cleanAndLinkReviews(product);
        return productRepository.save(product);
    }

    public Product updateProduct(Long id, Product formProduct) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ไม่พบสินค้ารหัส " + id));

        existing.setName(formProduct.getName());
        existing.setCategory(formProduct.getCategory());
        existing.setBrand(formProduct.getBrand());
        existing.setStock(formProduct.getStock());
        existing.setPrice(formProduct.getPrice());
        existing.setDiscountType(formProduct.getDiscountType());

        ProductDetail incomingDetail = formProduct.getDetail();
        if (existing.getDetail() == null) {
            existing.setDetailWithBackReference(incomingDetail);
        } else if (incomingDetail != null) {
            ProductDetail currentDetail = existing.getDetail();
            currentDetail.setDescription(incomingDetail.getDescription());
            currentDetail.setWarranty(incomingDetail.getWarranty());
            currentDetail.setWeight(incomingDetail.getWeight());
            currentDetail.setDimensions(incomingDetail.getDimensions());
            currentDetail.setManufacturedCountry(incomingDetail.getManufacturedCountry());
        }

        return productRepository.save(existing);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }


    private void linkDetail(Product product) {
        ProductDetail detail = product.getDetail();
        if (detail != null) {
            product.setDetailWithBackReference(detail);
        }
    }

    private void cleanAndLinkReviews(Product product) {
        List<Review> reviews = product.getReviews();
        if (reviews == null) {
            return;
        }
        reviews.removeIf(this::isBlankReview);
        for (Review review : reviews) {
            if (review.getReviewDate() == null) {
                review.setReviewDate(LocalDate.now());
            }
            review.setProduct(product);
        }
    }

    private boolean isBlankReview(Review review) {
        return review == null
                || ((review.getReviewer() == null || review.getReviewer().isBlank())
                    && (review.getComment() == null || review.getComment().isBlank()));
    }
}
