package jp.co.tv.excelmetaforce.runner;

import java.io.File;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.sforce.soap.metadata.Metadata;

import jp.co.tv.excelmetaforce.excel.SheetData;
import jp.co.tv.excelmetaforce.sfdc.Connector;

public class Reader {
    private SheetData data;
    
    /**
     * init by SheetData instance.
     * 
     * @param data SheetData instance
     */
    public Reader(Class<? extends SheetData> data) {
        try {
            Workbook book = WorkbookFactory.create(new File("filename.xlsx"), "", true);
            this.data = data.getDeclaredConstructor(SheetData.class).newInstance(book);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Write object definition to excel file
     */
    public void read() {
        Connector conn = Connector.getInstance();
        Metadata[] metadata = conn.readMetadata(data.getMetadataType(), data.getTargetMetadata());
        data.write(metadata);
    }
}
