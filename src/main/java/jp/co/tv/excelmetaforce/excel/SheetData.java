package jp.co.tv.excelmetaforce.excel;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.sforce.soap.metadata.Metadata;

import jp.co.tv.excelmetaforce.sfdc.ConnectionManager;
import jp.co.tv.excelmetaforce.sfdc.Connector;

public abstract class SheetData {
    protected final Sheet sheet;
    protected final ExcelOperator excel;
    protected Connector conn = new Connector(new ConnectionManager());
    
    /**
     * init excel sheet data
     * 
     * @param book workbook
     * @param sheetName target sheet name
     */
    public SheetData(Workbook book, String sheetName) {
        this.sheet = book.getSheet(sheetName);
        this.excel  = new ExcelOperator(this.sheet);
    }
    
    public abstract Metadata[] read();
    
    public abstract void write(Metadata... write);
    
    public abstract Metadata[] getTargetMetadata();
}
