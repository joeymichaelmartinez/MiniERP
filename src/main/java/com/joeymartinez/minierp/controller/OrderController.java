package com.joeymartinez.minierp.controller;

import com.joeymartinez.minierp.dto.OrderStatusUpdateDTO;
import com.joeymartinez.minierp.dto.OrderUpdateDTO;
import com.joeymartinez.minierp.model.Order;
import com.joeymartinez.minierp.dto.OrderCreateDTO;
import com.joeymartinez.minierp.model.OrderStatus;
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

    @GetMapping("/orders")
    public List<Order> getAllOrders() {
        return orderService.getOrders();
    }

    @GetMapping("/order")
    public ResponseEntity<Order> getOrderById(@RequestParam Long id) {
        return orderService.getOrderById(id).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/order")
    public Order createOrder(@RequestBody OrderCreateDTO orderCreateDTO) {
        return orderService.createOrder(orderCreateDTO);
    }

    @PutMapping("/order/{id}")
    public ResponseEntity<Order> updateOrder(@PathVariable Long id, @RequestBody OrderUpdateDTO orderUpdateDTO) {
        Order updatedOrder = orderService.updateOrder(id, orderUpdateDTO);
        return ResponseEntity.ok(updatedOrder);
    }

    @DeleteMapping("/order/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/order/{id}/cancel")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long id) {
        orderService.cancelOrder(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/order/{id}/status")
    public ResponseEntity<Order> updateOrderStatus(
            @PathVariable Long id,
            @RequestBody OrderStatusUpdateDTO statusUpdateDTO
    ) {
        Order updatedOrder = orderService.updateOrderStatus(id, statusUpdateDTO.getStatus());
        return ResponseEntity.ok(updatedOrder);
    }



}
