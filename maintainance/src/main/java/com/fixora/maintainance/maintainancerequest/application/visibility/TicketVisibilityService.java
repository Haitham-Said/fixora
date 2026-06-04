package com.fixora.maintainance.maintainancerequest.application.visibility;

import com.fixora.maintainance.user.domain.model.Role;
import com.fixora.security.application.model.UserInfo;
import org.springframework.stereotype.Service;

/**
 * Centralizes portal ticket visibility by PM/FM company linkage on tickets.
 */
@Service
public class TicketVisibilityService {

    public PortalTicketScope scopeFor(UserInfo user) {
        if (user == null || user.role() == null) {
            throw new IllegalArgumentException("Missing user context");
        }
        if ("OPERATION".equalsIgnoreCase(user.role())) {
            return PortalTicketScope.all();
        }
        if (user.companyId() == null) {
            throw new IllegalArgumentException("Portal user must have companyId");
        }
        return switch (Role.valueOf(user.role())) {
            case ADMIN -> PortalTicketScope.pmCompany(user.companyId());
            case FM_ADMIN -> PortalTicketScope.fmCompany(user.companyId());
            default -> throw new IllegalArgumentException("Role cannot list portal tickets: " + user.role());
        };
    }

    public record PortalTicketScope(
            boolean operationAll,
            Long pmCompanyId,
            Long fmCompanyId
    ) {
        public static PortalTicketScope all() {
            return new PortalTicketScope(true, null, null);
        }

        public static PortalTicketScope pmCompany(long pmCompanyId) {
            return new PortalTicketScope(false, pmCompanyId, null);
        }

        public static PortalTicketScope fmCompany(long fmCompanyId) {
            return new PortalTicketScope(false, null, fmCompanyId);
        }
    }
}
