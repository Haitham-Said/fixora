package com.fixora.maintainance.user.application;

import com.fixora.maintainance.user.domain.model.Customer;
import com.fixora.maintainance.user.domain.model.NotificationRequest;
import com.fixora.maintainance.user.domain.model.NotificationType;
import com.fixora.maintainance.user.domain.model.UserCode;
import com.fixora.maintainance.user.domain.model.request.CustomerRequest;
import com.fixora.maintainance.user.domain.repositories.IUserCodeRepository;
import com.fixora.maintainance.user.domain.service.IUserService;
import com.fixora.maintainance.user.domain.service.INotificationService;
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
import java.util.Optional;

@Service
public class CustomerApplicationService {
    private final IUserService userService;
    private final IUserCodeRepository userCodeRepository;
    private final INotificationService notificationService;

    public CustomerApplicationService(IUserService userService,
                                     IUserCodeRepository userCodeRepository,
                                     INotificationService notificationService) {
        this.userService = userService;
        this.userCodeRepository = userCodeRepository;
        this.notificationService = notificationService;
    }

    public void addTenants(MultipartFile file){
        List<CustomerRequest> requests = new ArrayList<>();
        try(InputStream stream = file.getInputStream();
            Workbook workbook = new XSSFWorkbook(stream)){
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
                    customerRequest.setPhone(phone != null ? phone : ""); // Phone can be optional
                    customerRequest.setBuildingCode(buildingCode);
                    customerRequest.setApartmentNumber(apartmentNumber);
                    
                    // Parse move_in_date
                    if (moveInDateStr != null && !moveInDateStr.isEmpty()) {
                        try {
                            customerRequest.setMoveInDate(LocalDate.parse(moveInDateStr));
                        } catch (Exception e) {
                            // If date parsing fails, set to null or current date
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

        // Process each tenant request
        for (CustomerRequest request : requests) {
            try {
                Customer customer = userService.addCustomer(request);
                
                // Get the generated code using the created user ID
                Long userId = customer.getUser().getId();
                Optional<UserCode> userCodeOpt = userCodeRepository.findByUserId(userId);
                if (userCodeOpt.isPresent()) {
                    UserCode userCode = userCodeOpt.get();
                    sendTenantUploadNotification(request.getEmail(), request.getName(), userCode.getCode());
                }
            } catch (Exception e) {
                // Log error but continue processing other tenants
                System.err.println("Error adding tenant: " + request.getEmail() + " - " + e.getMessage());
            }
        }
    }
    
    private void sendTenantUploadNotification(String email, String name, String code) {
        NotificationRequest notificationRequest = NotificationRequest.builder()
                .recipientEmail(email)
                .recipientName(name)
                .notificationType(NotificationType.TENANT_UPLOAD_CODE)
                .subject("Welcome! Your Account Activation Code")
                .message(String.format(
                    "Dear %s,\n\n" +
                    "Your account has been created successfully. " +
                    "Your activation code is: %s\n\n" +
                    "Please use this code to activate your account.\n\n" +
                    "Best regards,\nMaintenance Team",
                    name, code
                ))
                .activationCode(code)
                .build();
        
        notificationService.sendNotification(notificationRequest);
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

