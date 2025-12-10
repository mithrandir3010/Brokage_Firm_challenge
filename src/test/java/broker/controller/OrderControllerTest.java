package broker.controller;

import broker.dto.request.CreateOrderRequest;
import broker.dto.response.OrderResponse;
import broker.model.Order;
import broker.model.OrderSide;
import broker.model.OrderStatus;
import broker.security.CustomUserDetails;
import broker.service.IOrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class OrderControllerTest {

    @Mock
    private IOrderService orderService;

    @InjectMocks
    private OrderController orderController;

    private CustomUserDetails customerUserDetails;
    private CustomUserDetails adminUserDetails;
    private Order sampleOrder;

    @BeforeEach
    void setUp() {
        // Create mock user details for customer
        customerUserDetails = mock(CustomUserDetails.class);
        when(customerUserDetails.getCustomerId()).thenReturn(1L);
        when(customerUserDetails.getRole()).thenReturn("CUSTOMER");

        // Create mock user details for admin
        adminUserDetails = mock(CustomUserDetails.class);
        when(adminUserDetails.getCustomerId()).thenReturn(2L);
        when(adminUserDetails.getRole()).thenReturn("ADMIN");

        // Create sample order
        sampleOrder = new Order();
        sampleOrder.setId(1L);
        sampleOrder.setCustomerId(1L);
        sampleOrder.setAssetName("AAPL");
        sampleOrder.setOrderSide(OrderSide.BUY);
        sampleOrder.setSize(new BigDecimal("10"));
        sampleOrder.setPrice(new BigDecimal("150"));
        sampleOrder.setStatus(OrderStatus.PENDING);
        sampleOrder.setCreateDate(LocalDateTime.now());
    }

    @Test
    @DisplayName("Create order should return OK with valid request")
    void createOrder_Success() {
        // Given
        CreateOrderRequest request = new CreateOrderRequest(
            1L, "AAPL", OrderSide.BUY, new BigDecimal("10"), new BigDecimal("150")
        );
        when(orderService.createOrder(any(), any(), any(), any(), any())).thenReturn(sampleOrder);

        // When
        ResponseEntity<OrderResponse> response = orderController.createOrder(request, customerUserDetails);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("AAPL", response.getBody().assetName());
        assertEquals(OrderStatus.PENDING, response.getBody().status());
    }

    @Test
    @DisplayName("Customer can only create order for themselves")
    void createOrder_CustomerCanOnlyCreateForSelf() {
        // Given - customer trying to create order for another customer
        CreateOrderRequest request = new CreateOrderRequest(
            999L, "AAPL", OrderSide.BUY, new BigDecimal("10"), new BigDecimal("150")
        );
        when(orderService.createOrder(eq(1L), any(), any(), any(), any())).thenReturn(sampleOrder);

        // When
        orderController.createOrder(request, customerUserDetails);

        // Then - should use customer's own ID (1L) not the requested ID (999L)
        verify(orderService).createOrder(eq(1L), any(), any(), any(), any());
    }

    @Test
    @DisplayName("Admin can create order for any customer")
    void createOrder_AdminCanCreateForAnyCustomer() {
        // Given
        CreateOrderRequest request = new CreateOrderRequest(
            1L, "AAPL", OrderSide.BUY, new BigDecimal("10"), new BigDecimal("150")
        );
        when(orderService.createOrder(eq(1L), any(), any(), any(), any())).thenReturn(sampleOrder);

        // When
        orderController.createOrder(request, adminUserDetails);

        // Then - should use the requested customer ID
        verify(orderService).createOrder(eq(1L), any(), any(), any(), any());
    }

    @Test
    @DisplayName("Get orders should return list for customer")
    void getOrders_Success() {
        // Given
        when(orderService.listOrders(eq(1L), any(), any()))
            .thenReturn(Arrays.asList(sampleOrder));

        // When
        ResponseEntity<List<OrderResponse>> response = orderController.getOrders(
            customerUserDetails, null, null, null
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
    }

    @Test
    @DisplayName("Admin can get orders for any customer")
    void getOrders_AdminCanViewAnyCustomer() {
        // Given
        when(orderService.listOrders(eq(5L), any(), any()))
            .thenReturn(Arrays.asList(sampleOrder));

        // When
        orderController.getOrders(adminUserDetails, 5L, null, null);

        // Then
        verify(orderService).listOrders(eq(5L), any(), any());
    }

    @Test
    @DisplayName("Cancel order should return cancelled order")
    void cancelOrder_Success() {
        // Given
        sampleOrder.setStatus(OrderStatus.CANCELED);
        when(orderService.cancelOrder(eq(1L), eq(1L))).thenReturn(sampleOrder);

        // When
        ResponseEntity<OrderResponse> response = orderController.cancelOrder(1L, customerUserDetails);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(OrderStatus.CANCELED, response.getBody().status());
    }

    @Test
    @DisplayName("Match order should return matched order")
    void matchOrder_Success() {
        // Given
        sampleOrder.setStatus(OrderStatus.MATCHED);
        when(orderService.matchOrder(1L)).thenReturn(sampleOrder);

        // When
        ResponseEntity<OrderResponse> response = orderController.matchOrder(1L);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(OrderStatus.MATCHED, response.getBody().status());
    }

    @Test
    @DisplayName("Get pending orders should return filtered list")
    void getPendingOrders_Success() {
        // Given
        when(orderService.listPendingOrders(1L)).thenReturn(Arrays.asList(sampleOrder));

        // When
        ResponseEntity<List<OrderResponse>> response = orderController.getPendingOrders(
            customerUserDetails, null
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(OrderStatus.PENDING, response.getBody().get(0).status());
    }
}
