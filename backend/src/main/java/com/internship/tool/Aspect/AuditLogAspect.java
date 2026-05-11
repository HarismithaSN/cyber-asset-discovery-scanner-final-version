package com.internship.tool.aspect;

import com.internship.tool.model.AuditLog;
import com.internship.tool.repository.AuditLogRepository;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AuditLogAspect {

    private final AuditLogRepository auditLogRepository;

    public AuditLogAspect(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @AfterReturning("execution(* com.internship.tool.controller.AssetController.addAsset(..))")
    public void logCreate(JoinPoint joinPoint) {
        saveLog("CREATE", joinPoint);
    }

    @AfterReturning("execution(* com.internship.tool.controller.AssetController.updateAsset(..))")
    public void logUpdate(JoinPoint joinPoint) {
        saveLog("UPDATE", joinPoint);
    }

    @AfterReturning("execution(* com.internship.tool.controller.AssetController.deleteAsset(..))")
    public void logDelete(JoinPoint joinPoint) {
        saveLog("DELETE", joinPoint);
    }

    private void saveLog(String action, JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();

        AuditLog log = new AuditLog();
        log.setAction(action);
        log.setEntityName("Asset");
        log.setMethodName(methodName);
        log.setDescription(action + " operation performed on Asset using method: " + methodName);

        auditLogRepository.save(log);
    }
}