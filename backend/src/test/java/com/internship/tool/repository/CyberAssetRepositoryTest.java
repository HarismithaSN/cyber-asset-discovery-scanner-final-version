package com.internship.tool.repository;

import com.internship.tool.entity.CyberAsset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 8 JUnit 5 integration tests for CyberAssetRepository using @DataJpaTest.
 * Run with: mvn test -Dgroups=integration
 */
@Tag("integration")
@DataJpaTest
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"
})
class CyberAssetRepositoryTest {

    @Autowired
    private CyberAssetRepository assetRepository;

    private CyberAsset sampleAsset;

    @BeforeEach
    void setUp() {
        sampleAsset = new CyberAsset("Web Server 01", "SERVER", "ACTIVE");
        sampleAsset.setIpAddress("192.168.1.10");
        sampleAsset.setRiskScore(45);
        sampleAsset.setHostname("web-server-01.local");
        sampleAsset.setOwner("Security Team");
        sampleAsset.setDepartment("IT");
    }

    // ─── Test 1: Save Asset ────────────────────────────────────────────────────

    @Test
    @DisplayName("1. save — persists asset to database")
    void saveAsset_success() {
        CyberAsset savedAsset = assetRepository.save(sampleAsset);

        assertNotNull(savedAsset.getId());
        assertEquals("Web Server 01", savedAsset.getAssetName());
        assertEquals("SERVER", savedAsset.getAssetType());
    }

    // ─── Test 2: Find By ID And Deleted False ──────────────────────────────────

    @Test
    @DisplayName("2. findByIdAndDeletedFalse — returns asset when not deleted")
    void findByIdAndDeletedFalse_found() {
        CyberAsset savedAsset = assetRepository.save(sampleAsset);

        Optional<CyberAsset> result = assetRepository.findByIdAndDeletedFalse(savedAsset.getId());

        assertTrue(result.isPresent());
        assertEquals("Web Server 01", result.get().getAssetName());
    }

    // ─── Test 3: Find By ID And Deleted False — Not Found ──────────────────────

    @Test
    @DisplayName("3. findByIdAndDeletedFalse — returns empty when deleted")
    void findByIdAndDeletedFalse_notFound() {
        CyberAsset savedAsset = assetRepository.save(sampleAsset);
        savedAsset.setDeleted(true);
        assetRepository.save(savedAsset);

        Optional<CyberAsset> result = assetRepository.findByIdAndDeletedFalse(savedAsset.getId());

        assertFalse(result.isPresent());
    }

    // ─── Test 4: Find All By Deleted False ─────────────────────────────────────

    @Test
    @DisplayName("4. findAllByDeletedFalse — returns only non-deleted assets")
    void findAllByDeletedFalse_returnsNonDeleted() {
        CyberAsset asset1 = assetRepository.save(sampleAsset);

        CyberAsset asset2 = new CyberAsset("Database Server", "SERVER", "ACTIVE");
        asset2.setIpAddress("192.168.1.11");
        asset2 = assetRepository.save(asset2);

        CyberAsset asset3 = new CyberAsset("Old Router", "ROUTER", "INACTIVE");
        asset3.setDeleted(true);
        asset3 = assetRepository.save(asset3);

        Pageable pageable = PageRequest.of(0, 10);
        Page<CyberAsset> result = assetRepository.findAllByDeletedFalse(pageable);

        assertEquals(2, result.getTotalElements());
        assertTrue(result.getContent().stream().allMatch(a -> !a.isDeleted()));
    }

    // ─── Test 5: Find By Status And Deleted False ───────────────────────────────

    @Test
    @DisplayName("5. findAllByStatusAndDeletedFalse — filters by status")
    void findByStatusAndDeletedFalse_returnsFiltered() {
        CyberAsset activeAsset = assetRepository.save(sampleAsset);

        CyberAsset inactiveAsset = new CyberAsset("Old Device", "WORKSTATION", "INACTIVE");
        inactiveAsset = assetRepository.save(inactiveAsset);

        Pageable pageable = PageRequest.of(0, 10);
        Page<CyberAsset> result = assetRepository.findAllByStatusAndDeletedFalse("ACTIVE", pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("ACTIVE", result.getContent().get(0).getStatus());
    }

    // ─── Test 6: Find By Duplicate IP ──────────────────────────────────────────

    @Test
    @DisplayName("6. existsByIpAddressAndDeletedFalse — returns true for existing IP")
    void existsByIpAddressAndDeletedFalse_found() {
        assetRepository.save(sampleAsset);

        boolean exists = assetRepository.existsByIpAddressAndDeletedFalse("192.168.1.10");

        assertTrue(exists);
    }

    // ─── Test 7: Find High Risk Assets ─────────────────────────────────────────

    @Test
    @DisplayName("7. findHighRiskAssets — returns assets with risk score >= 70")
    void findHighRiskAssets_returnsFiltered() {
        CyberAsset lowRisk = assetRepository.save(sampleAsset);

        CyberAsset highRisk1 = new CyberAsset("Vulnerable Server", "SERVER", "ACTIVE");
        highRisk1.setRiskScore(85);
        highRisk1 = assetRepository.save(highRisk1);

        CyberAsset highRisk2 = new CyberAsset("Old Router", "ROUTER", "ACTIVE");
        highRisk2.setRiskScore(92);
        highRisk2 = assetRepository.save(highRisk2);

        var result = assetRepository.findHighRiskAssets();

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(a -> a.getRiskScore() >= 70));
    }

    // ─── Test 8: Count All By Deleted False ────────────────────────────────────

    @Test
    @DisplayName("8. countByDeletedFalse — counts non-deleted assets correctly")
    void countByDeletedFalse_returnsCorrectCount() {
        assetRepository.save(sampleAsset);
        assetRepository.save(sampleAsset);

        CyberAsset deletedAsset = new CyberAsset("Deleted Server", "SERVER", "ACTIVE");
        deletedAsset.setDeleted(true);
        assetRepository.save(deletedAsset);

        long count = assetRepository.countByDeletedFalse();

        assertEquals(2, count);
    }
}