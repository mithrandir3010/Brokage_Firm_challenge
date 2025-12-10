package broker.exception;

public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String resource, String field, String value) {
        super(String.format("%s zaten mevcut: %s = %s", resource, field, value));
    }
}
