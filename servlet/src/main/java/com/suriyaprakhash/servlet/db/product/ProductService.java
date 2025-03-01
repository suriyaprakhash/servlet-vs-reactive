package com.suriyaprakhash.servlet.db.product;

import org.hibernate.annotations.NotFound;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;


@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Page<Product> findAll(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    public void addProduct(Product product) {
        product.setId(null);
        productRepository.save(product);
    }

    public void updateProduct(Product product) {
        if (!productRepository.existsById(product.getId())) {
            throw new NoSuchElementException("Product not found");
        }
        productRepository.save(product);
    }

}
