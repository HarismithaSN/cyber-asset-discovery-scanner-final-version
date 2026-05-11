package com.internship.tool.dto;

public class AssetStatsResponse {

    private long totalAssets;
    private long activeAssets;
    private long inactiveAssets;
    private long highRiskAssets;

    public AssetStatsResponse(long totalAssets, long activeAssets, long inactiveAssets, long highRiskAssets) {
        this.totalAssets = totalAssets;
        this.activeAssets = activeAssets;
        this.inactiveAssets = inactiveAssets;
        this.highRiskAssets = highRiskAssets;
    }

    public long getTotalAssets() {
        return totalAssets;
    }

    public long getActiveAssets() {
        return activeAssets;
    }

    public long getInactiveAssets() {
        return inactiveAssets;
    }

    public long getHighRiskAssets() {
        return highRiskAssets;
    }
}