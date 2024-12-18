package org.zir.dragonieze.log;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.zir.dragonieze.openam.auth.OpenAmUserPrincipal;
import org.zir.dragonieze.services.BaseService;
import org.zir.dragonieze.user.User;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    @PersistenceContext
    private EntityManager entityManager;

    private final BaseService baseService;

    public void logAction(OpenAmUserPrincipal principal, String entityName, Long entityId,
                          String action, Object details) {
        try {
            User user = principal.getUser();

            String detailsJson = null;
            if (details != null) {
                detailsJson = baseService.convertToJson(details);
            }

            AuditLog auditLog = AuditLog.builder()
                    .entityName(entityName)
                    .entityId(entityId)
                    .action(action)
                    .changedBy(user.getUsername())
                    .changeDate(LocalDateTime.now())
                    .details(detailsJson)
                    .build();
            entityManager.persist(auditLog);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to log action for entity: " + entityName, e);
        }
    }
}
