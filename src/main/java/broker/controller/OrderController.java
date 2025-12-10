package broker.controller;

import broker.dto.request.CreateOrderRequest;
import broker.dto.response.OrderResponse;
import broker.model.Order;
import broker.security.CustomUserDetails;
import broker.service.IOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final IOrderService orderService;
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @Valid @RequestBody CreateOrderRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long customerId = request.customerId();
        if (!userDetails.getRole().equals("ADMIN") && !userDetails.getCustomerId().equals(customerId)) {
            customerId = userDetails.getCustomerId();
        }

        Order order = orderService.createOrder(
                customerId,
                request.assetName(),
                request.orderSide(),
                request.size(),
                request.price()
        );
        return ResponseEntity.ok(OrderResponse.fromEntity(order));
    }
    @DeleteMapping("/{orderId}")
    public ResponseEntity<OrderResponse> cancelOrder(
            @PathVariable Long orderId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long customerId = userDetails.getCustomerId();
        if (userDetails.getRole().equals("ADMIN")) {
            Order order = orderService.getOrderById(orderId);
            customerId = order.getCustomerId();
        }

        Order order = orderService.cancelOrder(orderId, customerId);
        return ResponseEntity.ok(OrderResponse.fromEntity(order));
    }
    @PostMapping("/{orderId}/match")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderResponse> matchOrder(@PathVariable Long orderId) {
        Order order = orderService.matchOrder(orderId);
        return ResponseEntity.ok(OrderResponse.fromEntity(order));
    }
    @GetMapping
    public ResponseEntity<List<OrderResponse>> getOrders(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        Long targetCustomerId;
        if (userDetails.getRole().equals("ADMIN") && customerId != null) {
            targetCustomerId = customerId;
        } else {
            targetCustomerId = userDetails.getCustomerId();
        }

        List<Order> orders = orderService.listOrders(targetCustomerId, startDate, endDate);
        List<OrderResponse> response = orders.stream()
                .map(OrderResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(response);
    }

  
    @GetMapping("/pending")
    public ResponseEntity<List<OrderResponse>> getPendingOrders(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) Long customerId) {

        Long targetCustomerId;
        if (userDetails.getRole().equals("ADMIN") && customerId != null) {
            targetCustomerId = customerId;
        } else {
            targetCustomerId = userDetails.getCustomerId();
        }

        List<Order> orders = orderService.listPendingOrders(targetCustomerId);
        List<OrderResponse> response = orders.stream()
                .map(OrderResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(response);
    }
}
