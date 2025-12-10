package broker.exception;

import broker.model.OrderStatus;

public class OrderNotCancellableException extends RuntimeException {
    public OrderNotCancellableException(Long orderId, OrderStatus status) {
        super(String.format("Emir iptal edilemez. Emir ID: %d, Mevcut durum: %s. Sadece PENDING emirler iptal edilebilir.", orderId, status));
    }
}
