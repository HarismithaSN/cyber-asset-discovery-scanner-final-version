package com.internship.tool.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Enables JPA Auditing so that @CreatedDate and @LastModifiedDate
 * on CyberAsset (and future entities) are automatically populated.
 */
@Configuration
@EnableJpaAuditing
public class JpaConfig {
}
