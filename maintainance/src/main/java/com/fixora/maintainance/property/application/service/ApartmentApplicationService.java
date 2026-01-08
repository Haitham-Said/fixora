package com.fixora.maintainance.property.application.service;

import com.fixora.maintainance.property.domain.model.Building;
import com.fixora.maintainance.property.domain.model.requests.ApartmentRequest;
import com.fixora.maintainance.property.domain.service.ApartmentService;
import com.fixora.maintainance.property.domain.service.BuildingService;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class ApartmentApplicationService {

    private final ApartmentService apartmentService;
    private final BuildingService buildingService;

    public ApartmentApplicationService(ApartmentService apartmentService, BuildingService buildingService) {
        this.apartmentService = apartmentService;
        this.buildingService = buildingService;
    }

    public void addApartments(MultipartFile file){
        List<ApartmentRequest> requests=new ArrayList<>();
        try(InputStream stream=file.getInputStream();
            Workbook workbook=new XSSFWorkbook(stream)){
            Sheet sheet=workbook.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row=sheet.getRow(i);
                String apartmentNumber=row.getCell(0).getStringCellValue();
                Integer floorNumber=(int)row.getCell(1).getNumericCellValue();
                String buildingCode=row.getCell(2).getStringCellValue();
                Building building=buildingService.getBuildingByBuildingCode(buildingCode);
                if(building != null){
                ApartmentRequest apartmentRequest=new ApartmentRequest();
                apartmentRequest.setBuildingId(building.getId());
                apartmentRequest.setFloorNumber(floorNumber);
                apartmentRequest.setApartmentNumber(apartmentNumber);
                requests.add(apartmentRequest);
                }

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        apartmentService.addApartments(requests);
    }

}

