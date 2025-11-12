package com.fixora.maintainance.user.infrastructure.repository;

import com.fixora.maintainance.user.domain.model.Maintainer;
import com.fixora.maintainance.user.domain.model.User;
import com.fixora.maintainance.user.domain.repositories.IMaintainerRepository;
import com.fixora.maintainance.user.infrastructure.entity.maintainer.MaintainerEntity;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;
import com.fixora.maintainance.user.infrastructure.mapper.MaintainerMapper;

@Repository
public class MaintainerRepository implements IMaintainerRepository {

    private final MaintainerJpaRepository maintainerJpaRepository;

    public MaintainerRepository(MaintainerJpaRepository maintainerJpaRepository) {
        this.maintainerJpaRepository = maintainerJpaRepository;
    }

    @Transactional
    public Maintainer addMaintainer(User user){
        MaintainerEntity maintainer=new MaintainerEntity();
        maintainer.setUserId(user.getId());
        maintainerJpaRepository.save(maintainer);
        return MaintainerMapper.toDomainMaintainer(maintainer);

    }
}
