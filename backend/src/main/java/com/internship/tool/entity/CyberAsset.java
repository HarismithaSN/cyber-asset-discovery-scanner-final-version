package com.internship.tool.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "cyber_assets",
    indexes = {
        @Index(name = "idx_cyber_asset_status", columnList = "status"),
        @Index(name = "idx_cyber_asset_asset_type", columnList = "asset_type"),
        @Index(name = "idx_cyber_asset_ip_address", columnList = "ip_address"),
        @Index(name = "idx_cyber_asset_created_at", columnList = "created_at")
    }
)
@EntityListeners(AuditingEntityListener.class)
public class CyberAsset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Asset name is required")
    @Size(max = 255, message = "Asset name cannot exceed 255 characters")
    @Column(name = "asset_name", nullable = false, length = 255)
    private String assetName;

    @NotBlank(message = "Asset type is required")
    @Size(max = 100, message = "Asset type cannot exceed 100 characters")
    @Column(name = "asset_type", nullable = false, length = 100)
    private String assetType;          // e.g. SERVER, WORKSTATION, ROUTER, IOT_DEVICE

    @Column(name = "ip_address", length = 45)
    private String ipAddress;          // supports IPv4 and IPv6

    @Column(name = "mac_address", length = 17)
    private String macAddress;

    @Column(name = "operating_system", length = 255)
    private String operatingSystem;

    @Column(name = "hostname", length = 255)
    private String hostname;

    @Column(name = "location", length = 255)
    private String location;

    @Column(name = "owner", length = 255)
    private String owner;

    @Column(name = "department", length = 255)
    private String department;

    @NotBlank(message = "Status is required")
    @Size(max = 50, message = "Status cannot exceed 50 characters")
    @Column(name = "status", nullable = false, length = 50)
    private String status;             // ACTIVE, INACTIVE, UNKNOWN, DECOMMISSIONED

    @Min(value = 0, message = "Risk score must be at least 0")
    @Max(value = 100, message = "Risk score cannot exceed 100")
    @Column(name = "risk_score")
    private Integer riskScore;         // 0–100

    @Column(name = "open_ports", columnDefinition = "TEXT")
    private String openPorts;          // comma-separated, e.g. "22,80,443"

    @Column(name = "vulnerabilities", columnDefinition = "TEXT")
    private String vulnerabilities;    // comma-separated CVE IDs or descriptions

    @Column(name = "last_scanned_at")
    private LocalDateTime lastScannedAt;

    @Column(name = "ai_description", columnDefinition = "TEXT")
    private String aiDescription;      // populated by AI /describe endpoint

    @Column(name = "ai_recommendations", columnDefinition = "TEXT")
    private String aiRecommendations;  // populated by AI /recommend endpoint

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "is_deleted", nullable = false)
    private boolean deleted = false;   // soft-delete flag

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // ─── Constructors ──────────────────────────────────────────────────────────

    public CyberAsset() {}

    public CyberAsset(String assetName, String assetType, String status) {
        this.assetName = assetName;
        this.assetType = assetType;
        this.status = status;
    }

    // ─── Getters & Setters ────────────────────────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getAssetName() { return assetName; }
    public void setAssetName(String assetName) { this.assetName = assetName; }

    public String getAssetType() { return assetType; }
    public void setAssetType(String assetType) { this.assetType = assetType; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public String getMacAddress() { return macAddress; }
    public void setMacAddress(String macAddress) { this.macAddress = macAddress; }

    public String getOperatingSystem() { return operatingSystem; }
    public void setOperatingSystem(String operatingSystem) { this.operatingSystem = operatingSystem; }

    public String getHostname() { return hostname; }
    public void setHostname(String hostname) { this.hostname = hostname; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getOwner() { return owner; }
    public void setOwner(String owner) { this.owner = owner; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getRiskScore() { return riskScore; }
    public void setRiskScore(Integer riskScore) { this.riskScore = riskScore; }

    public String getOpenPorts() { return openPorts; }
    public void setOpenPorts(String openPorts) { this.openPorts = openPorts; }

    public String getVulnerabilities() { return vulnerabilities; }
    public void setVulnerabilities(String vulnerabilities) { this.vulnerabilities = vulnerabilities; }

    public LocalDateTime getLastScannedAt() { return lastScannedAt; }
    public void setLastScannedAt(LocalDateTime lastScannedAt) { this.lastScannedAt = lastScannedAt; }

    public String getAiDescription() { return aiDescription; }
    public void setAiDescription(String aiDescription) { this.aiDescription = aiDescription; }

    public String getAiRecommendations() { return aiRecommendations; }
    public void setAiRecommendations(String aiRecommendations) { this.aiRecommendations = aiRecommendations; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public boolean isDeleted() { return deleted; }
    public void setDeleted(boolean deleted) { this.deleted = deleted; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
