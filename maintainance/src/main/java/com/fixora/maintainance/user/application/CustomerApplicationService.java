package com.fixora.maintainance.user.application;

import com.fixora.maintainance.user.domain.model.request.CustomerRequest;
import com.fixora.maintainance.user.domain.service.IUserService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class CustomerApplicationService {
    private final IUserService userService;

    public CustomerApplicationService(IUserService userService) {
        this.userService = userService;
    }

    public void addCustomers(MultipartFile file, Long companyId) {
        List<CustomerRequest> requests = new ArrayList<>();
        try (InputStream stream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(stream)) {
            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                CustomerRequest customerRequest = new CustomerRequest();
                customerRequest.setCompanyId(companyId);
                
                // Read name (column 0)
                Cell nameCell = row.getCell(0);
                if (nameCell != null) {
                    customerRequest.setName(nameCell.getStringCellValue());
                }
                
                // Read email (column 1)
                Cell emailCell = row.getCell(1);
                if (emailCell != null) {
                    customerRequest.setEmail(emailCell.getStringCellValue());
                }
                
                // Read phone (column 2)
                Cell phoneCell = row.getCell(2);
                if (phoneCell != null) {
                    if (phoneCell.getCellType() == CellType.NUMERIC) {
                        customerRequest.setPhone(String.valueOf((long) phoneCell.getNumericCellValue()));
                    } else {
                        customerRequest.setPhone(phoneCell.getStringCellValue());
                    }
                }
                
                // Read building_code (column 3)
                Cell buildingCodeCell = row.getCell(3);
                if (buildingCodeCell != null) {
                    customerRequest.setBuildingCode(buildingCodeCell.getStringCellValue());
                }
                
                // Read apartment_number (column 4)
                Cell apartmentNumberCell = row.getCell(4);
                if (apartmentNumberCell != null) {
                    if (apartmentNumberCell.getCellType() == CellType.NUMERIC) {
                        customerRequest.setApartmentNumber(String.valueOf((long) apartmentNumberCell.getNumericCellValue()));
                    } else {
                        customerRequest.setApartmentNumber(apartmentNumberCell.getStringCellValue());
                    }
                }
                
                // Read move_in_date (column 5)
                Cell moveInDateCell = row.getCell(5);
                if (moveInDateCell != null) {
                    LocalDate moveInDate = null;
                    if (moveInDateCell.getCellType() == CellType.NUMERIC) {
                        Date date = moveInDateCell.getDateCellValue();
                        moveInDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    }
                    customerRequest.setMoveInDate(moveInDate);
                }

                if (customerRequest.getName() != null && customerRequest.getEmail() != null) {
                    requests.add(customerRequest);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error processing Excel file", e);
        }

        // Process each customer request
        for (CustomerRequest request : requests) {
            try {
                userService.addCustomer(request);
            } catch (Exception e) {
                // Log error but continue processing other customers
                System.err.println("Error adding customer: " + request.getEmail() + " - " + e.getMessage());
            }
        }
    }
}

