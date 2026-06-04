package com.fixora.maintainance.user.infrastructure.repository;

import com.fixora.maintainance.user.infrastructure.entity.UserEntity;
import com.fixora.maintainance.user.infrastructure.entity.maintainer.MaintainerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByPhoneAndRole(String phone, String role);

    List<UserEntity> findByStatus(String status);
    
    Optional<UserEntity> findByIdAndStatus(Long id, String status);

    @Query(value = """
            SELECT m.* FROM maintainers m
                JOIN users u ON u.id = m.user_id
                WHERE u.company_id = :id
                AND NOT EXISTS (
                    SELECT 1 FROM maintenance_requests r
                    WHERE r.maintainer_id = m.user_id
                    AND r.preferred_time = :slot
                    AND DATE(r.updated_at) = CURRENT_DATE
                )
            """, nativeQuery = true)
    List<MaintainerEntity> findAvailableMaintainersForSlotAndCompany(@Param("slot") String preferredSlot, @Param("id") Long id);
}
