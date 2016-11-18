package jp.co.tv.excelmetaforce.excel;

import org.apache.poi.ss.usermodel.Workbook;

public class GeneralData {
    private static final String SHEET_NAME = "表紙";
    
    private final ExcelOperator excel;
    private final CellInfo objApi = new CellInfo(30, 12, 0, CellInfo.TYPE_STRING);
    private final CellInfo objLabel = new CellInfo(32, 12, 0, CellInfo.TYPE_STRING);

    public GeneralData(Workbook book) {
        this.excel = new ExcelOperator(book.getSheet(SHEET_NAME));
    }

    /**
     * return Object Api Name
     * 
     * @return Object Api Name
     */
    public String getObjApi() {
        return excel.getStringValue(objApi); 
    }

    /**
     * return Object Api Label
     * 
     * @return Object Api Label
     */
    public String getObjLabel() {
        return excel.getStringValue(objLabel); 
    }
}
