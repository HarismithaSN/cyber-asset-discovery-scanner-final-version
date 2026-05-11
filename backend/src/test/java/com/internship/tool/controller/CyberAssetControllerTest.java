package com.internship.tool.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.internship.tool.entity.CyberAsset;
import com.internship.tool.exception.ResourceNotFoundException;
import com.internship.tool.service.CyberAssetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 10 MockMvc integration tests for CyberAssetController.
 * Covers: GET /all, GET /{id}, POST /create, PUT /{id},
 *         DELETE /{id}, GET /search, GET /stats, GET /high-risk,
 *         404 on missing ID, 403 for non-admin writes.
 * Run with: mvn test -Dgroups=integration
 */
@Tag("integration")
@WebMvcTest(CyberAssetController.class)
class CyberAssetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CyberAssetService assetService;

    @MockBean
    private com.internship.tool.security.JwtUtil jwtUtil;

    @MockBean
    private com.internship.tool.security.JwtAuthFilter jwtAuthFilter;

    @Autowired
    private ObjectMapper objectMapper;

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

    // ─── Test 1: GET /all paginated ───────────────────────────────────────────

    @Test
    @DisplayName("1. GET /api/assets/all — returns paginated assets (200)")
    @WithMockUser(roles = "USER")
    void getAllAssets_success() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        CyberAsset asset2 = new CyberAsset("Database Server", "SERVER", "ACTIVE");
        asset2.setId(2L);
        Page<CyberAsset> page = new PageImpl<>(Arrays.asList(sampleAsset, asset2), pageable, 2);

        when(assetService.getAllAssets(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/assets/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].assetName").value("Web Server 01"))
                .andExpect(jsonPath("$.totalElements").value(2));

        verify(assetService).getAllAssets(any(Pageable.class));
    }

    // ─── Test 2: GET /{id} found ──────────────────────────────────────────────

    @Test
    @DisplayName("2. GET /api/assets/{id} — returns asset by ID (200)")
    @WithMockUser(roles = "USER")
    void getAssetById_success() throws Exception {
        when(assetService.getAssetById(1L)).thenReturn(sampleAsset);

        mockMvc.perform(get("/api/assets/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.assetName").value("Web Server 01"))
                .andExpect(jsonPath("$.assetType").value("SERVER"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        verify(assetService).getAssetById(1L);
    }

    // ─── Test 3: GET /{id} not found — 404 ───────────────────────────────────

    @Test
    @DisplayName("3. GET /api/assets/{id} — returns 404 when asset not found")
    @WithMockUser(roles = "USER")
    void getAssetById_notFound() throws Exception {
        when(assetService.getAssetById(999L))
                .thenThrow(new ResourceNotFoundException("CyberAsset not found with id: 999"));

        mockMvc.perform(get("/api/assets/999"))
                .andExpect(status().isNotFound());

        verify(assetService).getAssetById(999L);
    }

    // ─── Test 4: POST /create 201 ─────────────────────────────────────────────

    @Test
    @DisplayName("4. POST /api/assets/create — creates new asset (201)")
    @WithMockUser(roles = "ADMIN")
    void createAsset_success() throws Exception {
        CyberAsset newAsset = new CyberAsset("New Server", "SERVER", "ACTIVE");
        newAsset.setIpAddress("192.168.1.11");

        when(assetService.createAsset(any(CyberAsset.class))).thenReturn(sampleAsset);

        mockMvc.perform(post("/api/assets/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newAsset))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1));

        verify(assetService).createAsset(any(CyberAsset.class));
    }

    // ─── Test 5: POST /create 403 for non-admin ───────────────────────────────

    @Test
    @DisplayName("5. POST /api/assets/create — 403 for USER role")
    @WithMockUser(roles = "USER")
    void createAsset_accessDenied() throws Exception {
        CyberAsset newAsset = new CyberAsset("New Server", "SERVER", "ACTIVE");

        mockMvc.perform(post("/api/assets/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newAsset))
                        .with(csrf()))
                .andExpect(status().isForbidden());

        verify(assetService, never()).createAsset(any());
    }

    // ─── Test 6: PUT /{id} update ─────────────────────────────────────────────

    @Test
    @DisplayName("6. PUT /api/assets/{id} — updates asset successfully (200)")
    @WithMockUser(roles = "ADMIN")
    void updateAsset_success() throws Exception {
        CyberAsset updatedAsset = new CyberAsset("Updated Server", "WORKSTATION", "INACTIVE");
        updatedAsset.setId(1L);

        when(assetService.updateAsset(eq(1L), any(CyberAsset.class))).thenReturn(updatedAsset);

        mockMvc.perform(put("/api/assets/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedAsset))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assetName").value("Updated Server"))
                .andExpect(jsonPath("$.assetType").value("WORKSTATION"));

        verify(assetService).updateAsset(eq(1L), any(CyberAsset.class));
    }

    // ─── Test 7: DELETE /{id} soft delete ─────────────────────────────────────

    @Test
    @DisplayName("7. DELETE /api/assets/{id} — soft deletes asset (204)")
    @WithMockUser(roles = "ADMIN")
    void deleteAsset_success() throws Exception {
        doNothing().when(assetService).deleteAsset(1L);

        mockMvc.perform(delete("/api/assets/1").with(csrf()))
                .andExpect(status().isNoContent());

        verify(assetService).deleteAsset(1L);
    }

    // ─── Test 8: GET /search?q= ───────────────────────────────────────────────

    @Test
    @DisplayName("8. GET /api/assets/search?q= — returns matching assets (200)")
    @WithMockUser(roles = "USER")
    void searchAssets_success() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        Page<CyberAsset> page = new PageImpl<>(List.of(sampleAsset), pageable, 1);

        when(assetService.searchAssets(eq("web"), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/assets/search").param("q", "web"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].assetName").value("Web Server 01"))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(assetService).searchAssets(eq("web"), any(Pageable.class));
    }

    // ─── Test 9: GET /stats ───────────────────────────────────────────────────

    @Test
    @DisplayName("9. GET /api/assets/stats — returns statistics map (200)")
    @WithMockUser(roles = "USER")
    void getStats_success() throws Exception {
        Map<String, Long> stats = Map.of("total", 30L, "ACTIVE", 25L, "INACTIVE", 5L);

        when(assetService.getAssetStatistics()).thenReturn(stats);

        mockMvc.perform(get("/api/assets/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(30))
                .andExpect(jsonPath("$.ACTIVE").value(25));

        verify(assetService).getAssetStatistics();
    }

    // ─── Test 10: GET /high-risk ──────────────────────────────────────────────

    @Test
    @DisplayName("10. GET /api/assets/high-risk — returns high risk assets (200)")
    @WithMockUser(roles = "USER")
    void getHighRiskAssets_success() throws Exception {
        CyberAsset highRisk = new CyberAsset("Vuln Server", "SERVER", "ACTIVE");
        highRisk.setId(5L);
        highRisk.setRiskScore(85);

        when(assetService.getHighRiskAssets()).thenReturn(List.of(highRisk));

        mockMvc.perform(get("/api/assets/high-risk"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].riskScore").value(85))
                .andExpect(jsonPath("$[0].assetName").value("Vuln Server"));

        verify(assetService).getHighRiskAssets();
    }
}