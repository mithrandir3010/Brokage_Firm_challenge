package broker.dto.response;

import broker.model.Asset;

import java.math.BigDecimal;

public record AssetResponse(
    Long id,
    Long customerId,
    String assetName,
    BigDecimal size,
    BigDecimal usableSize
) {
    public static AssetResponse fromEntity(Asset asset) {
        return new AssetResponse(
            asset.getId(),
            asset.getCustomerId(),
            asset.getAssetName(),
            asset.getSize(),
            asset.getUsableSize()
        );
    }
}
