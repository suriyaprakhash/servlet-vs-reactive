package com.suriyaprakhash.reactive.db;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface ProductRepository extends ReactiveCrudRepository<Product, UUID> {
}
