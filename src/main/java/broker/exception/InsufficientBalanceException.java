package broker.exception;

import java.math.BigDecimal;

public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException(String assetName) {
        super("Yetersiz " + assetName + " bakiyesi");
    }
    
    public InsufficientBalanceException(String assetName, BigDecimal required, BigDecimal available) {
        super(String.format("Yetersiz %s bakiyesi. Gerekli: %s, Mevcut: %s", assetName, required.toPlainString(), available.toPlainString()));
    }
}
