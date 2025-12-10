package broker.controller;

import broker.dto.request.DepositRequest;
import broker.dto.request.WithdrawRequest;
import broker.dto.response.AssetResponse;
import broker.model.Asset;
import broker.security.CustomUserDetails;
import broker.service.IAssetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assets")
@RequiredArgsConstructor
public class AssetController {

    private final IAssetService assetService;

    @GetMapping
    public ResponseEntity<List<AssetResponse>> getAssets(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) Long customerId) {
        
        Long targetCustomerId;
        if (userDetails.getRole().equals("ADMIN") && customerId != null) {
            targetCustomerId = customerId;
        } else {
            targetCustomerId = userDetails.getCustomerId();
        }
        
        List<Asset> assets = assetService.getAssetsByCustomer(targetCustomerId);
        List<AssetResponse> response = assets.stream()
                .map(AssetResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{assetName}")
    public ResponseEntity<AssetResponse> getAsset(
            @PathVariable String assetName,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) Long customerId) {
        
        Long targetCustomerId;
        if (userDetails.getRole().equals("ADMIN") && customerId != null) {
            targetCustomerId = customerId;
        } else {
            targetCustomerId = userDetails.getCustomerId();
        }
        
        Asset asset = assetService.getAsset(targetCustomerId, assetName);
        if (asset == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(AssetResponse.fromEntity(asset));
    }

    @PostMapping("/deposit")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AssetResponse> deposit(@Valid @RequestBody DepositRequest request) {
        Asset asset = assetService.depositMoney(request.customerId(), request.amount());
        return ResponseEntity.ok(AssetResponse.fromEntity(asset));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<AssetResponse> withdraw(
            @Valid @RequestBody WithdrawRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        Long customerId = request.customerId();
        if (!userDetails.getRole().equals("ADMIN") && !userDetails.getCustomerId().equals(customerId)) {
            customerId = userDetails.getCustomerId();
        }
        
        Asset asset = assetService.withdrawMoney(customerId, request.amount(), request.iban());
        return ResponseEntity.ok(AssetResponse.fromEntity(asset));
    }
}
