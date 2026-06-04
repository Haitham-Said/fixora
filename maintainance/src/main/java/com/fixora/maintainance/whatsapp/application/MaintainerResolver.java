package com.fixora.maintainance.whatsapp.application;

import com.fixora.maintainance.user.infrastructure.entity.UserEntity;
import com.fixora.maintainance.user.infrastructure.repository.UserJpaRepository;
import com.fixora.maintainance.whatsapp.domain.model.MaintainerContext;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Resolves sender phone -> MaintainerContext.
 * Returns null if sender is not a maintainer.
 */
@Component
public class MaintainerResolver {

    private static final String ROLE_MAINTAINER = "MAINTAINER";

    private final UserJpaRepository userJpaRepository;

    public MaintainerResolver(UserJpaRepository userJpaRepository) {
        this.userJpaRepository = userJpaRepository;
    }

    public MaintainerContext resolve(String fromPhone) {
        String normalized = MessageNormalizer.normalizePhone(fromPhone);
        if (normalized.isEmpty()) return null;

        Optional<UserEntity> userOpt = userJpaRepository.findByPhoneAndRole(normalized, ROLE_MAINTAINER);
        if (userOpt.isEmpty()) {
            userOpt = tryAlternativePhoneFormats(normalized);
        }
        if (userOpt.isEmpty()) return null;

        UserEntity user = userOpt.get();
        Long companyId = user.getCompany() != null ? user.getCompany().getId() : null;
        return MaintainerContext.builder()
                .maintainerUserId(user.getId())
                .companyId(companyId)
                .role(ROLE_MAINTAINER)
                .build();
    }

    private Optional<UserEntity> tryAlternativePhoneFormats(String normalized) {
        String withPlus = normalized.startsWith("+") ? normalized : "+" + normalized;
        String withoutPlus = normalized.startsWith("+") ? normalized.substring(1) : normalized;
        Optional<UserEntity> u = userJpaRepository.findByPhoneAndRole(withPlus, ROLE_MAINTAINER);
        if (u.isPresent()) return u;
        return userJpaRepository.findByPhoneAndRole(withoutPlus, ROLE_MAINTAINER);
    }
}

