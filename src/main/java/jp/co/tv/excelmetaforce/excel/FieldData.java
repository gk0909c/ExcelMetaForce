package jp.co.tv.excelmetaforce.excel;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sforce.soap.metadata.CustomField;
import com.sforce.soap.metadata.CustomObject;
import com.sforce.soap.metadata.Metadata;
import com.sforce.soap.metadata.ValueSet;

import jp.co.tv.excelmetaforce.converter.ExcelToMetadata;
import jp.co.tv.excelmetaforce.converter.MetadataToExcel;

public class FieldData extends SheetData {
    public static final String SHEET_NAME = "項目定義";
    private static final int START_ROW = 7;
    private static final Logger LOGGER = LoggerFactory.getLogger(FieldData.class);
    
    private final CellInfo objectFullName = new CellInfo(0, 27, 0);

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
    // TODO consider picklist sort 
    private final CellInfo sortPicklist = new CellInfo(0, 58, 4);
    private final CellInfo globalPicklist = new CellInfo(0, 62, 7);
    private final CellInfo trackHistory = new CellInfo(0, 69, 2);
    private final CellInfo referenceTo = new CellInfo(0, 71, 7);
    private final CellInfo relationName = new CellInfo(0, 78, 7);
    private final CellInfo relationLabel = new CellInfo(0, 85, 7);
    private final CellInfo deleteConstraint = new CellInfo(0, 92, 4);
    private final CellInfo writeByReadAuth = new CellInfo(0, 96, 4);
    private final CellInfo reparentable = new CellInfo(0, 100, 4);
    private final CellInfo defaultValue = new CellInfo(0, 104, 7);
    private final CellInfo visibleLines = new CellInfo(0, 111, 2);
    private final CellInfo maskChar = new CellInfo(0, 113, 2);
    private final CellInfo maskType = new CellInfo(0, 115, 6);
    private final CellInfo displayFormat = new CellInfo(0, 121, 6);


    public FieldData(Workbook book) {
        super(book, SHEET_NAME);
    }

    @Override
    public Metadata[] read() {
        ExcelToMetadata converter = new ExcelToMetadata();
        int targetRow = START_ROW;
        List<CustomField> fields = new ArrayList<CustomField>();
        
        while (!excel.isEmpty(targetRow, fullName.getCol())) {
            if (excel.isEmpty(targetRow, isTarget.getCol())) {
                targetRow++;
                continue;
            }
            updateRow(targetRow);

            CustomField field = new CustomField();
            
            field.setFullName(excel.getStringValue(fullName));
            field.setLabel(excel.getStringValue(label));
            field.setType(converter.convFieldType(excel.getStringValue(type)));
            setLength(field, converter);
            field.setDescription(excel.getStringValue(description));
            field.setInlineHelpText(excel.getStringValue(helpText));
            field.setRequired(excel.getBooleanValue(required));
            field.setUnique(converter.getUnique(excel.getStringValue(unique)));
            field.setCaseSensitive(converter.getCaseSensitive(excel.getStringValue(unique)));
            field.setExternalId(excel.getBooleanValue(externalId));
            field.setValueSet(converter.getGlobalPick(excel.getStringValue(globalPicklist)));
            field.setTrackFeedHistory(excel.getBooleanValue(trackHistory));
            field.setReferenceTo(excel.getStringValue(referenceTo));
            field.setRelationshipName(excel.getStringValue(relationName));
            field.setRelationshipLabel(excel.getStringValue(relationLabel));
            field.setDeleteConstraint(converter.convDeleteConstraint(excel.getStringValue(deleteConstraint)));
            field.setWriteRequiresMasterRead(excel.getBooleanValue(writeByReadAuth));
            field.setReparentableMasterDetail(excel.getBooleanValue(reparentable));
            field.setDefaultValue(excel.getStringValue(defaultValue));
            field.setVisibleLines(converter.getVisibleLines(excel.getStringValue(visibleLines)));
            field.setMaskChar(converter.convMaskChar(excel.getStringValue(maskChar)));
            field.setMaskType(converter.convMaskType(excel.getStringValue(maskType)));
            field.setDisplayFormat(excel.getStringValue(displayFormat));
            
            targetRow++;
            fields.add(field);
        }
        
        return fields.toArray(new CustomField[]{});
    }

    @Override
    public void write(Metadata... fields) {
        MetadataToExcel converter = new MetadataToExcel();
        int targetRow = START_ROW;
        int headerRowRange = START_ROW - 1;

        for (Metadata target : fields) {
            CustomField field = (CustomField)target;
            updateRow(targetRow);
            
            excel.setValue(rowNo, targetRow - headerRowRange);
            excel.setValue(fullName, field.getFullName());
            excel.setValue(label, field.getLabel());
            excel.setValue(type, converter.convFieldType(field.getType()));
            writeLength(field, converter);
            excel.setValue(description, field.getDescription());
            excel.setValue(helpText, field.getInlineHelpText());
            excel.setValue(required, field.getRequired());
            excel.setValue(unique, converter.getUnique(field.getUnique(), field.getCaseSensitive()));
            excel.setValue(externalId, field.getExternalId());
            // Todo Picklist sort
            excel.setValue(globalPicklist, getGlobalPicklistName(field.getValueSet()));
            excel.setValue(trackHistory, field.getTrackHistory());
            excel.setValue(referenceTo, field.getReferenceTo());
            excel.setValue(relationName, field.getRelationshipName());
            excel.setValue(relationLabel, field.getRelationshipLabel());
            excel.setValue(deleteConstraint, converter.convDeleteConstraint(field.getDeleteConstraint()));
            excel.setValue(writeByReadAuth, field.getWriteRequiresMasterRead());
            excel.setValue(reparentable, field.getReparentableMasterDetail());
            excel.setValue(defaultValue, field.getDefaultValue());
            excel.setValueToEmpty(visibleLines, field.getVisibleLines());
            excel.setValue(maskChar, converter.convMaskChar(field.getMaskChar()));
            excel.setValue(maskType, converter.convMaskType(field.getMaskType()));
            excel.setValue(displayFormat, field.getDisplayFormat());


            targetRow++;
        }
    }

    @Override
    public Metadata[] getTargetMetadata() {
        String objApi = excel.getStringValue(objectFullName);
        LOGGER.info(String.format("get custom object fields: %s", objApi));
        
        String[] targetObject = new String[]{objApi};
        Metadata[] customObject = conn.readMetadata("CustomObject", targetObject);
        
        return ((CustomObject)customObject[0]).getFields();
    }
    
    private void setLength(CustomField field, ExcelToMetadata converter) {
        int lengthVal = excel.getNumericValue(length).intValue();

        if (converter.isNumericType(field.getType())) {
            int scaleVal = excel.getNumericValue(scale).intValue();
            field.setPrecision(lengthVal + scaleVal);
            field.setScale(scaleVal);
        } else {
            field.setLength(lengthVal); 
        }
    }
    
    private void writeLength(CustomField field, MetadataToExcel converter) {
        if (converter.isNumericType(field.getType())) {
            excel.setValue(length, field.getPrecision() - field.getScale());
            excel.setValue(scale, field.getScale());
        } else {
            excel.setValueToEmpty(length, field.getLength());
            excel.setValue(scale, "");
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
    
    private String getGlobalPicklistName(ValueSet val) {
        return val == null ? StringUtils.EMPTY : val.getValueSetName();
    }
}
