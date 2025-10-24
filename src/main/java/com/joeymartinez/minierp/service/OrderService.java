package com.joeymartinez.minierp.service;

import com.joeymartinez.minierp.model.Order;
import com.joeymartinez.minierp.model.OrderItem;
import com.joeymartinez.minierp.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    public Order createOrder(Order order) {
        double totalPrice = 0;
        for(OrderItem orderItem: order.getItems()) {
            totalPrice += orderItem.getProduct().getPrice() * orderItem.getQuantity();
        }
        order.setTotalPrice(totalPrice);
        return orderRepository.save(order);
    }
}
