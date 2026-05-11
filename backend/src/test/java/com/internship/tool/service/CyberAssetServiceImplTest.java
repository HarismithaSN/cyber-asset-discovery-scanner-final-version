package com.internship.tool.service;

import com.internship.tool.entity.CyberAsset;
import com.internship.tool.exception.AssetValidationException;
import com.internship.tool.exception.DuplicateResourceException;
import com.internship.tool.exception.ResourceNotFoundException;
import com.internship.tool.repository.CyberAssetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 11 JUnit 5 unit tests for CyberAssetServiceImpl using Mockito.
 * Run with: mvn test -Dgroups=unit
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
class CyberAssetServiceImplTest {

    @Mock
    private CyberAssetRepository assetRepository;

    @InjectMocks
    private CyberAssetServiceImpl assetService;

    private CyberAsset sampleAsset;

    @BeforeEach
    void setUp() {
        sampleAsset = new CyberAsset("Web Server 01", "SERVER", "ACTIVE");
        sampleAsset.setId(1L);
        sampleAsset.setIpAddress("192.168.1.10");
        sampleAsset.setRiskScore(45);
        sampleAsset.setHostname("web-server-01.local");
        sampleAsset.setOwner("Security Team");
        sampleAsset.setDepartment("IT");
    }

    // ─── Test 1: Create Asset Success ──────────────────────────────────────────

    @Test
    @DisplayName("1. createAsset — saves and returns new asset successfully")
    void createAsset_success() {
        when(assetRepository.existsByIpAddressAndDeletedFalse("192.168.1.10")).thenReturn(false);
        when(assetRepository.save(any(CyberAsset.class))).thenReturn(sampleAsset);

        CyberAsset result = assetService.createAsset(sampleAsset);

        assertNotNull(result);
        assertEquals("Web Server 01", result.getAssetName());
        assertEquals("SERVER", result.getAssetType());
        verify(assetRepository).save(sampleAsset);
    }

    // ─── Test 2: Create Asset Duplicate IP ─────────────────────────────────────

    @Test
    @DisplayName("2. createAsset — throws DuplicateResourceException for duplicate IP")
    void createAsset_duplicateIp_throwsException() {
        when(assetRepository.existsByIpAddressAndDeletedFalse("192.168.1.10")).thenReturn(true);

        DuplicateResourceException exception = assertThrows(
                DuplicateResourceException.class,
                () -> assetService.createAsset(sampleAsset)
        );

        assertTrue(exception.getMessage().contains("192.168.1.10"));
        verify(assetRepository, never()).save(any());
    }

    // ─── Test 3: Create Asset Invalid Name ─────────────────────────────────────

    @Test
    @DisplayName("3. createAsset — throws AssetValidationException for blank name")
    void createAsset_invalidName_throwsValidation() {
        CyberAsset invalidAsset = new CyberAsset("", "SERVER", "ACTIVE");

        assertThrows(
                AssetValidationException.class,
                () -> assetService.createAsset(invalidAsset)
        );

        verify(assetRepository, never()).save(any());
    }

    // ─── Test 4: Get Asset By ID Found ─────────────────────────────────────────

    @Test
    @DisplayName("4. getAssetById — returns existing asset")
    void getAssetById_found() {
        when(assetRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(sampleAsset));

        CyberAsset result = assetService.getAssetById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Web Server 01", result.getAssetName());
    }

    // ─── Test 5: Get Asset By ID Not Found ─────────────────────────────────────

    @Test
    @DisplayName("5. getAssetById — throws ResourceNotFoundException when not found")
    void getAssetById_notFound_throwsException() {
        when(assetRepository.findByIdAndDeletedFalse(999L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> assetService.getAssetById(999L)
        );

        assertTrue(exception.getMessage().contains("999"));
    }

    // ─── Test 6: Get All Assets Paginated ──────────────────────────────────────

    @Test
    @DisplayName("6. getAllAssets — returns paginated results")
    void getAllAssets_returnsPaginatedResults() {
        Pageable pageable = PageRequest.of(0, 10);
        CyberAsset asset2 = new CyberAsset("Database Server", "SERVER", "ACTIVE");
        asset2.setId(2L);
        Page<CyberAsset> page = new PageImpl<>(Arrays.asList(sampleAsset, asset2), pageable, 2);

        when(assetRepository.findAllByDeletedFalse(pageable)).thenReturn(page);

        Page<CyberAsset> result = assetService.getAllAssets(pageable);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());
    }

