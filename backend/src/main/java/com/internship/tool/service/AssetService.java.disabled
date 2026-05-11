package com.internship.tool.service;

import com.internship.tool.model.Asset;
import com.internship.tool.repository.AssetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AssetService {

    @Autowired
    private AssetRepository assetRepository;

    public Page<Asset> getAllAssets(Pageable pageable) {
        return assetRepository.findAll(pageable);
    }

    public Iterable<Asset> getAssets() {
        return assetRepository.findAll();
    }

    public Asset getAssetById(Long id) {
        return assetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Asset not found with id: " + id));
    }

    public Asset addAsset(Asset asset) {
        return assetRepository.save(asset);
    }

    public Asset updateAsset(Long id, Asset assetDetails) {
        Asset asset = getAssetById(id);

        asset.setName(assetDetails.getName());
        asset.setType(assetDetails.getType());
        asset.setIpAddress(assetDetails.getIpAddress());
        asset.setStatus(assetDetails.getStatus());
        asset.setRiskScore(assetDetails.getRiskScore());

        return assetRepository.save(asset);
    }

    public void deleteAsset(Long id) {
        Asset asset = getAssetById(id);
        assetRepository.delete(asset);
    }

    public long countAllAssets() {
        return assetRepository.count();
    }

    public long countByStatus(String status) {
        return assetRepository.countByStatusIgnoreCase(status);
    }

    public long countHighRiskAssets() {
        return assetRepository.countByRiskScoreGreaterThanEqual(70);
    }

    public Page<Asset> searchAssets(
            String q,
            String status,
            String startDate,
            String endDate,
            Pageable pageable
    ) {
        return assetRepository.searchAssets(q, status, pageable);
    }
}