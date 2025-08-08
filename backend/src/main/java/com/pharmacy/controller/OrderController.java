 

package com.pharmacy.controller;

import com.pharmacy.dto.OrderDto;
import com.pharmacy.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class OrderController {
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<List<OrderDto.OrderResponse>> getAllOrders() {
        logger.info("Fetching all orders");
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDto.OrderResponse> getOrderById(@PathVariable String id) {
        logger.info("Fetching order with id: {}", id);
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @GetMapping("/user")
    public ResponseEntity<List<OrderDto.OrderResponse>> getUserOrders() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserId = authentication.getName();
        
        logger.info("Fetching orders for user: {}", currentUserId);
        return ResponseEntity.ok(orderService.getOrdersByUser(currentUserId));
    }

    @PostMapping
    public ResponseEntity<OrderDto.OrderResponse> createOrder(@RequestBody OrderDto.OrderRequest request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentUserId = authentication.getName();
            
            logger.info("Creating new order for user: {}", currentUserId);
            OrderDto.OrderResponse response = orderService.createOrder(request, currentUserId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error creating order: {}", e.getMessage(), e);
            throw e;
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderDto.OrderResponse> updateOrderStatus(
            @PathVariable String id,
            @RequestBody OrderDto.OrderStatusUpdateRequest request) {
        logger.info("Updating status of order with id: {} to {}", id, request.getStatus());
        return ResponseEntity.ok(orderService.updateOrderStatus(id, request));
    }
}