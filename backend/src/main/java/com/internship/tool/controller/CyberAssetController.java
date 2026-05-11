package com.internship.tool.controller;

import com.internship.tool.entity.CyberAsset;
import com.internship.tool.service.CyberAssetService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for Cyber Asset CRUD operations.
 *
 * Day 4 JD1: GET /all (paginated), GET /{id} (404 on miss), POST /create (@Valid, 201)
 * Day 4 JD2: PUT /{id}, DELETE soft, GET /search?q=
 * Day 6 JD1: @Cacheable GETs 10-min TTL, @CacheEvict on writes, @PreAuthorize RBAC
 * Day 9 JD2: GET /export CSV, GET /stats
 */
@RestController
@RequestMapping("/api/assets")
@CrossOrigin(origins = "*")
public class CyberAssetController {

    private final CyberAssetService assetService;

    @Autowired
    public CyberAssetController(CyberAssetService assetService) {
        this.assetService = assetService;
    }

    // ─── GET /all — paginated list ────────────────────────────────────────────

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Cacheable(value = "assets", key = "'page-' + #pageable.pageNumber + '-' + #pageable.pageSize")
    public ResponseEntity<Page<CyberAsset>> getAllAssets(
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(assetService.getAllAssets(pageable));
    }

    // ─── GET /{id} — single asset, 404 if missing ─────────────────────────────

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Cacheable(value = "asset", key = "#id")
    public ResponseEntity<CyberAsset> getAssetById(@PathVariable Long id) {
        return ResponseEntity.ok(assetService.getAssetById(id));
    }

    // ─── POST /create — create, 201 ──────────────────────────────────────────

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    @CacheEvict(value = {"assets", "asset"}, allEntries = true)
    public ResponseEntity<CyberAsset> createAsset(@Valid @RequestBody CyberAsset asset) {
        return new ResponseEntity<>(assetService.createAsset(asset), HttpStatus.CREATED);
    }

    // ─── PUT /{id} — update ───────────────────────────────────────────────────

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @CacheEvict(value = {"assets", "asset"}, allEntries = true)
    public ResponseEntity<CyberAsset> updateAsset(
            @PathVariable Long id,
            @Valid @RequestBody CyberAsset assetDetails) {
        return ResponseEntity.ok(assetService.updateAsset(id, assetDetails));
    }

    // ─── DELETE /{id} — soft delete ───────────────────────────────────────────

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @CacheEvict(value = {"assets", "asset"}, allEntries = true)
    public ResponseEntity<Void> deleteAsset(@PathVariable Long id) {
        assetService.deleteAsset(id);
        return ResponseEntity.noContent().build();
    }

    // ─── GET /search?q= ───────────────────────────────────────────────────────

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Page<CyberAsset>> searchAssets(
            @RequestParam(name = "q", defaultValue = "") String query,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(assetService.searchAssets(query, pageable));
    }

    // ─── GET /stats — KPI dashboard counts ───────────────────────────────────

    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Cacheable(value = "stats", key = "'asset-stats'")
    public ResponseEntity<Map<String, Long>> getStats() {
        return ResponseEntity.ok(assetService.getAssetStatistics());
    }

    // ─── GET /high-risk — assets with risk >= 70 ─────────────────────────────

    @GetMapping("/high-risk")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Cacheable(value = "highRisk", key = "'high-risk-assets'")
    public ResponseEntity<List<CyberAsset>> getHighRiskAssets() {
        return ResponseEntity.ok(assetService.getHighRiskAssets());
    }

    // ─── GET /export — CSV download ───────────────────────────────────────────

    @GetMapping("/export")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public void exportCsv(HttpServletResponse response) throws IOException {
        response.setContentType(MediaType.TEXT_PLAIN_VALUE);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"cyber-assets.csv\"");

        // Fetch all non-deleted assets (up to 10 000 rows)
        Pageable all = Pageable.ofSize(10_000);
        Page<CyberAsset> page = assetService.getAllAssets(all);

        PrintWriter writer = response.getWriter();
        // CSV header
        writer.println("id,assetName,assetType,status,ipAddress,macAddress," +
                "operatingSystem,hostname,location,owner,department,riskScore," +
                "openPorts,vulnerabilities,notes,lastScannedAt,createdAt,updatedAt");

        for (CyberAsset a : page.getContent()) {
            writer.println(String.join(",",
                    nvl(a.getId()),
                    csvEscape(a.getAssetName()),
                    csvEscape(a.getAssetType()),
                    csvEscape(a.getStatus()),
                    csvEscape(a.getIpAddress()),
                    csvEscape(a.getMacAddress()),
                    csvEscape(a.getOperatingSystem()),
                    csvEscape(a.getHostname()),
                    csvEscape(a.getLocation()),
                    csvEscape(a.getOwner()),
                    csvEscape(a.getDepartment()),
                    nvl(a.getRiskScore()),
                    csvEscape(a.getOpenPorts()),
                    csvEscape(a.getVulnerabilities()),
                    csvEscape(a.getNotes()),
                    nvl(a.getLastScannedAt()),
                    nvl(a.getCreatedAt()),
                    nvl(a.getUpdatedAt())
            ));
        }
        writer.flush();
    }

    // ─── helpers ──────────────────────────────────────────────────────────────

    private String nvl(Object value) {
        return value == null ? "" : value.toString();
    }

    private String csvEscape(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
