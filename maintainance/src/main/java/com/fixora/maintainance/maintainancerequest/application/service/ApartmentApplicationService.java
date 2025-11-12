package com.fixora.maintainance.maintainancerequest.application.service;

import com.fixora.maintainance.maintainancerequest.domain.model.requests.ApartmentRequest;
import com.fixora.maintainance.maintainancerequest.domain.service.ApartmentService;
import com.fixora.maintainance.maintainancerequest.inbound.model.ApartmentRequestDTO;
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

    public ApartmentApplicationService(ApartmentService apartmentService) {
        this.apartmentService = apartmentService;
    }

    public void addApartments(MultipartFile file){
        List<ApartmentRequest> requests=new ArrayList<>();
        try(InputStream stream=file.getInputStream()){
            Workbook workbook=new XSSFWorkbook(stream);
            Sheet sheet=workbook.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row=sheet.getRow(i);
                Long buildingId=(long)row.getCell(0).getNumericCellValue();
                String apartmentNumber=row.getCell(1).getStringCellValue();
                Integer floorNumber=(int)row.getCell(2).getNumericCellValue();
                ApartmentRequest request=new ApartmentRequest();
                request.setBuildingId(buildingId);
                request.setFloorNumber(floorNumber);
                request.setApartmentNumber(apartmentNumber);
                requests.add(request);

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        apartmentService.addApartments(requests);
    }

}
