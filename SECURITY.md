# Security Policy and Final Review

## Executive Summary
This document outlines the security posture, testing procedures, and threat mitigation strategies for the Cyber Asset Discovery Scanner tool.

## Threats and Mitigations
1. **Prompt Injection**
   - *Mitigation*: Sanitisation middleware on the AI Flask layer filters and detects injections, rejecting malicious input.
2. **SQL Injection**
   - *Mitigation*: Use of Hibernate ORM/JPA to parameterize queries automatically.
3. **Cross-Site Scripting (XSS)**
   - *Mitigation*: React's dynamic DOM operations automatically escape variables, preventing XSS.
4. **Unauthorized Access**
   - *Mitigation*: Implementation of JWT-based authentication combined with Spring Security and Role-Based Access Control (RBAC).

## Final Checklist & Team Sign-Off
- [x] JWT, rate limiting, and all injection tests verified
- [x] PII audit confirms no personal data in prompts
- [x] All High/Critical ZAP active scan findings resolved
- [x] No plaintext secrets (verified visually during team code review)

## Residual Risks
- System assumes redis/postgres backend runs on a securely segmented internal network.

**Signed off by Team members: Java Developer 1, Java Developer 2, AI Developer 1, AI Developer 2.**
