package broker.exception;

import broker.model.OrderStatus;

public class OrderNotMatchableException extends RuntimeException {
    public OrderNotMatchableException(Long orderId, OrderStatus status) {
        super(String.format("Emir eşleştirilemez. Emir ID: %d, Mevcut durum: %s. Sadece PENDING emirler eşleştirilebilir.", orderId, status));
    }
}
