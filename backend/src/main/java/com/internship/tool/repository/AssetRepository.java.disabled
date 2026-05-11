package com.internship.tool.repository;

import com.internship.tool.model.Asset;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AssetRepository extends JpaRepository<Asset, Long> {

    long countByStatusIgnoreCase(String status);

    long countByRiskScoreGreaterThanEqual(int riskScore);

    @Query("""
        SELECT a FROM Asset a
        WHERE
        (:q IS NULL OR :q = '' OR
            LOWER(a.name) LIKE LOWER(CONCAT('%', :q, '%')) OR
            LOWER(a.type) LIKE LOWER(CONCAT('%', :q, '%')) OR
            LOWER(a.ipAddress) LIKE LOWER(CONCAT('%', :q, '%'))
        )
        AND (:status IS NULL OR :status = '' OR LOWER(a.status) = LOWER(:status))
    """)
    Page<Asset> searchAssets(
            @Param("q") String q,
            @Param("status") String status,
            Pageable pageable
    );
}