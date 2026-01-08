package com.fixora.maintainance.property.infrastructure.config;

import com.fixora.maintainance.property.domain.repository.*;
import com.fixora.maintainance.property.domain.service.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PropertyDomainServiceConfiguration {

    @Bean
    public CompanyService companyService(ICompanyRepository companyRepository) {
        return new DefaultCompanyService(companyRepository);
    }

    @Bean
    public ApartmentService apartmentService(IApartmentRepository apartmentRepository) {
        return new DefaultApartmentService(apartmentRepository);
    }

    @Bean
    public BuildingService buildingService(IBuildingRepository buildingRepository) {
        return new DefaultBuildingService(buildingRepository);
    }
}

