package broker.service;

import broker.exception.CustomerNotFoundException;
import broker.exception.InsufficientBalanceException;
import broker.exception.OrderNotCancellableException;
import broker.exception.OrderNotMatchableException;
import broker.model.Asset;
import broker.model.Order;
import broker.model.OrderSide;
import broker.model.OrderStatus;
import broker.repository.AssetRepository;
import broker.repository.CustomerRepository;
import broker.repository.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private AssetRepository assetRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private OrderService orderService;

    private static final Long CUSTOMER_ID = 1L;
    private static final String TRY_ASSET = "TRY";
    private static final String AAPL_ASSET = "AAPL";

    // Helper method for BigDecimal comparison
    private void assertBigDecimalEquals(String expected, BigDecimal actual) {
        assertEquals(0, new BigDecimal(expected).compareTo(actual), 
            "Expected " + expected + " but was " + actual);
    }

    @Nested
    @DisplayName("Create Order Tests")
    class CreateOrderTests {

        @Test
        @DisplayName("BUY order should succeed with sufficient TRY balance")
        void createBuyOrder_Success() {
            // Given
            Asset tryAsset = createAsset(CUSTOMER_ID, TRY_ASSET, new BigDecimal("10000"), new BigDecimal("10000"));
            
            when(customerRepository.existsById(CUSTOMER_ID)).thenReturn(true);
            when(assetRepository.findAssetForUpdate(CUSTOMER_ID, TRY_ASSET)).thenReturn(tryAsset);
            when(assetRepository.save(any(Asset.class))).thenReturn(tryAsset);
            when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
                Order order = invocation.getArgument(0);
                order.setId(1L);
                return order;
            });

            // When
            Order result = orderService.createOrder(
                CUSTOMER_ID, AAPL_ASSET, OrderSide.BUY,
                new BigDecimal("10"), new BigDecimal("150")
            );

            // Then
            assertNotNull(result);
            assertEquals(OrderStatus.PENDING, result.getStatus());
            assertEquals(AAPL_ASSET, result.getAssetName());
            assertEquals(OrderSide.BUY, result.getOrderSide());
            
            // Verify TRY usableSize was reduced (10 * 150 = 1500)
            assertBigDecimalEquals("8500", tryAsset.getUsableSize());
            
            verify(assetRepository).save(tryAsset);
            verify(orderRepository).save(any(Order.class));
        }

        @Test
        @DisplayName("BUY order should fail with insufficient TRY balance")
        void createBuyOrder_InsufficientBalance() {
            // Given
            Asset tryAsset = createAsset(CUSTOMER_ID, TRY_ASSET, new BigDecimal("100"), new BigDecimal("100"));
            
            when(customerRepository.existsById(CUSTOMER_ID)).thenReturn(true);
            when(assetRepository.findAssetForUpdate(CUSTOMER_ID, TRY_ASSET)).thenReturn(tryAsset);

            // When & Then
            assertThrows(InsufficientBalanceException.class, () ->
                orderService.createOrder(
                    CUSTOMER_ID, AAPL_ASSET, OrderSide.BUY,
                    new BigDecimal("10"), new BigDecimal("150")
                )
            );

            verify(orderRepository, never()).save(any(Order.class));
        }

        @Test
        @DisplayName("BUY order should fail when TRY asset does not exist")
        void createBuyOrder_NoTryAsset() {
            // Given
            when(customerRepository.existsById(CUSTOMER_ID)).thenReturn(true);
            when(assetRepository.findAssetForUpdate(CUSTOMER_ID, TRY_ASSET)).thenReturn(null);

            // When & Then
            assertThrows(InsufficientBalanceException.class, () ->
                orderService.createOrder(
                    CUSTOMER_ID, AAPL_ASSET, OrderSide.BUY,
                    new BigDecimal("10"), new BigDecimal("150")
                )
            );
        }

        @Test
        @DisplayName("SELL order should succeed with sufficient asset balance")
        void createSellOrder_Success() {
            // Given
            Asset aaplAsset = createAsset(CUSTOMER_ID, AAPL_ASSET, new BigDecimal("100"), new BigDecimal("100"));
            
            when(customerRepository.existsById(CUSTOMER_ID)).thenReturn(true);
            when(assetRepository.findAssetForUpdate(CUSTOMER_ID, AAPL_ASSET)).thenReturn(aaplAsset);
            when(assetRepository.save(any(Asset.class))).thenReturn(aaplAsset);
            when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
                Order order = invocation.getArgument(0);
                order.setId(1L);
                return order;
            });

            // When
            Order result = orderService.createOrder(
                CUSTOMER_ID, AAPL_ASSET, OrderSide.SELL,
                new BigDecimal("10"), new BigDecimal("150")
            );

            // Then
            assertNotNull(result);
            assertEquals(OrderStatus.PENDING, result.getStatus());
            assertEquals(OrderSide.SELL, result.getOrderSide());
            
            // Verify asset usableSize was reduced
            assertBigDecimalEquals("90", aaplAsset.getUsableSize());
        }

        @Test
        @DisplayName("SELL order should fail with insufficient asset balance")
        void createSellOrder_InsufficientBalance() {
            // Given
            Asset aaplAsset = createAsset(CUSTOMER_ID, AAPL_ASSET, new BigDecimal("5"), new BigDecimal("5"));
            
            when(customerRepository.existsById(CUSTOMER_ID)).thenReturn(true);
            when(assetRepository.findAssetForUpdate(CUSTOMER_ID, AAPL_ASSET)).thenReturn(aaplAsset);

            // When & Then
            assertThrows(InsufficientBalanceException.class, () ->
                orderService.createOrder(
                    CUSTOMER_ID, AAPL_ASSET, OrderSide.SELL,
                    new BigDecimal("10"), new BigDecimal("150")
                )
            );
        }

        @Test
        @DisplayName("Order should fail for non-existent customer")
        void createOrder_CustomerNotFound() {
            // Given
            when(customerRepository.existsById(CUSTOMER_ID)).thenReturn(false);

            // When & Then
            assertThrows(CustomerNotFoundException.class, () ->
                orderService.createOrder(
                    CUSTOMER_ID, AAPL_ASSET, OrderSide.BUY,
                    new BigDecimal("10"), new BigDecimal("150")
                )
            );
        }
    }

    @Nested
    @DisplayName("Cancel Order Tests")
    class CancelOrderTests {

        @Test
        @DisplayName("Cancel BUY order should restore TRY usableSize")
        void cancelBuyOrder_Success() {
            // Given
            Order order = createOrder(1L, CUSTOMER_ID, AAPL_ASSET, OrderSide.BUY, 
                new BigDecimal("10"), new BigDecimal("150"), OrderStatus.PENDING);
            Asset tryAsset = createAsset(CUSTOMER_ID, TRY_ASSET, new BigDecimal("10000"), new BigDecimal("8500"));
            
            when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
            when(assetRepository.findAssetForUpdate(CUSTOMER_ID, TRY_ASSET)).thenReturn(tryAsset);
            when(assetRepository.save(any(Asset.class))).thenReturn(tryAsset);
            when(orderRepository.save(any(Order.class))).thenReturn(order);

            // When
            Order result = orderService.cancelOrder(1L, CUSTOMER_ID);

            // Then
            assertEquals(OrderStatus.CANCELED, result.getStatus());
            // Verify TRY usableSize was restored (8500 + 1500 = 10000)
            assertBigDecimalEquals("10000", tryAsset.getUsableSize());
        }

        @Test
        @DisplayName("Cancel SELL order should restore asset usableSize")
        void cancelSellOrder_Success() {
            // Given
            Order order = createOrder(1L, CUSTOMER_ID, AAPL_ASSET, OrderSide.SELL,
                new BigDecimal("10"), new BigDecimal("150"), OrderStatus.PENDING);
            Asset aaplAsset = createAsset(CUSTOMER_ID, AAPL_ASSET, new BigDecimal("100"), new BigDecimal("90"));
            
            when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
            when(assetRepository.findAssetForUpdate(CUSTOMER_ID, AAPL_ASSET)).thenReturn(aaplAsset);
            when(assetRepository.save(any(Asset.class))).thenReturn(aaplAsset);
            when(orderRepository.save(any(Order.class))).thenReturn(order);

            // When
            Order result = orderService.cancelOrder(1L, CUSTOMER_ID);

            // Then
            assertEquals(OrderStatus.CANCELED, result.getStatus());
            assertBigDecimalEquals("100", aaplAsset.getUsableSize());
        }

        @Test
        @DisplayName("Cancel should fail for MATCHED order")
        void cancelOrder_AlreadyMatched() {
            // Given
            Order order = createOrder(1L, CUSTOMER_ID, AAPL_ASSET, OrderSide.BUY,
                new BigDecimal("10"), new BigDecimal("150"), OrderStatus.MATCHED);
            
            when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

            // When & Then
            assertThrows(OrderNotCancellableException.class, () ->
                orderService.cancelOrder(1L, CUSTOMER_ID)
            );
        }

        @Test
        @DisplayName("Cancel should fail for CANCELED order")
        void cancelOrder_AlreadyCanceled() {
            // Given
            Order order = createOrder(1L, CUSTOMER_ID, AAPL_ASSET, OrderSide.BUY,
                new BigDecimal("10"), new BigDecimal("150"), OrderStatus.CANCELED);
            
            when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

            // When & Then
            assertThrows(OrderNotCancellableException.class, () ->
                orderService.cancelOrder(1L, CUSTOMER_ID)
            );
        }
    }

    @Nested
    @DisplayName("Match Order Tests")
    class MatchOrderTests {

        @Test
        @DisplayName("Match BUY order should transfer TRY to asset")
        void matchBuyOrder_Success() {
            // Given
            Order order = createOrder(1L, CUSTOMER_ID, AAPL_ASSET, OrderSide.BUY,
                new BigDecimal("10"), new BigDecimal("150"), OrderStatus.PENDING);
            Asset tryAsset = createAsset(CUSTOMER_ID, TRY_ASSET, new BigDecimal("10000"), new BigDecimal("8500"));
            
            when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
            when(assetRepository.findAssetForUpdate(CUSTOMER_ID, TRY_ASSET)).thenReturn(tryAsset);
            when(assetRepository.findAssetForUpdate(CUSTOMER_ID, AAPL_ASSET)).thenReturn(null);
            when(assetRepository.save(any(Asset.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(orderRepository.save(any(Order.class))).thenReturn(order);

            // When
            Order result = orderService.matchOrder(1L);

            // Then
            assertEquals(OrderStatus.MATCHED, result.getStatus());
            // TRY size should decrease by 1500
            assertBigDecimalEquals("8500", tryAsset.getSize());
            
            verify(assetRepository, times(2)).save(any(Asset.class));
        }

        @Test
        @DisplayName("Match SELL order should transfer asset to TRY")
        void matchSellOrder_Success() {
            // Given
            Order order = createOrder(1L, CUSTOMER_ID, AAPL_ASSET, OrderSide.SELL,
                new BigDecimal("10"), new BigDecimal("150"), OrderStatus.PENDING);
            Asset aaplAsset = createAsset(CUSTOMER_ID, AAPL_ASSET, new BigDecimal("100"), new BigDecimal("90"));
            Asset tryAsset = createAsset(CUSTOMER_ID, TRY_ASSET, new BigDecimal("5000"), new BigDecimal("5000"));
            
            when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
            when(assetRepository.findAssetForUpdate(CUSTOMER_ID, AAPL_ASSET)).thenReturn(aaplAsset);
            when(assetRepository.findAssetForUpdate(CUSTOMER_ID, TRY_ASSET)).thenReturn(tryAsset);
            when(assetRepository.save(any(Asset.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(orderRepository.save(any(Order.class))).thenReturn(order);

            // When
            Order result = orderService.matchOrder(1L);

            // Then
            assertEquals(OrderStatus.MATCHED, result.getStatus());
            // Asset size should decrease
            assertBigDecimalEquals("90", aaplAsset.getSize());
            // TRY should increase by 1500
            assertBigDecimalEquals("6500", tryAsset.getSize());
        }

        @Test
        @DisplayName("Match should fail for non-PENDING order")
        void matchOrder_NotPending() {
            // Given
            Order order = createOrder(1L, CUSTOMER_ID, AAPL_ASSET, OrderSide.BUY,
                new BigDecimal("10"), new BigDecimal("150"), OrderStatus.MATCHED);
            
            when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

            // When & Then
            assertThrows(OrderNotMatchableException.class, () ->
                orderService.matchOrder(1L)
            );
        }
    }

    // Helper methods
    private Asset createAsset(Long customerId, String assetName, BigDecimal size, BigDecimal usableSize) {
        Asset asset = new Asset();
        asset.setId(1L);
        asset.setCustomerId(customerId);
        asset.setAssetName(assetName);
        asset.setSize(size);
        asset.setUsableSize(usableSize);
        return asset;
    }

    private Order createOrder(Long id, Long customerId, String assetName, OrderSide side,
                              BigDecimal size, BigDecimal price, OrderStatus status) {
        Order order = new Order();
        order.setId(id);
        order.setCustomerId(customerId);
        order.setAssetName(assetName);
        order.setOrderSide(side);
        order.setSize(size);
        order.setPrice(price);
        order.setStatus(status);
        return order;
    }
}
