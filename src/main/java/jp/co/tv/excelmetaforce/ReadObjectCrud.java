package jp.co.tv.excelmetaforce;

import jp.co.tv.excelmetaforce.excel.ObjectCrudData;

public final class ReadObjectCrud {
    private ReadObjectCrud() {}

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
            ObjectCrudData.class,
        };
        String excelFileName = RunnerHelper.getFileName(args);
        
        // execute target operation
        for (Class cls : target) {
            new Reader(cls, excelFileName).read();
        }
    }
}
