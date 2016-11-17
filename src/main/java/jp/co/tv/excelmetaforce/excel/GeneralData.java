package jp.co.tv.excelmetaforce.excel;

import org.apache.poi.ss.usermodel.Workbook;

import com.sforce.soap.metadata.Metadata;

public class GeneralData {
    private static final String SHEET_NAME = "表紙";
    
    private Workbook book;
    private CellData objApi = new CellData(30, 12, 0);
    private CellData objLabel = new CellData(32, 12, 0);

    public GeneralData(Workbook book) {
        this.book = book; 
    }

    public String getObjApi() {
       return null; 
    }

    public String getObjLabel() {
       return null; 
    }
}
