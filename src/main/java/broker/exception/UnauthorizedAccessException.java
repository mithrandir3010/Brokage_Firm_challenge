package broker.exception;

public class UnauthorizedAccessException extends RuntimeException {
    public UnauthorizedAccessException() {
        super("Bu işlem için yetkiniz yok");
    }
    
    public UnauthorizedAccessException(String message) {
        super(message);
    }
}
