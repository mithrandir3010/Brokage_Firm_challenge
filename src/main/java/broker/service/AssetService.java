package broker.service;

import broker.model.Asset;
import broker.repository.AssetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AssetService implements IAssetService {

    private final AssetRepository assetRepository;
    private static final String TRY_ASSET = "TRY";

    @Override
    public List<Asset> getAssetsByCustomer(Long customerId) {
        return assetRepository.findByCustomerId(customerId);
    }

    @Override
    @Transactional
    public Asset getOrCreateAsset(Long customerId, String assetName) {
        Asset asset = assetRepository.findByCustomerIdAndAssetName(customerId, assetName);
        if (asset == null) {
            asset = new Asset();
            asset.setCustomerId(customerId);
            asset.setAssetName(assetName);
            asset.setSize(BigDecimal.ZERO);
            asset.setUsableSize(BigDecimal.ZERO);
            asset = assetRepository.save(asset);
        }
        return asset;
    }

    @Override
    public Asset getAsset(Long customerId, String assetName) {
        return assetRepository.findByCustomerIdAndAssetName(customerId, assetName);
    }

    @Override
    @Transactional
    public Asset depositMoney(Long customerId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Yatırılacak miktar 0'dan büyük olmalıdır");
        }

        Asset tryAsset = getOrCreateAsset(customerId, TRY_ASSET);
        tryAsset.setSize(tryAsset.getSize().add(amount));
        tryAsset.setUsableSize(tryAsset.getUsableSize().add(amount));

        return assetRepository.save(tryAsset);
    }

    @Override
    @Transactional
    public Asset withdrawMoney(Long customerId, BigDecimal amount, String iban) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Çekilecek miktar 0'dan büyük olmalıdır");
        }

        Asset tryAsset = assetRepository.findAssetForUpdate(customerId, TRY_ASSET);
        
        if (tryAsset == null || tryAsset.getUsableSize().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Yetersiz bakiye");
        }

        tryAsset.setSize(tryAsset.getSize().subtract(amount));
        tryAsset.setUsableSize(tryAsset.getUsableSize().subtract(amount));
        return assetRepository.save(tryAsset);
    }
}
