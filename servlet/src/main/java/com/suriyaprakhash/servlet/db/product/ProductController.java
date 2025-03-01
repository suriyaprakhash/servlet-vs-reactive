package com.suriyaprakhash.servlet.db.product;

import io.swagger.v3.oas.annotations.Parameter;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/db")
@RestController
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    @PageableAsQueryParam
    public ResponseEntity<PagedModel<Product>> findAll(@Parameter(hidden=true) Pageable pageable) {
        return ResponseEntity.ok(new PagedModel<>(productService.findAll(pageable)));
    }

    @PostMapping
    public ResponseEntity<Void> addProduct(Product product) {
        productService.addProduct(product);
        return  ResponseEntity.accepted().build();
    }
}

