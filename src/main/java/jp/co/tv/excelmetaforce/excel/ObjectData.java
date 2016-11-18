package jp.co.tv.excelmetaforce.excel;

import org.apache.poi.ss.usermodel.Workbook;

import com.sforce.soap.metadata.CustomField;
import com.sforce.soap.metadata.CustomObject;
import com.sforce.soap.metadata.Metadata;

import jp.co.tv.excelmetaforce.converter.ExcelToMetadata;

public class ObjectData extends SheetData {
    private static final String SHEET_NAME = "オブジェクト定義";
    
    private final ExcelToMetadata converter;

    private final CellInfo description = new CellInfo(7, 1, 0, CellInfo.TYPE_STRING);
    private final CellInfo report = new CellInfo(16, 10, 0, CellInfo.TYPE_BOOLEAN);
    private final CellInfo activity = new CellInfo(17, 10, 0, CellInfo.TYPE_BOOLEAN);
    private final CellInfo chatter = new CellInfo(18, 10, 0, CellInfo.TYPE_BOOLEAN);
    private final CellInfo history = new CellInfo(19, 10, 0, CellInfo.TYPE_BOOLEAN);
    private final CellInfo share = new CellInfo(20, 10, 0, CellInfo.TYPE_STRING);
    private final CellInfo deploy = new CellInfo(21, 10, 0, CellInfo.TYPE_STRING);
    private final CellInfo search = new CellInfo(22, 10, 0, CellInfo.TYPE_BOOLEAN);

    private final CellInfo label = new CellInfo(27, 10, 0, CellInfo.TYPE_STRING);
    private final CellInfo type = new CellInfo(28, 10, 0, CellInfo.TYPE_STRING);
    private final CellInfo format = new CellInfo(29, 10, 0, CellInfo.TYPE_STRING);
    private final CellInfo startNum = new CellInfo(30, 10, 0, CellInfo.TYPE_NUMBER);

    public ObjectData(Workbook book) {
        super(book, SHEET_NAME);
        converter = new ExcelToMetadata();
    }

    @Override
    public Metadata[] read() {
        CustomObject object = new CustomObject();

        object.setFullName(generalData.getObjApi());
        object.setLabel(generalData.getObjLabel());

        object.setDescription(excel.getStringValue(description));
        object.setEnableReports(excel.getBooleanValue(report));
        object.setEnableActivities(excel.getBooleanValue(activity));
        object.setAllowInChatterGroups(excel.getBooleanValue(chatter));
        object.setEnableHistory(excel.getBooleanValue(history));
        object.setSharingModel(converter.convertSharingModel(excel.getStringValue(share)));
        object.setDeploymentStatus(converter.convertDeployment(excel.getStringValue(deploy)));
        object.setEnableSearch(excel.getBooleanValue(search));

        CustomField nameField = new CustomField();
        nameField.setLabel(excel.getStringValue(label));
        nameField.setType(converter.convertType(excel.getStringValue(type)));
        nameField.setDisplayFormat(excel.getStringValue(format));
        nameField.setStartingNumber(excel.getNumericValue(startNum).intValue());

        object.setNameField(nameField);

        return new Metadata[]{object};
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
