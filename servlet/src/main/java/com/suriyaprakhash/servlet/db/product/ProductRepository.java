package com.suriyaprakhash.servlet.db.product;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.UUID;

public interface ProductRepository extends PagingAndSortingRepository<Product, UUID>, CrudRepository<Product, UUID> {
}
