package com.suriyaprakhash.reactive.db.product;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    public ResponseEntity<Flux<Product>> findAll() {
        return ResponseEntity.ok(productService.findAll());
    }

    public ResponseEntity<Void> addProduct(Product product) {
        productService.addProduct(product);
        return  ResponseEntity.accepted().build();
    }
}

