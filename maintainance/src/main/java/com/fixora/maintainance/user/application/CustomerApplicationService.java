package com.fixora.maintainance.user.application;

import com.fixora.maintainance.user.domain.model.request.CustomerRequest;
import com.fixora.maintainance.user.domain.service.IUserService;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerApplicationService {
    private final IUserService userService;

    public CustomerApplicationService(IUserService userService) {
        this.userService = userService;
    }

    public void addTenants(MultipartFile file) {
        List<CustomerRequest> requests = new ArrayList<>();
        try (InputStream stream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(stream)) {
            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String name = getCellValueAsString(row, 0);
                String email = getCellValueAsString(row, 1);
                String phone = getCellValueAsString(row, 2);
                String moveInDateStr = getCellValueAsString(row, 3);
                String buildingCode = getCellValueAsString(row, 4);
                String apartmentNumber = getCellValueAsString(row, 5);

                if (name != null && email != null && buildingCode != null && apartmentNumber != null) {
                    CustomerRequest customerRequest = new CustomerRequest();
                    customerRequest.setName(name);
                    customerRequest.setEmail(email);
                    customerRequest.setPhone(phone != null ? phone : "");
                    customerRequest.setBuildingCode(buildingCode);
                    customerRequest.setApartmentNumber(apartmentNumber);

                    if (moveInDateStr != null && !moveInDateStr.isEmpty()) {
                        try {
                            customerRequest.setMoveInDate(LocalDate.parse(moveInDateStr));
                        } catch (Exception e) {
                            customerRequest.setMoveInDate(LocalDate.now());
                        }
                    } else {
                        customerRequest.setMoveInDate(LocalDate.now());
                    }

                    requests.add(customerRequest);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error processing Excel file", e);
        }

        for (CustomerRequest request : requests) {
            try {
                userService.addCustomer(request);
            } catch (Exception e) {
                System.err.println("Error adding tenant: " + request.getEmail() + " - " + e.getMessage());
            }
        }
    }

    private String getCellValueAsString(Row row, int cellIndex) {
        if (row.getCell(cellIndex) == null) {
            return null;
        }
        try {
            return row.getCell(cellIndex).getStringCellValue();
        } catch (Exception e) {
            try {
                return String.valueOf((long) row.getCell(cellIndex).getNumericCellValue());
            } catch (Exception ex) {
                return null;
            }
        }
    }
}
