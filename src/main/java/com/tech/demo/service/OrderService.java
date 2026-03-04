package com.example.demo.service;

import com.example.demo.dto.orders.OrderItemInput;
import com.example.demo.model.*;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.ProductRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public Order createOrder(Long userId, List<OrderItemInput> itemsInput) {
        if (itemsInput == null || itemsInput.isEmpty()) {
            throw new IllegalArgumentException("Order must have at least 1 item");
        }

        Order order = new Order();
        order.setUserId(userId);

        List<OrderItem> items = itemsInput.stream().map(itemInput -> {
            Product product = productRepository.findById(itemInput.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Product not found with id: " + itemInput.getProductId()));

            return OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(itemInput.getQuantity())
                    // tu campo price es subtotal (unit * qty)
                    .price(product.getPrice().multiply(BigDecimal.valueOf(itemInput.getQuantity())))
                    .build();
        }).toList();

        BigDecimal totalAmount = items.stream()
                .map(OrderItem::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setItems(items);
        order.setTotalAmount(totalAmount);

        return orderRepository.save(order);
    }

    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with id: " + id));
    }

    public Order getOrderByIdSecured(Long orderId, Long currentUserId, boolean isAdmin) {
        Order order = getOrderById(orderId);

        if (!isAdmin && !order.getUserId().equals(currentUserId)) {
            throw new AccessDeniedException("Forbidden");
        }

        return order;
    }

    @Transactional
    public Order updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = getOrderById(orderId);
        order.setStatus(status);
        return orderRepository.save(order);
    }
}