package jp.co.tv.excelmetaforce.excel;

import com.sforce.soap.metadata.Metadata;

public interface SheetData {
    public Metadata[] read();
    public void write(Metadata[] write);
    public String getMetadataType();
    public String[] getTargetMetadata();
}
