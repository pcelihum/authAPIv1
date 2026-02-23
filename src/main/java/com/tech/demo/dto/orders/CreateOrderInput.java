package com.example.demo.dto.orders;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOrderInput {
    private Long userId;
    private List<OrderItemInput> items;
}