package com.suriyaprakhash.reactive.db.product;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table
@Data
public class Product implements Persistable<UUID> {

    @Id
    private UUID id;
    private String name;
    private String shortDescription;
    private Double price;

    @Transient
    @JsonIgnore
    private boolean newProduct;

    @Transient
    @Override
    @JsonIgnore
    public boolean isNew() {
        return newProduct;
    }
}
