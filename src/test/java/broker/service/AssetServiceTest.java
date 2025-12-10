package broker.service;

import broker.model.Asset;
import broker.repository.AssetRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssetServiceTest {

    @Mock
    private AssetRepository assetRepository;

    @InjectMocks
    private AssetService assetService;

    private static final Long CUSTOMER_ID = 1L;
    private static final String TRY_ASSET = "TRY";

    // Helper method for BigDecimal comparison
    private void assertBigDecimalEquals(String expected, BigDecimal actual) {
        assertEquals(0, new BigDecimal(expected).compareTo(actual), 
            "Expected " + expected + " but was " + actual);
    }

    @Nested
    @DisplayName("Deposit Money Tests")
    class DepositMoneyTests {

        @Test
        @DisplayName("Deposit should increase TRY balance for existing asset")
        void depositMoney_ExistingAsset_Success() {
            // Given
            Asset existingAsset = createAsset(CUSTOMER_ID, TRY_ASSET, 
                new BigDecimal("1000"), new BigDecimal("1000"));
            BigDecimal depositAmount = new BigDecimal("500");
            
            when(assetRepository.findByCustomerIdAndAssetName(CUSTOMER_ID, TRY_ASSET))
                .thenReturn(existingAsset);
            when(assetRepository.save(any(Asset.class))).thenAnswer(inv -> inv.getArgument(0));

            // When
            Asset result = assetService.depositMoney(CUSTOMER_ID, depositAmount);

            // Then
            assertBigDecimalEquals("1500", result.getSize());
            assertBigDecimalEquals("1500", result.getUsableSize());
            verify(assetRepository).save(existingAsset);
        }

        @Test
        @DisplayName("Deposit should create TRY asset if not exists")
        void depositMoney_NewAsset_Success() {
            // Given
            BigDecimal depositAmount = new BigDecimal("1000");
            
            when(assetRepository.findByCustomerIdAndAssetName(CUSTOMER_ID, TRY_ASSET))
                .thenReturn(null);
            when(assetRepository.save(any(Asset.class))).thenAnswer(invocation -> {
                Asset asset = invocation.getArgument(0);
                asset.setId(1L);
                return asset;
            });

            // When
            Asset result = assetService.depositMoney(CUSTOMER_ID, depositAmount);

            // Then
            assertEquals(TRY_ASSET, result.getAssetName());
            assertEquals(CUSTOMER_ID, result.getCustomerId());
            assertBigDecimalEquals("1000", result.getSize());
            assertBigDecimalEquals("1000", result.getUsableSize());
        }

        @Test
        @DisplayName("Deposit should fail with zero amount")
        void depositMoney_ZeroAmount_Fail() {
            // When & Then
            assertThrows(IllegalArgumentException.class, () ->
                assetService.depositMoney(CUSTOMER_ID, BigDecimal.ZERO)
            );
            verify(assetRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deposit should fail with negative amount")
        void depositMoney_NegativeAmount_Fail() {
            // When & Then
            assertThrows(IllegalArgumentException.class, () ->
                assetService.depositMoney(CUSTOMER_ID, new BigDecimal("-100"))
            );
            verify(assetRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Withdraw Money Tests")
    class WithdrawMoneyTests {

        @Test
        @DisplayName("Withdraw should decrease TRY balance")
        void withdrawMoney_Success() {
            // Given
            Asset tryAsset = createAsset(CUSTOMER_ID, TRY_ASSET, 
                new BigDecimal("1000"), new BigDecimal("1000"));
            BigDecimal withdrawAmount = new BigDecimal("300");
            
            when(assetRepository.findAssetForUpdate(CUSTOMER_ID, TRY_ASSET)).thenReturn(tryAsset);
            when(assetRepository.save(any(Asset.class))).thenAnswer(inv -> inv.getArgument(0));

            // When
            Asset result = assetService.withdrawMoney(CUSTOMER_ID, withdrawAmount, "TR123456789");

            // Then
            assertBigDecimalEquals("700", result.getSize());
            assertBigDecimalEquals("700", result.getUsableSize());
        }

        @Test
        @DisplayName("Withdraw should fail with insufficient balance")
        void withdrawMoney_InsufficientBalance_Fail() {
            // Given
            Asset tryAsset = createAsset(CUSTOMER_ID, TRY_ASSET, 
                new BigDecimal("100"), new BigDecimal("100"));
            
            when(assetRepository.findAssetForUpdate(CUSTOMER_ID, TRY_ASSET)).thenReturn(tryAsset);

            // When & Then
            assertThrows(IllegalArgumentException.class, () ->
                assetService.withdrawMoney(CUSTOMER_ID, new BigDecimal("500"), "TR123456789")
            );
        }

        @Test
        @DisplayName("Withdraw should fail when TRY asset does not exist")
        void withdrawMoney_NoAsset_Fail() {
            // Given
            when(assetRepository.findAssetForUpdate(CUSTOMER_ID, TRY_ASSET)).thenReturn(null);

            // When & Then
            assertThrows(IllegalArgumentException.class, () ->
                assetService.withdrawMoney(CUSTOMER_ID, new BigDecimal("100"), "TR123456789")
            );
        }

        @Test
        @DisplayName("Withdraw should fail with zero amount")
        void withdrawMoney_ZeroAmount_Fail() {
            // When & Then
            assertThrows(IllegalArgumentException.class, () ->
                assetService.withdrawMoney(CUSTOMER_ID, BigDecimal.ZERO, "TR123456789")
            );
        }

        @Test
        @DisplayName("Withdraw should consider usableSize not total size")
        void withdrawMoney_CheckUsableSize() {
            // Given - size is 1000 but usableSize is only 200 (800 blocked)
            Asset tryAsset = createAsset(CUSTOMER_ID, TRY_ASSET, 
                new BigDecimal("1000"), new BigDecimal("200"));
            
            when(assetRepository.findAssetForUpdate(CUSTOMER_ID, TRY_ASSET)).thenReturn(tryAsset);

            // When & Then - trying to withdraw 500 should fail
            assertThrows(IllegalArgumentException.class, () ->
                assetService.withdrawMoney(CUSTOMER_ID, new BigDecimal("500"), "TR123456789")
            );
        }
    }

    @Nested
    @DisplayName("Get Assets Tests")
    class GetAssetsTests {

        @Test
        @DisplayName("Should return all assets for customer")
        void getAssetsByCustomer_Success() {
            // Given
            List<Asset> assets = Arrays.asList(
                createAsset(CUSTOMER_ID, TRY_ASSET, new BigDecimal("1000"), new BigDecimal("1000")),
                createAsset(CUSTOMER_ID, "AAPL", new BigDecimal("50"), new BigDecimal("50"))
            );
            
            when(assetRepository.findByCustomerId(CUSTOMER_ID)).thenReturn(assets);

            // When
            List<Asset> result = assetService.getAssetsByCustomer(CUSTOMER_ID);

            // Then
            assertEquals(2, result.size());
        }

        @Test
        @DisplayName("Should return specific asset")
        void getAsset_Success() {
            // Given
            Asset asset = createAsset(CUSTOMER_ID, "AAPL", new BigDecimal("50"), new BigDecimal("50"));
            
            when(assetRepository.findByCustomerIdAndAssetName(CUSTOMER_ID, "AAPL")).thenReturn(asset);

            // When
            Asset result = assetService.getAsset(CUSTOMER_ID, "AAPL");

            // Then
            assertNotNull(result);
            assertEquals("AAPL", result.getAssetName());
        }

        @Test
        @DisplayName("Should return null for non-existent asset")
        void getAsset_NotFound() {
            // Given
            when(assetRepository.findByCustomerIdAndAssetName(CUSTOMER_ID, "GOOG")).thenReturn(null);

            // When
            Asset result = assetService.getAsset(CUSTOMER_ID, "GOOG");

            // Then
            assertNull(result);
        }
    }

    @Nested
    @DisplayName("Get Or Create Asset Tests")
    class GetOrCreateAssetTests {

        @Test
        @DisplayName("Should return existing asset")
        void getOrCreateAsset_Existing() {
            // Given
            Asset existingAsset = createAsset(CUSTOMER_ID, "AAPL", 
                new BigDecimal("50"), new BigDecimal("50"));
            
            when(assetRepository.findByCustomerIdAndAssetName(CUSTOMER_ID, "AAPL"))
                .thenReturn(existingAsset);

            // When
            Asset result = assetService.getOrCreateAsset(CUSTOMER_ID, "AAPL");

            // Then
            assertEquals(existingAsset, result);
            verify(assetRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should create new asset if not exists")
        void getOrCreateAsset_New() {
            // Given
            when(assetRepository.findByCustomerIdAndAssetName(CUSTOMER_ID, "GOOG"))
                .thenReturn(null);
            when(assetRepository.save(any(Asset.class))).thenAnswer(invocation -> {
                Asset asset = invocation.getArgument(0);
                asset.setId(1L);
                return asset;
            });

            // When
            Asset result = assetService.getOrCreateAsset(CUSTOMER_ID, "GOOG");

            // Then
            assertNotNull(result);
            assertEquals("GOOG", result.getAssetName());
            assertBigDecimalEquals("0", result.getSize());
            assertBigDecimalEquals("0", result.getUsableSize());
            verify(assetRepository).save(any(Asset.class));
        }
    }

    // Helper method
    private Asset createAsset(Long customerId, String assetName, BigDecimal size, BigDecimal usableSize) {
        Asset asset = new Asset();
        asset.setId(1L);
        asset.setCustomerId(customerId);
        asset.setAssetName(assetName);
        asset.setSize(size);
        asset.setUsableSize(usableSize);
        return asset;
    }
}
