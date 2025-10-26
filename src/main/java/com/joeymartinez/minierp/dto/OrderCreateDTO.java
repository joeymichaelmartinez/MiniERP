package com.joeymartinez.minierp.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderCreateDTO {

    private Long customerId;
    private List<ItemCreateDTO> items;

    @Getter
    @Setter
    public static class ItemCreateDTO {
        private Long productId;
        private Long quantity;
    }
}
