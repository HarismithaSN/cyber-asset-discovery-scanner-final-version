package com.internship.tool.repository;

import com.internship.tool.entity.CyberAsset;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CyberAssetRepository extends JpaRepository<CyberAsset, Long> {

    // ─── Soft-delete aware finders ────────────────────────────────────────────

    Optional<CyberAsset> findByIdAndDeletedFalse(Long id);

    Page<CyberAsset> findAllByDeletedFalse(Pageable pageable);

    Page<CyberAsset> findAllByStatusAndDeletedFalse(String status, Pageable pageable);

    Page<CyberAsset> findAllByAssetTypeAndDeletedFalse(String assetType, Pageable pageable);

    // ─── Search ───────────────────────────────────────────────────────────────

    @Query("""
        SELECT a FROM CyberAsset a
        WHERE a.deleted = false
          AND (
            LOWER(a.assetName)   LIKE LOWER(CONCAT('%', :q, '%')) OR
            LOWER(a.hostname)    LIKE LOWER(CONCAT('%', :q, '%')) OR
            LOWER(a.ipAddress)   LIKE LOWER(CONCAT('%', :q, '%')) OR
            LOWER(a.owner)       LIKE LOWER(CONCAT('%', :q, '%')) OR
            LOWER(a.department)  LIKE LOWER(CONCAT('%', :q, '%'))
          )
        """)
    Page<CyberAsset> searchAssets(@Param("q") String query, Pageable pageable);

    // ─── Date range ───────────────────────────────────────────────────────────

    @Query("""
        SELECT a FROM CyberAsset a
        WHERE a.deleted = false
          AND a.createdAt BETWEEN :from AND :to
        """)
    Page<CyberAsset> findByCreatedAtBetween(
            @Param("from") LocalDateTime from,
            @Param("to")   LocalDateTime to,
            Pageable pageable);

    // ─── Risk score ───────────────────────────────────────────────────────────

    List<CyberAsset> findByRiskScoreGreaterThanEqualAndDeletedFalse(Integer threshold);

    @Query("""
        SELECT a FROM CyberAsset a
        WHERE a.deleted = false
          AND a.riskScore >= 70
        ORDER BY a.riskScore DESC
        """)
    List<CyberAsset> findHighRiskAssets();

    // ─── Statistics / dashboard ───────────────────────────────────────────────

    long countByStatusAndDeletedFalse(String status);

    long countByDeletedFalse();

    long countByAssetTypeAndDeletedFalse(String assetType);

    @Query("SELECT AVG(a.riskScore) FROM CyberAsset a WHERE a.deleted = false AND a.riskScore IS NOT NULL")
    Double findAverageRiskScore();

    @Query("""
        SELECT a.status, COUNT(a)
        FROM CyberAsset a
        WHERE a.deleted = false
        GROUP BY a.status
        """)
    List<Object[]> countByStatusGrouped();

    // ─── IP / hostname lookups ────────────────────────────────────────────────

    boolean existsByIpAddressAndDeletedFalse(String ipAddress);

    Optional<CyberAsset> findByIpAddressAndDeletedFalse(String ipAddress);

    // ─── AI fields ────────────────────────────────────────────────────────────

    @Query("SELECT a FROM CyberAsset a WHERE a.deleted = false AND a.aiDescription IS NULL")
    List<CyberAsset> findAssetsWithoutAiDescription();
}
