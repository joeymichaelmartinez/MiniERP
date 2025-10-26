package com.joeymartinez.minierp.service;

import com.joeymartinez.minierp.model.*;
import com.joeymartinez.minierp.repository.CustomerRepository;
import com.joeymartinez.minierp.repository.ProductRepository;

import com.joeymartinez.minierp.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;


    public OrderService(OrderRepository orderRepository, CustomerRepository customerRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
    }

    public List<Order> getOrders() {
        return orderRepository.findAll();
    }

    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    public Order createOrder(OrderRequest orderRequest) {
        Customer orderCustomer = customerRepository.findById(orderRequest.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        Order order = new Order();
        order.setCustomer(orderCustomer);

        List<OrderItem> orderItems = orderRequest.getItems().stream()
                .map(itemRequest -> {
                    System.out.println(itemRequest.getProductId());
                    Product product = productRepository.findById(itemRequest.getProductId())
                            .orElseThrow(() -> new RuntimeException("Product not found"));

                    OrderItem orderItem = new OrderItem();
                    orderItem.setProduct(product);
                    orderItem.setQuantity(itemRequest.getQuantity());
                    orderItem.setOrder(order);  // link back to order
                    return orderItem;
                })
                .toList();

        order.setCustomer(orderCustomer);
        order.setItems(orderItems);

        double totalPrice = 0;
        for(OrderItem orderItem: order.getItems()) {
            totalPrice += orderItem.getProduct().getPrice() * orderItem.getQuantity();
        }
        order.setTotalPrice(totalPrice);
        return orderRepository.save(order);
    }
}
