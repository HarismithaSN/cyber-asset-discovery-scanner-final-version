package com.internship.tool.config;

import com.internship.tool.entity.CyberAsset;
import com.internship.tool.entity.User;
import com.internship.tool.repository.CyberAssetRepository;
import com.internship.tool.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class DataSeeder {

    @Bean
    @Transactional
    public CommandLineRunner initData(CyberAssetRepository assetRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.count() == 0) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setEmail("admin@example.com");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRole("ROLE_ADMIN");
                userRepository.save(admin);
            }

            if (assetRepository.count() == 0) {
                List<CyberAsset> assets = new ArrayList<>();
                for (int i = 1; i <= 30; i++) {
                    CyberAsset asset = new CyberAsset();
                    asset.setAssetName("Asset " + i);
                    asset.setAssetType(i % 3 == 0 ? "SERVER" : (i % 2 == 0 ? "WORKSTATION" : "ROUTER"));
                    asset.setStatus(i % 5 == 0 ? "INACTIVE" : "ACTIVE");
                    asset.setIpAddress("192.168.1." + i);
                    asset.setMacAddress("00:1B:44:11:3A:B" + (i % 10));
                    asset.setOperatingSystem(i % 2 == 0 ? "Ubuntu 22.04" : "Windows Server 2022");
                    asset.setHostname("host-" + i);
                    asset.setLocation(i % 2 == 0 ? "Data Center A" : "Office B");
                    asset.setOwner("Admin");
                    asset.setDepartment("IT");
                    asset.setRiskScore((int) (Math.random() * 100));
                    asset.setOpenPorts("80, 443, 22");
                    asset.setLastScannedAt(LocalDateTime.now().minusDays(i % 5));
                    assets.add(asset);
                }
                assetRepository.saveAll(assets);
                System.out.println("Seeded 30 realistic demo cyber assets.");
            }
        };
    }
}
