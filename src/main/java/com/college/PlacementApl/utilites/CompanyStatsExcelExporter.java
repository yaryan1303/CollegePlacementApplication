package com.college.PlacementApl.utilites;

import com.college.PlacementApl.dtos.CompanyStatsDto;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class CompanyStatsExcelExporter {

    public static byte[] exportToExcel(List<CompanyStatsDto> stats) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Company Stats");

        // Header Row
        Row headerRow = sheet.createRow(0);
        String[] headers = {"Company Name", "Total Visits", "Total Applications", "Total Placements"};

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        // Data Rows
        int rowNum = 1;
        for (CompanyStatsDto stat : stats) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(stat.getCompanyName());
            row.createCell(1).setCellValue(stat.getTotalVisits());
            row.createCell(2).setCellValue(stat.getTotalApplications());
            row.createCell(3).setCellValue(stat.getTotalPlacements());
        }

        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();
        return out.toByteArray();
    }
}

