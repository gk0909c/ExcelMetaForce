package jp.co.tv.excelmetaforce.excel;

import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

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
        return Integer.parseInt(getCell(cellInfo).getStringCellValue()); 
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
        if (row == null) {
            row = sheet.createRow(cellInfo.getRow());
        }
        
        Cell cell = row.getCell(cellInfo.getCol());
        if (cell == null) {
            cell = row.createCell(cellInfo.getCol());
        }

        return cell;
    }
    
    /**
     * set string value to cell
     * 
     * @param cellInfo cellInfo
     * @param value string
     */
    public void setValue(CellInfo cellInfo, String value) {
        Function<String, String> func = str -> str;
        doSetValue(cellInfo, value, func);
    }

    /**
     * return numeric cell value
     * @param cellInfo cellInfo
     * @param value integer
     */
    public void setValue(CellInfo cellInfo, Integer value) {
        Function<Integer, String> func = integer -> integer.toString();
        doSetValue(cellInfo, value, func);
    }

    /**
     * set boolean value to cell
     * 
     * @param cellInfo cellInfo
     * @param value boolean
     */
    public void setValue(CellInfo cellInfo, boolean value) {
        Function<Boolean, String> func = bool -> bool ? "○" : "";
        doSetValue(cellInfo, value, func);
    }

    /**
     * return numeric cell value, if 0, set empty
     * @param cellInfo cellInfo
     * @param value integer
     */
    public void setValueToEmpty(CellInfo cellInfo, Integer value) {
        Function<Integer, String> func = integer -> integer == 0 ? "" : integer.toString();
        doSetValue(cellInfo, value, func);
    }
    
    private <T extends Object> void doSetValue(CellInfo cellInfo, T value, Function<T, String> valueSetter) {
        Cell cell = getCell(cellInfo);
        cell.setCellValue(valueSetter.apply(value));
        setStyle(cellInfo);
    }

    /**
     * return true if target cell is null or empty.
     * 
     * @param rowNum row number 
     * @param colNum column number
     * @return whether target cell is empty
     */
    public boolean isEmpty(int rowNum, int colNum) {
        Row row = sheet.getRow(rowNum);
        if (row == null) return true;
        
        Cell cell = row.getCell(colNum);
        if (cell == null) return true;
        
        return StringUtils.isEmpty(cell.getStringCellValue());
    }
    
    private void setStyle(CellInfo cellInfo) {
        if (cellInfo.getColSpan() != 0) {
            int row = cellInfo.getRow();
            int startCol = cellInfo.getCol();
            int endCol = startCol + cellInfo.getColSpan() - 1;
            sheet.addMergedRegion(new CellRangeAddress(row, row, startCol, endCol));
        }
    }
}
