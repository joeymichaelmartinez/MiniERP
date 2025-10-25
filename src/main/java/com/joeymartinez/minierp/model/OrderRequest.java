package com.joeymartinez.minierp.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderRequest {

    private Long customerId;
    private List<ItemRequest> items;

    @Getter
    @Setter
    public static class ItemRequest {
        private Long productId;
        private Long quantity;
    }
}
