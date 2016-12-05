package jp.co.tv.excelmetaforce.excel;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Workbook;

import com.sforce.soap.metadata.CustomField;
import com.sforce.soap.metadata.FieldType;
import com.sforce.soap.metadata.Metadata;

import jp.co.tv.excelmetaforce.converter.ExcelToMetadata;

public class FieldData extends SheetData {
    public static final String SHEET_NAME = "項目定義";
    private static final int START_ROW = 7;
    
    private final CellInfo objFullName = new CellInfo(0, 27, 0);

    private final CellInfo isTarget = new CellInfo(0, 0, 0);
    private final CellInfo rowNo = new CellInfo(0, 1, 2);
    private final CellInfo fullName = new CellInfo(0, 3, 7);
    private final CellInfo label = new CellInfo(0, 10, 7);
    private final CellInfo type = new CellInfo(0, 17, 6);
    private final CellInfo length = new CellInfo(0, 23, 3);
    private final CellInfo scale = new CellInfo(0, 26, 2);
    private final CellInfo description = new CellInfo(0, 28, 12);
    private final CellInfo helpText = new CellInfo(0, 40, 12);
    private final CellInfo required = new CellInfo(0, 52, 2);
    private final CellInfo unique = new CellInfo(0, 54, 2);
    private final CellInfo externalId = new CellInfo(0, 56, 2);
    private final CellInfo sortPicklist = new CellInfo(0, 58, 4);
    private final CellInfo globalPicklist = new CellInfo(0, 62, 7);
    private final CellInfo trackHistory = new CellInfo(0, 69, 2);
    private final CellInfo referenceTo = new CellInfo(0, 71, 7);
    private final CellInfo relationName = new CellInfo(0, 78, 7);
    private final CellInfo relationLabel = new CellInfo(0, 85, 7);
    private final CellInfo deleteConstraint = new CellInfo(0, 92, 7);
    private final CellInfo wirteByReadAuth = new CellInfo(0, 96, 4);
    private final CellInfo reparentable = new CellInfo(0, 100, 4);
    private final CellInfo defaultValue = new CellInfo(0, 104, 4);
    private final CellInfo visibleLines = new CellInfo(0, 111, 7);
    private final CellInfo maskChar = new CellInfo(0, 113, 2);
    private final CellInfo maskType = new CellInfo(0, 115, 2);
    private final CellInfo displayFormat = new CellInfo(0, 121, 6);


    public FieldData(Workbook book) {
        super(book, SHEET_NAME);
    }

    @Override
    public Metadata[] read() {
        ExcelToMetadata converter = new ExcelToMetadata();
        int tmpRow = START_ROW;
        List<CustomField> fields = new ArrayList<CustomField>();
        
        while (!excel.isEmpty(tmpRow, fullName.getCol())) {
            if (excel.isEmpty(tmpRow, isTarget.getCol())) {
                tmpRow++;
                continue;
            }
            updateRow(tmpRow);

            CustomField field = new CustomField();
            
            field.setFullName(excel.getStringValue(fullName));
            field.setLabel(excel.getStringValue(label));
            field.setType(converter.convertType(excel.getStringValue(type)));
            setLength(field);
            field.setDescription(excel.getStringValue(description));
            field.setInlineHelpText(excel.getStringValue(helpText));
            field.setRequired(excel.getBooleanValue(required));
            field.setUnique(converter.getUnique(excel.getStringValue(unique)));
            field.setCaseSensitive(converter.getCaseSensitive(excel.getStringValue(unique)));
            field.setExternalId(excel.getBooleanValue(externalId));
            // TODO set sort picklist
            field.setValueSet(converter.getGlobalPick(excel.getStringValue(globalPicklist)));
            field.setTrackFeedHistory(excel.getBooleanValue(trackHistory));
            field.setReferenceTo(excel.getStringValue(referenceTo));
            field.setRelationshipName(excel.getStringValue(relationName));
            field.setRelationshipLabel(excel.getStringValue(relationLabel));
            field.setDeleteConstraint(converter.deleteConstraint(excel.getStringValue(deleteConstraint)));
            field.setWriteRequiresMasterRead(excel.getBooleanValue(wirteByReadAuth));
            field.setReparentableMasterDetail(excel.getBooleanValue(reparentable));
            field.setDefaultValue(excel.getStringValue(defaultValue));
            setVisibleLines(field);
            field.setMaskChar(converter.getMaskChar(excel.getStringValue(maskChar)));
            field.setMaskType(converter.getMaskType(excel.getStringValue(maskType)));
            field.setDisplayFormat(excel.getStringValue(displayFormat));
            
            tmpRow++;
            fields.add(field);
        }
        
        return fields.toArray(new CustomField[]{});
    }

    @Override
    public void write(Metadata... metadata) {
    }

    @Override
    public String getMetadataType() {
        throw new RuntimeException("not implement");
    }

    @Override
    public String[] getTargetMetadata() {
        throw new RuntimeException("not implement");
    }
    
    private void setLength(CustomField field) {
        int lengthVal = excel.getNumericValue(length).intValue();

        if (field.getType().equals(FieldType.Number)
            || field.getType().equals(FieldType.Currency)
            || field.getType().equals(FieldType.Percent)) {

            int scaleVal = excel.getNumericValue(scale).intValue();

            field.setPrecision(lengthVal + scaleVal);
            field.setScale(scaleVal);
        } else {
            if (lengthVal != 0) {
                field.setLength(lengthVal);
            }
        }
    }
    
    private void setVisibleLines(CustomField field) {
        if (field.getType().equals(FieldType.LongTextArea)
             || field.getType().equals(FieldType.MultiselectPicklist)) {

            field.setVisibleLines(excel.getNumericValue(visibleLines).intValue());
        }
    }
    
    private void updateRow(int tmpRow) {
        try {
            java.lang.reflect.Field[] fields = this.getClass().getDeclaredFields();
            
            for (java.lang.reflect.Field field : fields) {
                if (!field.getType().equals(CellInfo.class)) continue;
                
                field.setAccessible(true);
                CellInfo cellInfo = (CellInfo)field.get(this);
                cellInfo.setRow(tmpRow);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
