package broker.service;

import broker.model.Order;
import broker.model.OrderSide;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface IOrderService {

    Order createOrder(Long customerId, String assetName, OrderSide orderSide, BigDecimal size, BigDecimal price);

    List<Order> listOrders(Long customerId, LocalDateTime startDate, LocalDateTime endDate);

    List<Order> listOrdersByCustomer(Long customerId);

    List<Order> listPendingOrders(Long customerId);

    Order cancelOrder(Long orderId, Long customerId);

    Order matchOrder(Long orderId);

    Order getOrderById(Long orderId);
}
