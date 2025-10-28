package com.joeymartinez.minierp.service;

import com.joeymartinez.minierp.dto.OrderCreateDTO;
import com.joeymartinez.minierp.dto.OrderUpdateDTO;
import com.joeymartinez.minierp.model.*;
import com.joeymartinez.minierp.repository.CustomerRepository;
import com.joeymartinez.minierp.repository.ProductRepository;

import com.joeymartinez.minierp.repository.OrderRepository;
import jakarta.transaction.Transactional;
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

    @Transactional
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

    @Transactional
    public Order updateOrder(Long orderId, OrderUpdateDTO orderUpdateDTO) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("No order found with id: " + orderId));

        ensureOrderIsEditable(order);

        List<OrderItem> orderItems = order.getItems();

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

        for (OrderUpdateDTO.ItemUpdateDTO itemDTO : orderUpdateDTO.getItems()) {
            Product product = productRepository.findById(itemDTO.getProductId()).get();
            OrderItem existingItem = orderItems.stream()
                    .filter(oi -> Objects.equals(oi.getProduct().getId(), itemDTO.getProductId()))
                    .findFirst()
                    .orElse(null);

            long newQuantity = itemDTO.getQuantity();

            if (existingItem == null && newQuantity > 0) {
                OrderItem newItem = new OrderItem();
                newItem.setProduct(product);
                newItem.setQuantity(newQuantity);
                newItem.setSubtotal(product.getPrice() * newQuantity);
                newItem.setOrder(order);
                orderItems.add(newItem);
                adjustProductStock(product, newQuantity);
            } else if (existingItem != null) {
                long diff = newQuantity - existingItem.getQuantity();

                if (newQuantity > 0) {
                    existingItem.setQuantity(newQuantity);
                    existingItem.setSubtotal(product.getPrice() * newQuantity);
                    adjustProductStock(product, diff);
                } else {
                    orderItems.remove(existingItem);
                    adjustProductStock(product, -existingItem.getQuantity());
                }
            }
        }

        order.setItems(orderItems);
        order.setTotalPrice(orderItems.stream().mapToDouble(OrderItem::getSubtotal).sum());

        return orderRepository.save(order);
    }

    @Transactional
    public void deleteOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        ensureOrderIsEditable(order);

        orderRepository.delete(order);
    }

    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        ensureOrderIsEditable(order);

        for (OrderItem item : order.getItems()) {
            adjustProductStock(item.getProduct(), -item.getQuantity());
        }

        order.setStatus(OrderStatus.CANCELED);
        orderRepository.save(order);
    }

    public Order updateOrderStatus(Long id, OrderStatus newStatus) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        OrderStatus currentStatus = order.getStatus();

        if (newStatus == currentStatus) {
            return order;
        }

        switch (currentStatus) {
            case NEW -> {
                if (newStatus == OrderStatus.PROCESSING) {
                    for (OrderItem item : order.getItems()) {
                        adjustProductStock(item.getProduct(), item.getQuantity());
                    }
                } else if (newStatus == OrderStatus.CANCELED) {

                } else {
                    throw new RuntimeException("Invalid transition from NEW to " + newStatus);
                }
            }
            case PROCESSING -> {
                if (newStatus == OrderStatus.CANCELED) {
                    for (OrderItem item : order.getItems()) {
                        adjustProductStock(item.getProduct(), -item.getQuantity());
                    }
                } else if (newStatus != OrderStatus.COMPLETE) {
                    throw new RuntimeException("Invalid transition from PROCESSING to " + newStatus);
                }
            }
            case COMPLETE, CANCELED -> {
                throw new RuntimeException("Cannot change status of a completed or canceled order");
            }
        }

        order.setStatus(newStatus);
        order.setUpdatedAt(new java.sql.Timestamp(System.currentTimeMillis()));
        return orderRepository.save(order);
    }

    private void ensureOrderIsEditable(Order order) {
        if (order.getStatus() != OrderStatus.NEW) {
            throw new RuntimeException("Order cannot be modified in status: " + order.getStatus());
        }
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
