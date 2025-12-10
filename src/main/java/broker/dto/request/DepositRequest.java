package broker.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record DepositRequest(
    @NotNull(message = "Customer ID boş olamaz")
    Long customerId,
    
    @NotNull(message = "Miktar boş olamaz")
    @DecimalMin(value = "0.01", message = "Miktar pozitif olmalıdır")
    BigDecimal amount
) {}
