package jp.co.tv.excelmetaforce.excel;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.sforce.soap.metadata.Metadata;

public abstract class SheetData {
    protected GeneralData generalData;

    protected final Sheet sheet;
    protected final ExcelOperator excel;
    
    /**
     * init excel sheet data
     * 
     * @param book workbook
     * @param sheetName target sheet name
     */
    public SheetData(Workbook book, String sheetName) {
        generalData = new GeneralData(book);
        this.sheet = book.getSheet(sheetName);
        this.excel  = new ExcelOperator(this.sheet);
    }
    
    public abstract Metadata[] read();
    
    public abstract void write(Metadata... write);
    
    public abstract String getMetadataType();
    
    public abstract String[] getTargetMetadata();
}
