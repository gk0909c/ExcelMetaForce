package jp.co.tv.excelmetaforce.excel;

import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExcelOperator {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelOperator.class);
    
    private final Workbook book;
    private final Sheet sheet;
    
    public ExcelOperator(Workbook book, Sheet sheet) {
        this.book = book;
        this.sheet = sheet;
    }

    /**
     * return string cell value
     * @param cellInfo cellInfo
     * @return string value
     */
    public String getStringValue(CellInfo cellInfo) {
        String val = getCell(cellInfo).getStringCellValue();
        return StringUtils.stripToNull(val); 
    }

    /**
     * return numeric cell value, when invalid number format, return null.
     * @param cellInfo cellInfo
     * @return integer value
     */
    public Integer getNumericValue(CellInfo cellInfo) {
        try {
            return Integer.parseInt(getCell(cellInfo).getStringCellValue()); 
        } catch (NumberFormatException e) {
            return null;
        }
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
        return getCell(cellInfo.getRow(), cellInfo.getCol());
    }

    private Cell getCell(int rowNum, int colNum) {
        Row row = sheet.getRow(rowNum);
        if (row == null) {
            row = sheet.createRow(rowNum);
        }
        
        Cell cell = row.getCell(colNum);
        if (cell == null) {
            cell = row.createCell(colNum);
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

            CellStyle cellStyle = createBaseStyle();
            
            for (int i = startCol; i < endCol + 1; i++) {
                LOGGER.trace(String.format("set style, target row:%d, target col: %d", row, i));
                getCell(row, i).setCellStyle(cellStyle);
            }
        }
    }
    
    private CellStyle createBaseStyle() {
        Font font = book.createFont();
        font.setFontName("Meiryo");

        CellStyle style = book.createCellStyle();
        style.setFont(font);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        
        return style;
    }
}
