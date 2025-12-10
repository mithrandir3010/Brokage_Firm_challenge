package broker.dto.response;

import broker.model.Order;
import broker.model.OrderSide;
import broker.model.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderResponse(
    Long id,
    Long customerId,
    String assetName,
    OrderSide orderSide,
    BigDecimal size,
    BigDecimal price,
    OrderStatus status,
    LocalDateTime createDate
) {
    public static OrderResponse fromEntity(Order order) {
        return new OrderResponse(
            order.getId(),
            order.getCustomerId(),
            order.getAssetName(),
            order.getOrderSide(),
            order.getSize(),
            order.getPrice(),
            order.getStatus(),
            order.getCreateDate()
        );
    }
}
