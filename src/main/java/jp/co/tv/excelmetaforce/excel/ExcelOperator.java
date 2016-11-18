package jp.co.tv.excelmetaforce.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

public class ExcelOperator {
    private final Sheet sheet;
    
    public ExcelOperator(Sheet sheet) {
        this.sheet = sheet;
    }

    /**
     * return string cell value
     * @param cellInfo cellInfo
     * @return string value
     */
    public String getStringValue(CellInfo cellInfo) {
        return getCell(cellInfo).getStringCellValue(); 
    }

    /**
     * return numeric cell value
     * @param cellInfo cellInfo
     * @return string value
     */
    public Number getNumericValue(CellInfo cellInfo) {
        return getCell(cellInfo).getNumericCellValue(); 
    }

    /**
     * return boolean cell value
     * @param cellInfo cellInfo
     * @return string value
     */
    public boolean getBooleanValue(CellInfo cellInfo) {
        String val = getCell(cellInfo).getStringCellValue();
        return "○".equals(val) ? true : false; 
    }
    
    private Cell getCell(CellInfo cellInfo) {
        Row row = sheet.getRow(cellInfo.getRow());
        return row.getCell(cellInfo.getCol());
    }

}
