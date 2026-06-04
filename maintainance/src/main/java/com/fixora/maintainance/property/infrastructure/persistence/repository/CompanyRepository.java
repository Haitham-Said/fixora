package com.fixora.maintainance.property.infrastructure.persistence.repository;

import com.fixora.maintainance.property.domain.model.Company;
import com.fixora.maintainance.property.domain.model.requests.CompanyRequest;
import com.fixora.maintainance.property.domain.repository.ICompanyRepository;
import com.fixora.maintainance.property.infrastructure.persistence.mapper.CompanyMapper;
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
        com.fixora.maintainance.property.infrastructure.entity.Company company=new com.fixora.maintainance.property.infrastructure.entity.Company();
        company.setAddress(companyRequest.getAddress());
        company.setEmail(companyRequest.getEmail());
        company.setName(companyRequest.getName());
        company.setPhone(companyRequest.getPhone());
        company.setCompanyCode(companyRequest.getCompanyCode());
        company.setType(companyRequest.getType() != null
                ? companyRequest.getType()
                : com.fixora.maintainance.property.domain.model.CompanyType.PROPERTY_MANAGEMENT);
        companyJPARepository.save(company);
        return CompanyMapper.toDomain(company);
    }

    @Override
    public Company findByCompanyCode(String companyCode) {
        com.fixora.maintainance.property.infrastructure.entity.Company companyEntity = companyJPARepository.findByCompanyCode(companyCode);
        if (companyEntity == null) {
            return null;
        }
        return CompanyMapper.toDomain(companyEntity);
    }

    @Override
    public java.util.Optional<Company> findById(long companyId) {
        return companyJPARepository.findById(companyId).map(CompanyMapper::toDomain);
    }
}

