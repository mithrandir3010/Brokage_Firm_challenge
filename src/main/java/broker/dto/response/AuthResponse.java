package broker.dto.response;

public record AuthResponse(
    String token,
    String type,
    Long customerId,
    String username,
    String role
) {
    public AuthResponse(String token, Long customerId, String username, String role) {
        this(token, "Bearer", customerId, username, role);
    }
}
