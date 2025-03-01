package com.suriyaprakhash.reactive.db.product;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;
import java.util.UUID;

@Slf4j
@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Flux<Product> findAll() {
        return productRepository.findAll();
    }

    public void addProduct(Product product) {
        product.setNewProduct(true);
        product.setId(UUID.randomUUID());
        productRepository.save(product)
//                .log("about to add product")
                .subscribe(savedProduct -> {
            log.info("Product saved: " + savedProduct);
        });
    }

    public Mono<Product> updateProduct(Product product) {
        product.setNewProduct(false);
        return productRepository.existsById(product.getId())
//                .log("about to update product")
                .flatMap(exists -> {
                    if (!exists) {
                        throw new NoSuchElementException("Product not found");
                    }
                    return productRepository.save(product);
                });
    }

}
