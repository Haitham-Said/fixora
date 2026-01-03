package com.fixora.maintainance.maintainancerequest.domain.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaginationRequest {
    private Integer pageNumber;
    private Integer pageSize;
    private String sortBy;
    private String sortDirection; // ASC, DESC

    public PaginationRequest() {
        this.pageNumber = 0;
        this.pageSize = 10;
        this.sortBy = "id";
        this.sortDirection = "DESC";
    }

    public PaginationRequest(Integer pageNumber, Integer pageSize, String sortBy, String sortDirection) {
        this.pageNumber = pageNumber != null ? pageNumber : 0;
        this.pageSize = pageSize != null ? pageSize : 10;
        this.sortBy = sortBy != null ? sortBy : "id";
        this.sortDirection = sortDirection != null ? sortDirection : "DESC";
    }

    public int getOffset() {
        return pageNumber * pageSize;
    }
}

