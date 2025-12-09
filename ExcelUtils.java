package utils;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelUtils {

    // Helper to get file path from Config
    private static String getFilePath() {
        return ConfigReader.getInstance().getProperty("dataFilePath");
    }

    /**
     * EXISTING METHOD: Reads ALL rows from Excel (For iteration)
     */
    public static Object[][] getExcelDataAsMapForIteration(String sheetName) {
        Object[][] data = null;
        String filePath = getFilePath();

        try (FileInputStream fis = new FileInputStream(filePath);
             XSSFWorkbook wb = new XSSFWorkbook(fis)) {

            XSSFSheet sheet = wb.getSheet(sheetName);
            int rowCount = sheet.getLastRowNum();
            int colCount = sheet.getRow(0).getLastCellNum();

            data = new Object[rowCount][1];
            DataFormatter formatter = new DataFormatter();

            for (int i = 1; i <= rowCount; i++) {
                Map<String, String> rowDataMap = new HashMap<>();
                for (int j = 0; j < colCount; j++) {
                    String key = formatter.formatCellValue(sheet.getRow(0).getCell(j));
                    String value = formatter.formatCellValue(sheet.getRow(i).getCell(j));
                    rowDataMap.put(key, value);
                }
                data[i - 1][0] = rowDataMap;
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to read Excel file: " + filePath);
        }
        return data;
    }

    /**
     * NEW METHOD: Gets data ONLY for a specific Test Case ID (e.g., "tc_001")
     * @param sheetName Name of the Excel sheet
     * @param testCaseId The specific ID to filter by (Must match 'Test Case' column in Excel)
     */
    public static Object[][] getExcelDataAsMap(String sheetName, String testCaseId) {
        String filePath = getFilePath();
        List<Map<String, String>> filteredList = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(filePath);
             XSSFWorkbook wb = new XSSFWorkbook(fis)) {

            XSSFSheet sheet = wb.getSheet(sheetName);
            int rowCount = sheet.getLastRowNum();
            int colCount = sheet.getRow(0).getLastCellNum();

            DataFormatter formatter = new DataFormatter();

            // 1. Loop through all rows
            for (int i = 1; i <= rowCount; i++) {

                // 2. Build the Map for the current row
                Map<String, String> rowDataMap = new HashMap<>();
                boolean isMatch = false;

                for (int j = 0; j < colCount; j++) {
                    String key = formatter.formatCellValue(sheet.getRow(0).getCell(j));
                    String value = formatter.formatCellValue(sheet.getRow(i).getCell(j));
                    rowDataMap.put(key, value);

                    // 3. Check if this row belongs to the requested Test Case ID
                    // We assume the column name in Excel is exactly "Test Case"
                    if (key.equalsIgnoreCase("Test Case") && value.equalsIgnoreCase(testCaseId)) {
                        isMatch = true;
                    }
                }

                // 4. If the ID matched, add this map to our list
                if (isMatch) {
                    filteredList.add(rowDataMap);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to read Excel file: " + filePath);
        }

        // 5. Convert List back to Object[][] for TestNG
        Object[][] result = new Object[filteredList.size()][1];
        for (int i = 0; i < filteredList.size(); i++) {
            result[i][0] = filteredList.get(i);
        }

        return result;
    }
}