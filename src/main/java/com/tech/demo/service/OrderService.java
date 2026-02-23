package com.example.demo.service;

import com.example.demo.dto.orders.CreateOrderInput;
import com.example.demo.dto.orders.OrderItemInput;
import com.example.demo.model.Order;
import com.example.demo.model.OrderItem;
import com.example.demo.model.Product;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public Order createOrder(CreateOrderInput input) {
        Order order = new Order();
        order.setUserId(input.getUserId());

        List<OrderItem> items = input.getItems().stream().map(itemInput -> {
            Product product = productRepository.findById(itemInput.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + itemInput.getProductId()));

            return OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(itemInput.getQuantity())
                    .price(product.getPrice().multiply(BigDecimal.valueOf(itemInput.getQuantity())))
                    .build();
        }).collect(Collectors.toList());

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
}