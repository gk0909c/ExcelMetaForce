package jp.co.tv.excelmetaforce.excel;

import org.apache.poi.ss.usermodel.Workbook;

import com.sforce.soap.metadata.Metadata;

public abstract class SheetData {
    protected GeneralData generalData;
    
    public SheetData(Workbook book) {
        generalData = new GeneralData(book);
    }
    
    public abstract Metadata[] read();
    
    public abstract void write(Metadata... write);
    
    public abstract String getMetadataType();
    
    public abstract String[] getTargetMetadata();
}
