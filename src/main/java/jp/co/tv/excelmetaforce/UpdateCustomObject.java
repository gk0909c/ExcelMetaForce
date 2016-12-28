package jp.co.tv.excelmetaforce;

import jp.co.tv.excelmetaforce.excel.ObjectData;

public final class UpdateCustomObject {
    private UpdateCustomObject() {}

    /**
     * Read Custom Object Definition from excel workbook, and save to salesforce. 
     * + object definition 
     * + field definition
     * + picklist definition
     * 
     * @param args arguments
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static void main(String... args) {
        // set create target metadata
        Class[] target = new Class[]{
            ObjectData.class
        };
        String excelFileName = RunnerHelper.getFileName(args);
        
        // execute target operation
        for (Class cls : target) {
            new Creater(cls, excelFileName).update();
        }
    }
}
