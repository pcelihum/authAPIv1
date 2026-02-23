package com.example.demo.resolver;

import com.example.demo.dto.orders.CreateOrderInput;
import com.example.demo.model.Order;
import com.example.demo.service.OrderService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class OrderResolver {

    private final OrderService orderService;

    public OrderResolver(OrderService orderService) {
        this.orderService = orderService;
    }

    @MutationMapping
    public Order createOrder(@Argument CreateOrderInput input) {
        return orderService.createOrder(input);
    }

    @QueryMapping
    public List<Order> ordersByUser(@Argument Long userId) {
        return orderService.getOrdersByUserId(userId);
    }

    @QueryMapping
    public Order orderById(@Argument Long id) {
        return orderService.getOrderById(id);
    }
}