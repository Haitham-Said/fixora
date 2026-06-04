package com.fixora.maintainance.whatsapp.application;

import com.fixora.maintainance.whatsapp.domain.model.InboundMessage;
import com.fixora.maintainance.whatsapp.domain.model.MaintainerContext;
import com.fixora.maintainance.whatsapp.domain.model.TenantContext;
import org.springframework.stereotype.Component;

@Component
public class ConversationParticipantResolver {

    private final TenantResolver tenantResolver;
    private final MaintainerResolver maintainerResolver;

    public ConversationParticipantResolver(TenantResolver tenantResolver, MaintainerResolver maintainerResolver) {
        this.tenantResolver = tenantResolver;
        this.maintainerResolver = maintainerResolver;
    }

    public Resolution resolve(InboundMessage inbound) {
        TenantContext tenant = tenantResolver.resolve(inbound.getFromPhone());
        if (tenant != null) {
            return new Resolution(ParticipantType.TENANT, tenant, null);
        }

        MaintainerContext maintainer = maintainerResolver.resolve(inbound.getFromPhone());
        if (maintainer != null) {
            return new Resolution(ParticipantType.MAINTAINER, null, maintainer);
        }

        return new Resolution(ParticipantType.UNKNOWN, null, null);
    }

    public enum ParticipantType {
        TENANT,
        MAINTAINER,
        UNKNOWN
    }

    public record Resolution(ParticipantType type, TenantContext tenantContext, MaintainerContext maintainerContext) {
    }
}

