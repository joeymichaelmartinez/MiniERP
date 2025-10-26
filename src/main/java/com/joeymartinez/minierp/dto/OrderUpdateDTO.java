package com.joeymartinez.minierp.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class OrderUpdateDTO {
    private List<ItemUpdateDTO> items;

    @Getter
    @Setter
    public static class ItemUpdateDTO {
        private Long orderItemId;
        private Long productId;
        private Long quantity;
    }
}
