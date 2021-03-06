package jp.co.tv.excelmetaforce;

import jp.co.tv.excelmetaforce.excel.FieldData;
import jp.co.tv.excelmetaforce.excel.ObjectData;

public final class ReadCustomObject {

    private ReadCustomObject() {}

    /**
     * Read Custom Object Definition from salesforce, and write to excel workbook. 
     * + object definition 
     * + field definition
     * + picklist definition
     * 
     * @param args arguments
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static void main(String... args) {
        // set read target metadata
        Class[] target = new Class[]{
            ObjectData.class,
            FieldData.class
        };

        String excelFileName = RunnerHelper.getFileName(args);
        
        // execute target operation
        for (Class cls : target) {
            new Reader(cls, excelFileName).read();
        }
    }
    
}
