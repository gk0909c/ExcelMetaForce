package jp.co.tv.excelmetaforce.runner;

import com.sforce.soap.metadata.Metadata;

import jp.co.tv.excelmetaforce.excel.SheetData;
import jp.co.tv.excelmetaforce.sfdc.Connector;

public class Reader {
    private SheetData data;
    
    public Reader(SheetData data) {
        this.data = data;
    }
    
    public void read() {
        Connector conn = Connector.getInstance();
        Metadata[] metadata = conn.readMetadata(data.getMetadataType(), data.getTargetMetadata());
        data.write(metadata);
    }
}
