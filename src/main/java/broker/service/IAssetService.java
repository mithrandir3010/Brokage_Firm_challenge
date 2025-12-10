package broker.service;

import broker.model.Asset;

import java.math.BigDecimal;
import java.util.List;

public interface IAssetService {

    List<Asset> getAssetsByCustomer(Long customerId);

    Asset getOrCreateAsset(Long customerId, String assetName);

    Asset getAsset(Long customerId, String assetName);

    Asset depositMoney(Long customerId, BigDecimal amount);

    Asset withdrawMoney(Long customerId, BigDecimal amount, String iban);
}
