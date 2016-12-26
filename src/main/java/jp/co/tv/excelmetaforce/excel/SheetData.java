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
        this.excel  = new ExcelOperator(book, this.sheet);
    }
    
    public abstract Metadata[] read();
    
    public abstract void write(Metadata... write);
    
    public abstract Metadata[] getTargetMetadata();
    
    protected void updateRow(int tmpRow) {
        updateRow(tmpRow, this);
    }

    protected void updateRow(int tmpRow, Object instance) {
        try {
            java.lang.reflect.Field[] fields = instance.getClass().getDeclaredFields();
            
            for (java.lang.reflect.Field field : fields) {
                if (!field.getType().equals(CellInfo.class)) continue;
                
                field.setAccessible(true);
                CellInfo cellInfo = (CellInfo)field.get(instance);
                
                if (cellInfo.isHeader()) continue;
                
                cellInfo.setRow(tmpRow);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
