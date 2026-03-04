package com.example.demo.resolver;

import com.example.demo.dto.orders.OrderItemInput;
import com.example.demo.model.Order;
import com.example.demo.model.OrderStatus;
import com.example.demo.service.OrderService;
import com.example.demo.service.UserService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class OrderResolver {

    private final OrderService orderService;
    private final UserService userService;

    public OrderResolver(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    // USER + ADMIN -> solo sus órdenes
    @QueryMapping
    public List<Order> myOrders() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Long userId = userService.getUserIdByUsername(username);
        return orderService.getOrdersByUserId(userId);
    }

    // SOLO ADMIN -> órdenes por userId
    @PreAuthorize("hasRole('ADMIN')")
    @QueryMapping
    public List<Order> ordersByUser(@Argument Long userId) {
        return orderService.getOrdersByUserId(userId);
    }

    // USER -> solo si es suya, ADMIN -> cualquiera
    @QueryMapping
    public Order orderById(@Argument Long id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Long currentUserId = userService.getUserIdByUsername(username);

        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        return orderService.getOrderByIdSecured(id, currentUserId, isAdmin);
    }

    // USER crea para sí mismo (no manda userId)
    @MutationMapping
    public Order createMyOrder(@Argument List<OrderItemInput> items) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Long userId = userService.getUserIdByUsername(username);
        return orderService.createOrder(userId, items);
    }

    // ADMIN crea orden para otro usuario (opcional)
    @PreAuthorize("hasRole('ADMIN')")
    @MutationMapping
    public Order createOrderForUser(@Argument Long userId, @Argument List<OrderItemInput> items) {
        return orderService.createOrder(userId, items);
    }

    // ADMIN actualiza estado
    @PreAuthorize("hasRole('ADMIN')")
    @MutationMapping
    public Order updateOrderStatus(@Argument Long id, @Argument OrderStatus status) {
        return orderService.updateOrderStatus(id, status);
    }
}