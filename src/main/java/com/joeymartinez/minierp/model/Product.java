package com.joeymartinez.minierp.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "products")
public class Product extends BaseEntity {
    private String name;
    private double price;
    private Long stock;

    public Product() {}

    public Product(String name, double price, Long stock) {
        this.name = name;
        this.price = price;
        this.stock = stock;
    }
}
