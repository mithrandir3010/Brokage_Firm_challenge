package broker.dto.request;

import jakarta.validation.constraints.Email;

public record UpdateCustomerRequest(
    String name,
    
    @Email(message = "Geçerli bir e-posta adresi giriniz")
    String email,
    
    String password
) {}
