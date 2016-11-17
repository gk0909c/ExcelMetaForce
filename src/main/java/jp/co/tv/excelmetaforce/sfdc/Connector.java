package jp.co.tv.excelmetaforce.sfdc;

import com.sforce.soap.metadata.Metadata;

public class Connector {
    private Connector() {}
    
    public static Connector getInstance() {
        return null;
    }

    public Metadata[] readMetadata(String type, String[] target) {
        return null;
    }

    public void createMetadata() {
    }
}
