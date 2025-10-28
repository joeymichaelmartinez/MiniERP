package com.joeymartinez.minierp.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductUpdateDTO {
    private String name;
    private Double price;
    private Long stock;
}
