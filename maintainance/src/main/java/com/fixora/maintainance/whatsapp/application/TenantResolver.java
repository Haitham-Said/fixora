package com.fixora.maintainance.whatsapp.application;

import com.fixora.maintainance.whatsapp.domain.model.TenantContext;
import com.fixora.maintainance.user.infrastructure.entity.UserEntity;
import com.fixora.maintainance.user.infrastructure.entity.customer.Customer;
import com.fixora.maintainance.user.infrastructure.repository.CustomerJpaRepository;
import com.fixora.maintainance.user.infrastructure.repository.UserJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Resolves sender phone -> TenantContext (tenantId, apartmentId, buildingId, companyId, role).
 * Returns null if not found (unregistered number).
 */
@Component
public class TenantResolver {

    private static final String ROLE_CUSTOMER = "CUSTOMER";

    private final UserJpaRepository userJpaRepository;
    private final CustomerJpaRepository customerJpaRepository;

    public TenantResolver(UserJpaRepository userJpaRepository, CustomerJpaRepository customerJpaRepository) {
        this.userJpaRepository = userJpaRepository;
        this.customerJpaRepository = customerJpaRepository;
    }

    public TenantContext resolve(String fromPhone) {
        String normalized = MessageNormalizer.normalizePhone(fromPhone);
        if (normalized.isEmpty()) return null;

        Optional<UserEntity> userOpt = userJpaRepository.findByPhoneAndRole(normalized, ROLE_CUSTOMER);
        if (userOpt.isEmpty()) {
            userOpt = tryAlternativePhoneFormats(normalized);
        }
        if (userOpt.isEmpty()) return null;

        UserEntity user = userOpt.get();
        Optional<Customer> customerOpt = customerJpaRepository.findById(user.getId());
        if (customerOpt.isEmpty()) return null;

        Customer customer = customerOpt.get();
        if (customer.getApartment() == null || customer.getApartment().getBuilding() == null) {
            return null;
        }

        Long companyId = customer.getApartment().getBuilding().getCompany() != null
                ? customer.getApartment().getBuilding().getCompany().getId()
                : null;

        return TenantContext.builder()
                .tenantId(user.getId())
                .apartmentId(customer.getApartment().getId())
                .buildingId(customer.getApartment().getBuilding().getId())
                .companyId(companyId)
                .role(ROLE_CUSTOMER)
                .build();
    }

    private Optional<UserEntity> tryAlternativePhoneFormats(String normalized) {
        String withPlus = normalized.startsWith("+") ? normalized : "+" + normalized;
        String withoutPlus = normalized.startsWith("+") ? normalized.substring(1) : normalized;
        Optional<UserEntity> u = userJpaRepository.findByPhoneAndRole(withPlus, ROLE_CUSTOMER);
        if (u.isPresent()) return u;
        return userJpaRepository.findByPhoneAndRole(withoutPlus, ROLE_CUSTOMER);
    }
}

