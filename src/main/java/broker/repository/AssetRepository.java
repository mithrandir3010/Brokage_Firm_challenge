package broker.repository;

import broker.model.Asset;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AssetRepository extends JpaRepository<Asset, Long> {

    List<Asset> findByCustomerId(Long customerId);

    Asset findByCustomerIdAndAssetName(Long customerId, String assetName);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Asset a WHERE a.customerId = :customerId AND a.assetName = :assetName")
    Asset findAssetForUpdate(@Param("customerId") Long customerId, @Param("assetName") String assetName);
}
