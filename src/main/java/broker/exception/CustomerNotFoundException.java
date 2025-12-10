package broker.exception;

public class CustomerNotFoundException extends RuntimeException {
    public CustomerNotFoundException(Long id) {
        super("Müşteri bulunamadı: " + id);
    }
    
    public CustomerNotFoundException(String username) {
        super("Müşteri bulunamadı: " + username);
    }
}
