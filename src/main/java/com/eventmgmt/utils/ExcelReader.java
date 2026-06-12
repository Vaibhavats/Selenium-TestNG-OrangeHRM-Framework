package com.eventmgmt.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * ExcelReader.java
 * -----------------
 * Utility class that reads test data from Excel (.xlsx) files.
 * Treats the first row as a header row (column names).
 * Each subsequent row becomes a Map<columnName, cellValue>.
 *
 * Usage:
 *   List<Map<String,String>> data =
 *       ExcelReader.getSheetData("path/to/file.xlsx", "Sheet1");
 *
 *   // Then in @DataProvider:
 *   Object[][] testData = ExcelReader.toTestNGDataProvider(data);
 */
public class ExcelReader {

    private static final Logger log = LogManager.getLogger(ExcelReader.class);

    private ExcelReader() {}

    // ----------------------------------------------------------------
    // Core reader — returns list of row-maps
    // ----------------------------------------------------------------

    /**
     * Reads all rows from a given sheet and returns them as a list of maps.
     * First row = headers, remaining rows = data.
     *
     * @param filePath  path to the .xlsx file
     * @param sheetName name of the worksheet tab
     * @return          list of maps; each map is one test row
     */
    public static List<Map<String, String>> getSheetData(String filePath,
                                                          String sheetName) {
        List<Map<String, String>> dataList = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                throw new RuntimeException("Sheet '" + sheetName +
                        "' not found in file: " + filePath);
            }

            // Read headers from row 0
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                log.warn("Excel file has no header row: {}", filePath);
                return dataList;
            }
            int columnCount = headerRow.getLastCellNum();

            // Read data rows (starting from row 1)
            int rowCount = sheet.getLastRowNum();
            for (int rowIdx = 1; rowIdx <= rowCount; rowIdx++) {
                Row row = sheet.getRow(rowIdx);
                if (row == null) continue;

                Map<String, String> rowData = new LinkedHashMap<>();
                for (int colIdx = 0; colIdx < columnCount; colIdx++) {
                    String header = getCellValue(headerRow.getCell(colIdx));
                    String value  = getCellValue(row.getCell(colIdx));
                    rowData.put(header, value);
                }
                dataList.add(rowData);
            }

            log.info("Loaded {} data rows from sheet '{}' in file '{}'",
                    dataList.size(), sheetName, filePath);

        } catch (IOException e) {
            log.error("Failed to read Excel file '{}': {}", filePath, e.getMessage());
            throw new RuntimeException("Error reading Excel file: " + filePath, e);
        }

        return dataList;
    }

    // ----------------------------------------------------------------
    // Converter for TestNG @DataProvider
    // ----------------------------------------------------------------

    /**
     * Converts the list-of-maps into a 2D Object[][] array suitable
     * for use in a TestNG @DataProvider method.
     * Each Object[] in the array contains a single Map representing one row.
     *
     * @param dataList  output from getSheetData()
     * @return          TestNG-compatible Object[][] test data
     */
    public static Object[][] toTestNGDataProvider(List<Map<String, String>> dataList) {
        Object[][] result = new Object[dataList.size()][1];
        for (int i = 0; i < dataList.size(); i++) {
            result[i][0] = dataList.get(i);
        }
        return result;
    }

    // ----------------------------------------------------------------
    // Private helper: extract cell value as String regardless of type
    // ----------------------------------------------------------------
    private static String getCellValue(Cell cell) {
        if (cell == null) return "";

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                }
                // Avoid scientific notation for numbers like phone numbers
                double numVal = cell.getNumericCellValue();
                if (numVal == Math.floor(numVal)) {
                    return String.valueOf((long) numVal);
                }
                return String.valueOf(numVal);
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
            default:
                return "";
        }
    }
}
