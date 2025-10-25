package com.joeymartinez.minierp.controller;

import com.joeymartinez.minierp.model.Order;
import com.joeymartinez.minierp.model.OrderRequest;
import com.joeymartinez.minierp.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/Orders")
    public List<Order> getAllOrders() {
        return orderService.getOrders();
    }

    @GetMapping("/order")
    public ResponseEntity<Order> getOrderById(@RequestParam Long id) {
        return orderService.getOrderById(id).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/order")
    public Order createOrder(@RequestBody OrderRequest orderRequest) {
        return orderService.createOrder(orderRequest);
    }
}
