package jp.co.tv.excelmetaforce;

import java.io.File;
import java.util.List;
import java.util.function.Function;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sforce.soap.metadata.Metadata;
import com.sforce.soap.metadata.SaveResult;

import jp.co.tv.excelmetaforce.excel.SheetData;
import jp.co.tv.excelmetaforce.sfdc.ConnectionManager;
import jp.co.tv.excelmetaforce.sfdc.Connector;

public class Creater {
    private static final Logger LOGGER = LoggerFactory.getLogger(Creater.class);
    private SheetData data;

    /**
     * init by SheetData instance.
     * 
     * @param dataCls SheetData instance
     */
    public Creater(Class<? extends SheetData> dataCls, String excelFileName) {
        try {
            Workbook book = WorkbookFactory.create(new File(excelFileName), "", true);
            this.data = dataCls.getDeclaredConstructor(Workbook.class).newInstance(book);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } 
    }
    
    /**
     * Write object definition to excel file
     */
    public void create() {
        Function<Metadata[], List<SaveResult>> func = metadata -> {
            Connector conn = new Connector(new ConnectionManager());
            return conn.createMetadata(metadata);
        };
        save(func);
    }

    /**
     * Write object definition to excel file
     */
    public void update() { 
        Function<Metadata[], List<SaveResult>> func = metadata -> {
            Connector conn = new Connector(new ConnectionManager());
            return conn.updateMetadata(metadata);
        };
        save(func);
    }
    
    private void save(Function<Metadata[], List<SaveResult>> func) {
        Metadata[] metadata = data.read();
        List<SaveResult> results = func.apply(metadata);
        
        for (SaveResult result : results) {
            for (com.sforce.soap.metadata.Error err : result.getErrors()) {
                LOGGER.warn(String.format("save error: %s", err.getMessage()));
            }
        }
        
    }
}
