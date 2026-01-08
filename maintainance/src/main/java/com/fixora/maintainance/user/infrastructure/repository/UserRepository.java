package com.fixora.maintainance.user.infrastructure.repository;

import com.fixora.maintainance.user.domain.model.Maintainer;
import com.fixora.maintainance.user.domain.model.request.MaintainerRequest;
import com.fixora.maintainance.user.infrastructure.entity.UserEntity;
import com.fixora.maintainance.user.domain.repositories.IUserRepository;
import com.fixora.maintainance.user.infrastructure.mapper.MaintainerMapper;
import com.fixora.maintainance.user.infrastructure.mapper.UserMapper;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import com.fixora.maintainance.property.infrastructure.entity.Company;

@Component
public class UserRepository implements IUserRepository {

    private final UserJpaRepository userJpaRepository;


    public UserRepository(UserJpaRepository userJpaRepository) {
        this.userJpaRepository = userJpaRepository;
    }

    @Override
    public Optional<UserEntity> findUserByUsername(String userName) {
        return userJpaRepository.findByEmail(userName);
    }

    public List<Maintainer> findAvailableMaintainersForSlotAndCompany(String preferredSlot,Long id){
        return userJpaRepository.findAvailableMaintainersForSlotAndCompany(preferredSlot,id).stream()
                .map(MaintainerMapper::toDomainMaintainer)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Transactional
    public com.fixora.maintainance.user.domain.model.User addUser(MaintainerRequest maintainerRequest){
        UserEntity userEntity =new UserEntity();
        userEntity.setName(maintainerRequest.getName());
        Company company=new Company();
        company.setId(maintainerRequest.getCompanyId());
        userEntity.setCompany(company);
        userEntity.setEmail(maintainerRequest.getEmail());
        userEntity.setPhone(maintainerRequest.getPhone());
        userEntity.setRole(maintainerRequest.getRole());
        userJpaRepository.save(userEntity);

        return UserMapper.toDomain(userEntity);

    }






}
