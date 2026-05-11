package com.internship.tool.service;

import com.internship.tool.entity.CyberAsset;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface CyberAssetService {

    CyberAsset createAsset(CyberAsset asset);

    CyberAsset updateAsset(Long id, CyberAsset assetDetails);

    CyberAsset getAssetById(Long id);

    Page<CyberAsset> getAllAssets(Pageable pageable);

    Page<CyberAsset> searchAssets(String query, Pageable pageable);

    void deleteAsset(Long id);

    List<CyberAsset> getHighRiskAssets();

    Map<String, Long> getAssetStatistics();
    
    Page<CyberAsset> getAssetsCreatedBetween(LocalDateTime from, LocalDateTime to, Pageable pageable);
}
