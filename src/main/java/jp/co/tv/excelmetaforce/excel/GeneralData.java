package jp.co.tv.excelmetaforce.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public class GeneralData {
    private static final String SHEET_NAME = "表紙";
    
    private Sheet sheet;
    private CellData objApi = new CellData(30, 12, 0);
    private CellData objLabel = new CellData(32, 12, 0);

    public GeneralData(Workbook book) {
        this.sheet = book.getSheet(SHEET_NAME);
    }

    public String getObjApi() {
        Row row = sheet.getRow(objApi.getRow());
        Cell cell = row.getCell(objApi.getCol());

        return cell.getStringCellValue(); 
    }

    public String getObjLabel() {
        Row row = sheet.getRow(objLabel.getRow());
        Cell cell = row.getCell(objLabel.getCol());

        return cell.getStringCellValue(); 
    }
}