    // ─── Test 7: Update Asset Success ──────────────────────────────────────────

    @Test
    @DisplayName("7. updateAsset — updates all fields successfully")
    void updateAsset_success() {
        CyberAsset updateDetails = new CyberAsset("Updated Server", "WORKSTATION", "INACTIVE");
        updateDetails.setIpAddress("10.0.0.1");
        updateDetails.setOwner("Dev Team");
        updateDetails.setRiskScore(80);

        when(assetRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(sampleAsset));
        when(assetRepository.save(any(CyberAsset.class))).thenReturn(sampleAsset);

        CyberAsset result = assetService.updateAsset(1L, updateDetails);

        assertNotNull(result);
        assertEquals("Updated Server", sampleAsset.getAssetName());
        assertEquals("WORKSTATION", sampleAsset.getAssetType());
        assertEquals("INACTIVE", sampleAsset.getStatus());
        verify(assetRepository).save(sampleAsset);
    }

    // ─── Test 8: Delete Asset Soft Deletes ─────────────────────────────────────

    @Test
    @DisplayName("8. deleteAsset — sets deleted flag to true (soft delete)")
    void deleteAsset_softDeletes() {
        when(assetRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(sampleAsset));
        when(assetRepository.save(any(CyberAsset.class))).thenReturn(sampleAsset);

        assertFalse(sampleAsset.isDeleted());

        assetService.deleteAsset(1L);

        assertTrue(sampleAsset.isDeleted());
        verify(assetRepository).save(sampleAsset);
    }

    // ─── Test 9: Get High Risk Assets ──────────────────────────────────────────

    @Test
    @DisplayName("9. getHighRiskAssets — returns only assets with risk >= 70")
    void getHighRiskAssets_returnsFiltered() {
        CyberAsset highRisk1 = new CyberAsset("Vuln Server", "SERVER", "ACTIVE");
        highRisk1.setRiskScore(85);
        CyberAsset highRisk2 = new CyberAsset("Old Router", "ROUTER", "ACTIVE");
        highRisk2.setRiskScore(92);

        when(assetRepository.findHighRiskAssets()).thenReturn(Arrays.asList(highRisk1, highRisk2));

        List<CyberAsset> result = assetService.getHighRiskAssets();

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(a -> a.getRiskScore() >= 70));
    }

    // ─── Test 10: Get Asset Statistics ─────────────────────────────────────────

    @Test
    @DisplayName("10. getAssetStatistics — returns correct counts per status")
    void getAssetStatistics_returnsCorrectCounts() {
        when(assetRepository.countByDeletedFalse()).thenReturn(25L);
        when(assetRepository.countByStatusGrouped()).thenReturn(Arrays.asList(
                new Object[]{"ACTIVE", 15L},
                new Object[]{"INACTIVE", 7L},
                new Object[]{"UNKNOWN", 3L}
        ));

        Map<String, Long> stats = assetService.getAssetStatistics();

        assertEquals(25L, stats.get("total"));
        assertEquals(15L, stats.get("ACTIVE"));
        assertEquals(7L, stats.get("INACTIVE"));
        assertEquals(3L, stats.get("UNKNOWN"));
        assertEquals(4, stats.size()); // total + 3 statuses
    }

    // ─── Test 11: Create Asset Null Type ───────────────────────────────────────

    @Test
    @DisplayName("11. createAsset — throws AssetValidationException when assetType is null")
    void createAsset_nullType_throwsValidation() {
        CyberAsset invalidAsset = new CyberAsset("Valid Name", null, "ACTIVE");

        assertThrows(
                AssetValidationException.class,
                () -> assetService.createAsset(invalidAsset)
        );

        verify(assetRepository, never()).save(any());
    }
}
