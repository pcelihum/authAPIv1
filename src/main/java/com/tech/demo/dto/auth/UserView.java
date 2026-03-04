package com.example.demo.dto;

import com.example.demo.model.User;

public record UserView(Long id, String username, String role) {
    public static UserView from(User u) {
        return new UserView(u.getId(), u.getUsername(), u.getRole().name());
    }
}