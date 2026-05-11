package com.internship.tool.service;

import com.internship.tool.entity.CyberAsset;
import com.internship.tool.exception.AssetValidationException;
import com.internship.tool.exception.DuplicateResourceException;
import com.internship.tool.exception.ResourceNotFoundException;
import com.internship.tool.repository.CyberAssetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class CyberAssetServiceImpl implements CyberAssetService {

    private final CyberAssetRepository assetRepository;

    @Autowired
    public CyberAssetServiceImpl(CyberAssetRepository assetRepository) {
        this.assetRepository = assetRepository;
    }

    @Override
    public CyberAsset createAsset(CyberAsset asset) {
        validateAsset(asset);
        
        if (asset.getIpAddress() != null && assetRepository.existsByIpAddressAndDeletedFalse(asset.getIpAddress())) {
            throw new DuplicateResourceException("Asset with IP address " + asset.getIpAddress() + " already exists.");
        }
        
        return assetRepository.save(asset);
    }

    @Override
    public CyberAsset updateAsset(Long id, CyberAsset assetDetails) {
        CyberAsset existingAsset = getAssetById(id);
        
        validateAsset(assetDetails);

        // Update fields
        existingAsset.setAssetName(assetDetails.getAssetName());
        existingAsset.setAssetType(assetDetails.getAssetType());
        existingAsset.setIpAddress(assetDetails.getIpAddress());
        existingAsset.setMacAddress(assetDetails.getMacAddress());
        existingAsset.setOperatingSystem(assetDetails.getOperatingSystem());
        existingAsset.setHostname(assetDetails.getHostname());
        existingAsset.setLocation(assetDetails.getLocation());
        existingAsset.setOwner(assetDetails.getOwner());
        existingAsset.setDepartment(assetDetails.getDepartment());
        existingAsset.setStatus(assetDetails.getStatus());
        existingAsset.setRiskScore(assetDetails.getRiskScore());
        existingAsset.setOpenPorts(assetDetails.getOpenPorts());
        existingAsset.setVulnerabilities(assetDetails.getVulnerabilities());
        existingAsset.setNotes(assetDetails.getNotes());
        
        return assetRepository.save(existingAsset);
    }

    @Override
    @Transactional(readOnly = true)
    public CyberAsset getAssetById(Long id) {
        return assetRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("CyberAsset not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CyberAsset> getAllAssets(Pageable pageable) {
        return assetRepository.findAllByDeletedFalse(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CyberAsset> searchAssets(String query, Pageable pageable) {
        return assetRepository.searchAssets(query, pageable);
    }

    @Override
    public void deleteAsset(Long id) {
        CyberAsset asset = getAssetById(id);
        asset.setDeleted(true);
        assetRepository.save(asset);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CyberAsset> getHighRiskAssets() {
        return assetRepository.findHighRiskAssets();
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getAssetStatistics() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("total", assetRepository.countByDeletedFalse());
        
        List<Object[]> statusCounts = assetRepository.countByStatusGrouped();
        for (Object[] row : statusCounts) {
            stats.put((String) row[0], (Long) row[1]);
        }
        
        return stats;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CyberAsset> getAssetsCreatedBetween(LocalDateTime from, LocalDateTime to, Pageable pageable) {
        return assetRepository.findByCreatedAtBetween(from, to, pageable);
    }

    private void validateAsset(CyberAsset asset) {
        if (asset.getAssetName() == null || asset.getAssetName().trim().isEmpty()) {
            throw new AssetValidationException("Asset name is required.");
        }
        if (asset.getAssetType() == null || asset.getAssetType().trim().isEmpty()) {
            throw new AssetValidationException("Asset type is required.");
        }
        if (asset.getStatus() == null || asset.getStatus().trim().isEmpty()) {
            throw new AssetValidationException("Asset status is required.");
        }
        if (asset.getRiskScore() != null && (asset.getRiskScore() < 0 || asset.getRiskScore() > 100)) {
            throw new AssetValidationException("Risk score must be between 0 and 100.");
        }
    }
}
