package broker.dto.response;

import broker.model.Customer;

public record CustomerResponse(
    Long id,
    String name,
    String username,
    String email,
    String role
) {
    public static CustomerResponse fromEntity(Customer customer) {
        return new CustomerResponse(
            customer.getId(),
            customer.getName(),
            customer.getUsername(),
            customer.getEmail(),
            customer.getRole()
        );
    }
}
