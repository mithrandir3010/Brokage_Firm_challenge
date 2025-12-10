package broker.exception;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(Long id) {
        super("Emir bulunamadı: " + id);
    }
}
