package jp.co.tv.excelmetaforce.excel;

import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sforce.soap.metadata.CustomField;
import com.sforce.soap.metadata.CustomObject;
import com.sforce.soap.metadata.Metadata;

import jp.co.tv.excelmetaforce.converter.ExcelToMetadata;
import jp.co.tv.excelmetaforce.converter.MetadataToExcel;

public class ObjectData extends SheetData {
    public static final String SHEET_NAME = "オブジェクト定義";
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ObjectData.class);
    
    private final CellInfo fullName = new CellInfo(0, 27, 0);
    private final CellInfo label = new CellInfo(1, 27, 0);

    private final CellInfo description = new CellInfo(7, 1, 0);
    private final CellInfo report = new CellInfo(16, 11, 0);
    private final CellInfo activity = new CellInfo(17, 11, 0);
    private final CellInfo chatter = new CellInfo(18, 11, 0);
    private final CellInfo history = new CellInfo(19, 11, 0);
    private final CellInfo share = new CellInfo(20, 11, 0);
    private final CellInfo deploy = new CellInfo(21, 11, 0);
    private final CellInfo search = new CellInfo(22, 11, 0);

    private final CellInfo nameLabel = new CellInfo(27, 11, 0);
    private final CellInfo type = new CellInfo(28, 11, 0);
    private final CellInfo format = new CellInfo(29, 11, 0);
    private final CellInfo startNum = new CellInfo(30, 11, 0);

    public ObjectData(Workbook book) {
        super(book, SHEET_NAME);
    }

    @Override
    public Metadata[] read() {
        ExcelToMetadata converter = new ExcelToMetadata();
        CustomObject object = new CustomObject();

        object.setFullName(excel.getStringValue(fullName));
        object.setLabel(excel.getStringValue(label));

        object.setDescription(excel.getStringValue(description));
        object.setEnableReports(excel.getBooleanValue(report));
        object.setEnableActivities(excel.getBooleanValue(activity));
        object.setAllowInChatterGroups(excel.getBooleanValue(chatter));
        object.setEnableHistory(excel.getBooleanValue(history));
        object.setSharingModel(converter.convSharingModel(excel.getStringValue(share)));
        object.setDeploymentStatus(converter.convDeploymentStatus(excel.getStringValue(deploy)));
        object.setEnableSearch(excel.getBooleanValue(search));

        CustomField nameField = new CustomField();
        nameField.setLabel(excel.getStringValue(nameLabel));
        nameField.setType(converter.convFieldType(excel.getStringValue(type)));
        nameField.setDisplayFormat(excel.getStringValue(format));
        setStartingNumber(nameField);

        object.setNameField(nameField);

        return new Metadata[]{object};
    }

    @Override
    public void write(Metadata... metadata) {

        MetadataToExcel converter = new MetadataToExcel();
        CustomObject object = (CustomObject)metadata[0];

        excel.setValue(description, object.getDescription());
        excel.setValue(report, object.getEnableReports());
        excel.setValue(activity, object.getEnableActivities());
        excel.setValue(chatter, object.getAllowInChatterGroups());
        excel.setValue(history, object.getEnableHistory());
        excel.setValue(share, converter.convSharingModel(object.getSharingModel()));
        excel.setValue(deploy, converter.convDeploymentStatus(object.getDeploymentStatus()));
        excel.setValue(search, object.getEnableSearch());
        
        CustomField name = object.getNameField();
        if (name == null) {
            // when standard object
            excel.setValue(nameLabel, "");
            excel.setValue(type, "");
            excel.setValue(format, "");
            excel.setValueToEmpty(startNum, 0);
            return;
        }
        excel.setValue(nameLabel, name.getLabel());
        excel.setValue(type, converter.convFieldType(name.getType()));
        excel.setValue(format, name.getDisplayFormat());
        excel.setValueToEmpty(startNum, name.getStartingNumber());
    }

    @Override
    public Metadata[] getTargetMetadata() {
        String objApi = excel.getStringValue(fullName);
        LOGGER.info(String.format("get custom object: %s", objApi));

        String[] targetObject = new String[]{objApi};
        return conn.readMetadata("CustomObject", targetObject);
    }
    
    private void setStartingNumber(CustomField field) {
        Integer startNum = excel.getNumericValue(this.startNum);
        if (startNum == null) return;
        field.setStartingNumber(startNum);
        
    }
}
