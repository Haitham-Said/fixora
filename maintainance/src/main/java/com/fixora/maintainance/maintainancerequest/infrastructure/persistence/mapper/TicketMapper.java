package com.fixora.maintainance.maintainancerequest.infrastructure.persistence.mapper;

import com.fixora.maintainance.maintainancerequest.domain.model.Ticket;
import com.fixora.maintainance.maintainancerequest.infrastructure.persistence.entity.MaintainanceRequest;
import com.fixora.maintainance.property.domain.model.Apartment;
import com.fixora.maintainance.property.domain.model.Building;
import com.fixora.maintainance.user.domain.model.Customer;
import com.fixora.maintainance.user.domain.model.Maintainer;
import com.fixora.maintainance.user.domain.model.User;

/**
 * Maps persistence entity {@link MaintainanceRequest} to domain {@link Ticket} (no JPA leakage to API layer).
 */
public class TicketMapper {

    public static Ticket toTicket(MaintainanceRequest request) {
        Long pmId = request.getPmCompanyId();
        Long executorId = request.getExecutorCompanyId();
        Long fmId = request.getFacilityManagementCompany() != null
                ? request.getFacilityManagementCompany().getId() : null;
        return Ticket.builder().
                apartment(getApartment(request)).
                building(getBuilding(request)).
                maintainer(getMaintainer(request))
                .customer(getCustomer(request))
                .pmCompanyId(pmId)
                .executorCompanyId(executorId)
                .facilityManagementCompanyId(fmId)
                .companyId(pmId)
                .status(request.getStatus())
                .customerRate(request.getCustomerRate())
                .preferredTime(request.getPreferredTime())
                .preferredVisitDate(request.getPreferredVisitDate())
                .id(request.getId())
                .createdAt(request.getUpdatedAt())
                .description(request.getDescription())
                .pictureUrl(request.getPictureUrl())
                .estimatedAmount(request.getEstimatedAmount())
                .estimationNote(request.getEstimationNote())
                .approved(request.isApproved())
                .paid(request.isPaid())
                .paymentRef(request.getPaymentRef())
                .approvalActor(request.getApprovalActor())
                .ticketApprovalStatus(request.getTicketApprovalStatus())
                .ticketPaymentStatus(request.getTicketPaymentStatus())
                .payerType(request.getPayerType())
                .approvedBy(request.getApprovedBy())
                .approvedAt(request.getApprovedAt())
                .build();

    }

    private static Customer getCustomer(MaintainanceRequest request) {
        Customer customer = new Customer();
        User user = new User();
        user.setName(request.getCustomer().getUser().getName());
        user.setId(request.getCustomer().getUser().getId());
        user.setPhone(request.getCustomer().getUser().getPhone());
        user.setEmail(request.getCustomer().getUser().getEmail());
        customer.setUser(user);
        customer.setMoveInDate(request.getCustomer().getMoveInDate());
        return customer;
    }

    private static Maintainer getMaintainer(MaintainanceRequest maintainanceRequest) {
        if (maintainanceRequest.getMaintainer() == null) {
            return null;
        }
        User user = new User();
        user.setId(maintainanceRequest.getMaintainer().getUserId());
        user.setName(maintainanceRequest.getMaintainer().getUser().getName());
        user.setPhone(maintainanceRequest.getMaintainer().getUser().getPhone());
        user.setEmail(maintainanceRequest.getMaintainer().getUser().getEmail());
        return Maintainer.builder()
                .user(user)
                .rate(maintainanceRequest.getMaintainer().getRate())
                .availability(maintainanceRequest.getMaintainer().getAvailability())
                .build();

    }

    private static Building getBuilding(MaintainanceRequest maintainanceRequest) {
        return Building.builder()
                .id(maintainanceRequest.getBuilding().getId())
                .name(maintainanceRequest.getBuilding().getName())
                .address(maintainanceRequest.getBuilding().getAddress())
                .build();
        
    }

    private static Apartment getApartment(MaintainanceRequest maintainanceRequest) {
        Apartment apartment = new Apartment();
        apartment.setApartmentNumber(maintainanceRequest.getApartment().getApartmentNumber());
        apartment.setFloorNumber(maintainanceRequest.getApartment().getFloorNumber());
        apartment.setId(maintainanceRequest.getApartment().getId());
        return apartment;
    }
}
