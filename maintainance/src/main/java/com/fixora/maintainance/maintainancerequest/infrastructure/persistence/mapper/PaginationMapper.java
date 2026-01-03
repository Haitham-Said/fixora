package com.fixora.maintainance.maintainancerequest.infrastructure.persistence.mapper;

import com.fixora.maintainance.maintainancerequest.domain.model.PaginationRequest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PaginationMapper {

    public static PaginationRequest toDomain(Pageable pageable) {
        if (pageable == null) {
            return PaginationRequest.builder().build();
        }

        String sortBy = "id";
        String sortDirection = "DESC";

        if (pageable.getSort().isSorted()) {
            Sort.Order order = pageable.getSort().iterator().next();
            sortBy = order.getProperty();
            sortDirection = order.getDirection().name();
        }

        return PaginationRequest.builder()
                .pageNumber(pageable.getPageNumber())
                .pageSize(pageable.getPageSize())
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .build();
    }

    public static Pageable toSpring(PaginationRequest pagination) {
        if (pagination == null) {
            return PageRequest.of(0, 10);
        }

        Sort.Direction direction = "ASC".equalsIgnoreCase(pagination.getSortDirection()) 
            ? Sort.Direction.ASC 
            : Sort.Direction.DESC;

        return PageRequest.of(
            pagination.getPageNumber(),
            pagination.getPageSize(),
            Sort.by(direction, pagination.getSortBy())
        );
    }
}

