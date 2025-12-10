package broker.integration;

import broker.model.Asset;
import broker.model.Order;
import broker.model.OrderSide;
import broker.model.OrderStatus;
import broker.repository.AssetRepository;
import broker.repository.CustomerRepository;
import broker.repository.OrderRepository;
import broker.service.AssetService;
import broker.service.CustomerService;
import broker.service.OrderService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BrokageIntegrationTest {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private AssetService assetService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private OrderRepository orderRepository;

    private Long customerId;

    @BeforeAll
    void setup() {
        // Clean up
        orderRepository.deleteAll();
        assetRepository.deleteAll();
        customerRepository.deleteAll();

        // Create test customer
        var customer = customerService.createCustomer(
            "Integration Test User",
            "integrationuser",
            "password123",
            "integration@test.com",
            "CUSTOMER"
        );
        customerId = customer.getId();
    }

    @AfterAll
    void cleanup() {
        orderRepository.deleteAll();
        assetRepository.deleteAll();
        customerRepository.deleteAll();
    }

    @Test
    @org.junit.jupiter.api.Order(1)
    @DisplayName("Step 1: Deposit TRY to customer account")
    void step1_DepositMoney() {
        // When
        Asset result = assetService.depositMoney(customerId, new BigDecimal("10000"));

        // Then
        assertNotNull(result);
        assertEquals("TRY", result.getAssetName());
        assertEquals(0, new BigDecimal("10000").compareTo(result.getSize()));
        assertEquals(0, new BigDecimal("10000").compareTo(result.getUsableSize()));
    }

    @Test
    @org.junit.jupiter.api.Order(2)
    @DisplayName("Step 2: Create BUY order")
    void step2_CreateBuyOrder() {
        // When
        Order order = orderService.createOrder(
            customerId,
            "AAPL",
            OrderSide.BUY,
            new BigDecimal("10"),
            new BigDecimal("150")
        );

        // Then
        assertNotNull(order);
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals("AAPL", order.getAssetName());

        // Verify TRY was blocked (10 * 150 = 1500)
        Asset tryAsset = assetRepository.findByCustomerIdAndAssetName(customerId, "TRY");
        assertEquals(0, new BigDecimal("10000").compareTo(tryAsset.getSize()));
        assertEquals(0, new BigDecimal("8500").compareTo(tryAsset.getUsableSize()));
    }

    @Test
    @org.junit.jupiter.api.Order(3)
    @DisplayName("Step 3: Match the BUY order")
    void step3_MatchOrder() {
        // Given
        List<Order> pendingOrders = orderService.listPendingOrders(customerId);
        assertFalse(pendingOrders.isEmpty());
        Long orderId = pendingOrders.get(0).getId();

        // When
        Order matchedOrder = orderService.matchOrder(orderId);

        // Then
        assertEquals(OrderStatus.MATCHED, matchedOrder.getStatus());

        // Verify TRY decreased
        Asset tryAsset = assetRepository.findByCustomerIdAndAssetName(customerId, "TRY");
        assertEquals(0, new BigDecimal("8500").compareTo(tryAsset.getSize()));
        assertEquals(0, new BigDecimal("8500").compareTo(tryAsset.getUsableSize()));

        // Verify AAPL was added
        Asset aaplAsset = assetRepository.findByCustomerIdAndAssetName(customerId, "AAPL");
        assertNotNull(aaplAsset);
        assertEquals(0, new BigDecimal("10").compareTo(aaplAsset.getSize()));
        assertEquals(0, new BigDecimal("10").compareTo(aaplAsset.getUsableSize()));
    }

    @Test
    @org.junit.jupiter.api.Order(4)
    @DisplayName("Step 4: Create SELL order")
    void step4_CreateSellOrder() {
        // When
        Order order = orderService.createOrder(
            customerId,
            "AAPL",
            OrderSide.SELL,
            new BigDecimal("5"),
            new BigDecimal("160")
        );

        // Then
        assertNotNull(order);
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(OrderSide.SELL, order.getOrderSide());

        // Verify AAPL usableSize was blocked
        Asset aaplAsset = assetRepository.findByCustomerIdAndAssetName(customerId, "AAPL");
        assertEquals(0, new BigDecimal("10").compareTo(aaplAsset.getSize()));
        assertEquals(0, new BigDecimal("5").compareTo(aaplAsset.getUsableSize()));
    }

    @Test
    @org.junit.jupiter.api.Order(5)
    @DisplayName("Step 5: Cancel SELL order")
    void step5_CancelOrder() {
        // Given
        List<Order> pendingOrders = orderService.listPendingOrders(customerId);
        assertFalse(pendingOrders.isEmpty());
        Long orderId = pendingOrders.get(0).getId();

        // When
        Order cancelledOrder = orderService.cancelOrder(orderId, customerId);

        // Then
        assertEquals(OrderStatus.CANCELED, cancelledOrder.getStatus());

        // Verify AAPL usableSize was restored
        Asset aaplAsset = assetRepository.findByCustomerIdAndAssetName(customerId, "AAPL");
        assertEquals(0, new BigDecimal("10").compareTo(aaplAsset.getSize()));
        assertEquals(0, new BigDecimal("10").compareTo(aaplAsset.getUsableSize()));
    }

    @Test
    @org.junit.jupiter.api.Order(6)
    @DisplayName("Step 6: Withdraw TRY")
    void step6_WithdrawMoney() {
        // When
        Asset result = assetService.withdrawMoney(customerId, new BigDecimal("1000"), "TR123456789");

        // Then
        assertNotNull(result);
        assertEquals(0, new BigDecimal("7500").compareTo(result.getSize()));
        assertEquals(0, new BigDecimal("7500").compareTo(result.getUsableSize()));
    }

    @Test
    @org.junit.jupiter.api.Order(7)
    @DisplayName("Step 7: Verify final state")
    void step7_VerifyFinalState() {
        // Verify all assets
        List<Asset> assets = assetService.getAssetsByCustomer(customerId);
        assertEquals(2, assets.size());

        Asset tryAsset = assetRepository.findByCustomerIdAndAssetName(customerId, "TRY");
        Asset aaplAsset = assetRepository.findByCustomerIdAndAssetName(customerId, "AAPL");

        // TRY: Started 10000, spent 1500 on AAPL, withdrew 1000 = 7500
        assertEquals(0, new BigDecimal("7500").compareTo(tryAsset.getSize()));

        // AAPL: Bought 10, tried to sell 5 but cancelled = 10
        assertEquals(0, new BigDecimal("10").compareTo(aaplAsset.getSize()));

        // Verify orders
        List<Order> allOrders = orderService.listOrdersByCustomer(customerId);
        assertEquals(2, allOrders.size());

        long matchedCount = allOrders.stream()
            .filter(o -> o.getStatus() == OrderStatus.MATCHED)
            .count();
        long cancelledCount = allOrders.stream()
            .filter(o -> o.getStatus() == OrderStatus.CANCELED)
            .count();

        assertEquals(1, matchedCount);
        assertEquals(1, cancelledCount);
    }
}

