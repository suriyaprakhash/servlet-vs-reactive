package com.suriyaprakhash.reactive.db.product;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequestMapping("/db-nio")
@RestController
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<Flux<Product>> findAll() {
        return ResponseEntity.ok(productService.findAll());
    }

    // example where error will be consumed within the service
    @PostMapping
    public ResponseEntity<Void> addProduct(Product product) {
        productService.addProduct(product);
        return  ResponseEntity.accepted().build();
    }

    // example where error is thrown into the api handler
    @PutMapping
    public ResponseEntity<Mono<Product>> updateProduct(Product product) {
        return  ResponseEntity.accepted().body(productService.updateProduct(product));
    }
}

