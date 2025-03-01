package com.suriyaprakhash.reactive.db;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table
@Data
public class Product {

    @Id
    private UUID id;
    private String name;
    private String description;
}
