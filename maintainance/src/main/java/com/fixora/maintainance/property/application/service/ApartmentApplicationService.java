package com.fixora.maintainance.property.application.service;

import com.fixora.maintainance.property.domain.model.Building;
import com.fixora.maintainance.property.domain.model.requests.ApartmentRequest;
import com.fixora.maintainance.property.domain.service.ApartmentService;
import com.fixora.maintainance.property.domain.service.BuildingService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ApartmentApplicationService {

    private final ApartmentService apartmentService;
    private final BuildingService buildingService;
    private final DataFormatter dataFormatter;

    // Expected column names (case-insensitive matching)
    private static final String COLUMN_APARTMENT_NUMBER = "apartmentNumber";
    private static final String COLUMN_FLOOR_NUMBER = "floorNumber";
    private static final String COLUMN_BUILDING_CODE = "buildingCode";

    public ApartmentApplicationService(ApartmentService apartmentService, BuildingService buildingService) {
        this.apartmentService = apartmentService;
        this.buildingService = buildingService;
        this.dataFormatter = new DataFormatter();
    }

    public void addApartments(MultipartFile file){
        try(InputStream stream = file.getInputStream();
            Workbook workbook = new XSSFWorkbook(stream)){
            Sheet sheet = workbook.getSheetAt(0);
            
            Map<String, Integer> columnIndexMap = readColumnIndexMap(sheet);
            validateRequiredColumns(columnIndexMap);
            
            List<ApartmentRequest> requests = processDataRows(sheet, columnIndexMap);
            apartmentService.addApartments(requests);
        } catch (IOException e) {
            throw new RuntimeException("Error processing Excel file", e);
        }
    }
    
    private Map<String, Integer> readColumnIndexMap(Sheet sheet) {
        Row headerRow = sheet.getRow(0);
        if (headerRow == null) {
            throw new RuntimeException("Excel file must have a header row");
        }
        
        Map<String, Integer> columnIndexMap = new HashMap<>();
        for (int i = 0; i <= headerRow.getLastCellNum(); i++) {
            Cell cell = headerRow.getCell(i);
            if (cell != null) {
                String columnName = getCellValueAsString(headerRow, i);
                if (columnName != null && !columnName.isEmpty()) {
                    columnIndexMap.put(columnName.toLowerCase(), i);
                }
            }
        }
        return columnIndexMap;
    }
    
    private void validateRequiredColumns(Map<String, Integer> columnIndexMap) {
        if (!columnIndexMap.containsKey(COLUMN_APARTMENT_NUMBER.toLowerCase()) ||
            !columnIndexMap.containsKey(COLUMN_FLOOR_NUMBER.toLowerCase()) ||
            !columnIndexMap.containsKey(COLUMN_BUILDING_CODE.toLowerCase())) {
            throw new RuntimeException("Excel file must contain columns: " + 
                COLUMN_APARTMENT_NUMBER + ", " + COLUMN_FLOOR_NUMBER + ", " + COLUMN_BUILDING_CODE);
        }
    }
    
    private List<ApartmentRequest> processDataRows(Sheet sheet, Map<String, Integer> columnIndexMap) {
        int apartmentNumberIndex = columnIndexMap.get(COLUMN_APARTMENT_NUMBER.toLowerCase());
        int floorNumberIndex = columnIndexMap.get(COLUMN_FLOOR_NUMBER.toLowerCase());
        int buildingCodeIndex = columnIndexMap.get(COLUMN_BUILDING_CODE.toLowerCase());
        
        List<ApartmentRequest> requests = new ArrayList<>();
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;
            
            ApartmentRequest apartmentRequest = parseRowToApartmentRequest(
                row, apartmentNumberIndex, floorNumberIndex, buildingCodeIndex);
            if (apartmentRequest != null) {
                requests.add(apartmentRequest);
            }
        }
        return requests;
    }
    
    private ApartmentRequest parseRowToApartmentRequest(Row row, int apartmentNumberIndex, 
                                                         int floorNumberIndex, int buildingCodeIndex) {
        String apartmentNumber = getCellValueAsString(row, apartmentNumberIndex);
        String floorNumberStr = getCellValueAsString(row, floorNumberIndex);
        String buildingCode = getCellValueAsString(row, buildingCodeIndex);
        
        if (!isValidRowData(apartmentNumber, floorNumberStr, buildingCode)) {
            return null;
        }
        
        Integer floorNumber = parseFloorNumber(floorNumberStr);
        if (floorNumber == null) {
            return null;
        }
        
        Building building = buildingService.getBuildingByBuildingCode(buildingCode);
        if (building == null) {
            return null;
        }
        
        ApartmentRequest apartmentRequest = new ApartmentRequest();
        apartmentRequest.setBuildingId(building.getId());
        apartmentRequest.setFloorNumber(floorNumber);
        apartmentRequest.setApartmentNumber(apartmentNumber);
        return apartmentRequest;
    }
    
    private boolean isValidRowData(String apartmentNumber, String floorNumberStr, String buildingCode) {
        return apartmentNumber != null && !apartmentNumber.isEmpty() &&
               floorNumberStr != null && !floorNumberStr.isEmpty() &&
               buildingCode != null && !buildingCode.isEmpty();
    }
    
    private Integer parseFloorNumber(String floorNumberStr) {
        try {
            return Integer.parseInt(floorNumberStr);
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    private String getCellValueAsString(Row row, int cellIndex) {
        if (cellIndex < 0 || row == null || row.getCell(cellIndex) == null) {
            return null;
        }
        Cell cell = row.getCell(cellIndex);
        try {
            // DataFormatter converts any cell type (numeric, string, boolean, formula, etc.) to string
            // as it would appear in Excel, making it generic for all data types
            String value = dataFormatter.formatCellValue(cell);
            return value != null ? value.trim() : null;
        } catch (Exception e) {
            return null;
        }
    }

}

