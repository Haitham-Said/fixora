package com.fixora.maintainance.maintainancerequest.infrastructure.persistence.repository;

import com.fixora.maintainance.maintainancerequest.domain.model.Company;
import com.fixora.maintainance.maintainancerequest.domain.model.requests.CompanyRequest;
import com.fixora.maintainance.maintainancerequest.domain.repository.ICompanyRepository;
import com.fixora.maintainance.maintainancerequest.infrastructure.persistence.mapper.CompanyMapper;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

@Repository
public class CompanyRepository implements ICompanyRepository {

    private final CompanyJPARepository companyJPARepository;

    public CompanyRepository(CompanyJPARepository companyJPARepository) {
        this.companyJPARepository = companyJPARepository;
    }

    @Override
    @Transactional
    public Company addCompany(CompanyRequest companyRequest) {
        com.fixora.maintainance.user.infrastructure.entity.shared.Company company=new com.fixora.maintainance.user.infrastructure.entity.shared.Company();
        company.setAddress(companyRequest.getAddress());
        company.setEmail(companyRequest.getEmail());
        company.setName(companyRequest.getName());
        company.setPhone(companyRequest.getPhone());
        companyJPARepository.save(company);
        return CompanyMapper.toDomain(company);
    }
}
