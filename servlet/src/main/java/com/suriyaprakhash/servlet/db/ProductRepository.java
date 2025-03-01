package com.suriyaprakhash.servlet.db;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface ProductRepository extends PagingAndSortingRepository<Product, UUID>, CrudRepository<Product, UUID> {
}
