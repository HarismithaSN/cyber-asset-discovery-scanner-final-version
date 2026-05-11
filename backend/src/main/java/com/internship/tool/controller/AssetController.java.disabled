package com.internship.tool.controller;

import com.internship.tool.model.Asset;
import com.internship.tool.service.AssetService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/assets")
@CrossOrigin(origins = "http://localhost:5173")
public class AssetController {

    @Autowired
    private AssetService assetService;

    
    @GetMapping("/all")
    public Page<Asset> getAllAssets(Pageable pageable) {
        return assetService.getAllAssets(pageable);
    }

   
    @GetMapping("/search")
    public Page<Asset> searchAssets(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        return assetService.searchAssets(
                q,
                status,
                startDate,
                endDate,
                PageRequest.of(page, size)
        );
    }

   
    @GetMapping("/stats")
    public Map<String, Long> getStats() {
        Map<String, Long> stats = new HashMap<>();

        stats.put("totalAssets", assetService.countAllAssets());
        stats.put("activeAssets", assetService.countByStatus("ACTIVE"));
        stats.put("inactiveAssets", assetService.countByStatus("INACTIVE"));
        stats.put("highRiskAssets", assetService.countHighRiskAssets());

        return stats;
    }

    
    @GetMapping("/{id}")
    public Asset getAssetById(@PathVariable Long id) {
        return assetService.getAssetById(id);
    }

  
    @GetMapping
    public Iterable<Asset> getAssets() {
        return assetService.getAssets();
    }

   
    @PostMapping
    public Asset addAsset(@RequestBody Asset asset) {
        return assetService.addAsset(asset);
    }

    
    @PutMapping("/{id}")
    public Asset updateAsset(@PathVariable Long id, @RequestBody Asset asset) {
        return assetService.updateAsset(id, asset);
    }

    
    @DeleteMapping("/{id}")
    public void deleteAsset(@PathVariable Long id) {
        assetService.deleteAsset(id);
    }
}