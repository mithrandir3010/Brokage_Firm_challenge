package broker.dto.request;

import broker.model.OrderSide;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateOrderRequest(
    @NotNull(message = "Customer ID boş olamaz")
    Long customerId,
    
    @NotBlank(message = "Asset adı boş olamaz")
    String assetName,
    
    @NotNull(message = "Order side boş olamaz")
    OrderSide orderSide,
    
    @NotNull(message = "Size boş olamaz")
    @DecimalMin(value = "0.00000001", message = "Size pozitif olmalıdır")
    BigDecimal size,
    
    @NotNull(message = "Price boş olamaz")
    @DecimalMin(value = "0.00000001", message = "Price pozitif olmalıdır")
    BigDecimal price
) {}
