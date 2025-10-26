package com.joeymartinez.minierp.service;

import com.joeymartinez.minierp.dto.OrderCreateDTO;
import com.joeymartinez.minierp.dto.OrderUpdateDTO;
import com.joeymartinez.minierp.model.*;
import com.joeymartinez.minierp.repository.CustomerRepository;
import com.joeymartinez.minierp.repository.ProductRepository;

import com.joeymartinez.minierp.repository.OrderRepository;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository,
                        CustomerRepository customerRepository,
                        ProductRepository productRepository) {
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

    public Order createOrder(OrderCreateDTO orderCreateDTO) {
        Customer customer = customerRepository.findById(orderCreateDTO.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        for (OrderCreateDTO.ItemCreateDTO itemDTO : orderCreateDTO.getItems()) {
            Product product = productRepository.findById(itemDTO.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            if (itemDTO.getQuantity() > product.getStock()) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName());
            }
        }

        Order order = new Order();
        order.setCustomer(customer);

        List<OrderItem> orderItems = orderCreateDTO.getItems().stream()
                .map(itemDTO -> {
                    Product product = productRepository.findById(itemDTO.getProductId()).get();
                    adjustProductStock(product, itemDTO.getQuantity());

                    OrderItem item = new OrderItem();
                    item.setProduct(product);
                    item.setQuantity(itemDTO.getQuantity());
                    item.setSubtotal(product.getPrice() * itemDTO.getQuantity());
                    item.setOrder(order);
                    return item;
                })
                .toList();

        order.setItems(orderItems);
        order.setTotalPrice(orderItems.stream().mapToDouble(OrderItem::getSubtotal).sum());

        return orderRepository.save(order);
    }

    public Order updateOrder(Long orderId, OrderUpdateDTO orderUpdateDTO) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("No order found with id: " + orderId));

        List<OrderItem> orderItems = order.getItems();

        // Pre-check stock for all updates
        for (OrderUpdateDTO.ItemUpdateDTO itemDTO : orderUpdateDTO.getItems()) {
            Product product = productRepository.findById(itemDTO.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + itemDTO.getProductId()));

            OrderItem existingItem = orderItems.stream()
                    .filter(oi -> Objects.equals(oi.getProduct().getId(), itemDTO.getProductId()))
                    .findFirst()
                    .orElse(null);

            long currentQuantity = existingItem != null ? existingItem.getQuantity() : 0;
            long diff = itemDTO.getQuantity() - currentQuantity;

            if (diff > product.getStock()) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName());
            }
        }

        // Apply updates
        for (OrderUpdateDTO.ItemUpdateDTO itemDTO : orderUpdateDTO.getItems()) {
            Product product = productRepository.findById(itemDTO.getProductId()).get();
            OrderItem existingItem = orderItems.stream()
                    .filter(oi -> Objects.equals(oi.getProduct().getId(), itemDTO.getProductId()))
                    .findFirst()
                    .orElse(null);

            if (existingItem == null && itemDTO.getQuantity() > 0) {
                // New item
                OrderItem newItem = new OrderItem();
                newItem.setProduct(product);
                newItem.setQuantity(itemDTO.getQuantity());
                newItem.setSubtotal(product.getPrice() * itemDTO.getQuantity());
                newItem.setOrder(order);
                orderItems.add(newItem);
                adjustProductStock(product, itemDTO.getQuantity());
            } else if (existingItem != null) {
                long diff = itemDTO.getQuantity() - existingItem.getQuantity();

                if (itemDTO.getQuantity() > 0) {
                    existingItem.setQuantity(itemDTO.getQuantity());
                    existingItem.setSubtotal(product.getPrice() * itemDTO.getQuantity());
                    adjustProductStock(product, diff);
                } else {
                    // Remove item and restore stock
                    orderItems.remove(existingItem);
                    adjustProductStock(product, -existingItem.getQuantity());
                }
            }
        }

        order.setItems(orderItems);
        order.setTotalPrice(orderItems.stream().mapToDouble(OrderItem::getSubtotal).sum());

        return orderRepository.save(order);
    }


    public void deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new RuntimeException("No order found with id: " + id);
        }
        orderRepository.deleteById(id);
    }

    private void adjustProductStock(Product product, long quantityChange) {
        long newStock = product.getStock() - quantityChange;
        if (newStock < 0) {
            throw new RuntimeException("Insufficient stock for product: " + product.getName());
        }
        product.setStock(newStock);
        productRepository.save(product);
    }
}
