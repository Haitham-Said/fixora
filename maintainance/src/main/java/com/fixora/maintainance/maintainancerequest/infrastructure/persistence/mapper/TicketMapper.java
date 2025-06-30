package com.fixora.maintainance.maintainancerequest.infrastructure.persistence.mapper;

import com.fixora.maintainance.maintainancerequest.domain.model.*;
import com.fixora.maintainance.maintainancerequest.infrastructure.persistence.entity.MaintainanceRequest;

public class TicketMapper {

    public static Ticket toTicket(MaintainanceRequest request){
        return Ticket.builder().
                apartment(getApartment(request)).
                building(getBuilding(request)).
                maintainer(getMaintainer(request))
                .customer(getCustomer(request))
                .status(request.getStatus())
                .customerRate(request.getCustomerRate())
                .preferredTime(request.getPreferredTime())
                .id(request.getId())
                .build();

    }

    private static Customer getCustomer(MaintainanceRequest request) {
        Customer customer=new Customer();
        User user=new User();
        user.setName(request.getCustomer().getUser().getName());
        user.setId(request.getCustomer().getUser().getId());
        user.setPhone(request.getCustomer().getUser().getPhone());
        customer.setUser(user);
        customer.setMoveInDate(request.getCustomer().getMoveInDate());
        return customer;
    }

    private static Maintainer getMaintainer(MaintainanceRequest maintainanceRequest) {
        User user=new User();
        user.setId(maintainanceRequest.getMaintainer().getUserId());
        user.setName(maintainanceRequest.getMaintainer().getUser().getName());
        user.setPhone(maintainanceRequest.getMaintainer().getUser().getPhone());
        user.setEmail(maintainanceRequest.getMaintainer().getUser().getEmail());
        Maintainer maintainer=new Maintainer();
        maintainer.setUser(user);
        maintainer.setRate(maintainanceRequest.getMaintainer().getRate());
        maintainer.setAvailability(maintainanceRequest.getMaintainer().getAvailability());
        maintainer.setLongitude(maintainanceRequest.getMaintainer().getLongitude());
        maintainer.setLatitude(maintainanceRequest.getMaintainer().getLatitude());
        return maintainer;
    }

    private static Building getBuilding(MaintainanceRequest maintainanceRequest) {
        Building building=new Building();
        building.setId(maintainanceRequest.getBuilding().getId());
        building.setName(maintainanceRequest.getBuilding().getName());
        building.setAddress(maintainanceRequest.getBuilding().getAddress());
        return building;
    }

    private static Apartment getApartment(MaintainanceRequest maintainanceRequest) {
        Apartment apartment=new Apartment();
        apartment.setApartmentNumber(maintainanceRequest.getApartment().getApartmentNumber());
        apartment.setFloorNumber(maintainanceRequest.getApartment().getFloorNumber());
        apartment.setId(maintainanceRequest.getApartment().getId());
        return apartment;
    }
}
