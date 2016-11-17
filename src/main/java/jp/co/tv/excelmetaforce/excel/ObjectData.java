package jp.co.tv.excelmetaforce.excel;

import org.apache.poi.ss.usermodel.Workbook;

import com.sforce.soap.metadata.Metadata;

public class ObjectData extends SheetData {
    private static final String SHEET_NAME = "オブジェクト定義";
    
    private GeneralData generalData;
    
    private CellData report = new CellData(16, 10, 0);
    private CellData activity = new CellData(17, 10, 0);
    private CellData chatter = new CellData(18, 10, 0);
    private CellData history = new CellData(19, 10, 0);
    private CellData share = new CellData(20, 10, 0);
    private CellData release = new CellData(21, 10, 0);
    private CellData search = new CellData(22, 10, 0);
    private CellData label = new CellData(27, 10, 0);
    private CellData type = new CellData(28, 10, 0);
    private CellData format = new CellData(29, 10, 0);
    private CellData startNum = new CellData(30, 10, 0);
    private CellData description = new CellData(7, 1, 0);

    public ObjectData(Workbook book) {
        super(book);
    }

    @Override
    public Metadata[] read() {
        return null;
    }

    @Override
    public void write(Metadata... write) {

    }

    @Override
    public String getMetadataType() {
        return null;
    }

    @Override
    public String[] getTargetMetadata() {
        return null;
    }

}
