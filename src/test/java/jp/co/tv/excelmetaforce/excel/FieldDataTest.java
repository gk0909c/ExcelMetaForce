package jp.co.tv.excelmetaforce.excel;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Before;
import org.junit.Test;

import com.sforce.soap.metadata.CustomField;
import com.sforce.soap.metadata.CustomObject;
import com.sforce.soap.metadata.CustomValue;
import com.sforce.soap.metadata.DeleteConstraint;
import com.sforce.soap.metadata.EncryptedFieldMaskChar;
import com.sforce.soap.metadata.EncryptedFieldMaskType;
import com.sforce.soap.metadata.FieldType;
import com.sforce.soap.metadata.Metadata;
import com.sforce.soap.metadata.ValueSet;
import com.sforce.soap.metadata.ValueSetValuesDefinition;

import jp.co.tv.excelmetaforce.sfdc.Connector;

public class FieldDataTest {
    private static final String OBJ_API = "ObjectApi__c";
    private Workbook book;
    private Sheet fieldSheet;
    private Sheet picklistSheet;

    @Before
    public void setup() {
        book = new XSSFWorkbook();
        fieldSheet = book.createSheet(FieldData.SHEET_NAME);
        picklistSheet = book.createSheet(FieldData.PicklistData.SHEET_NAME);
        
        Row headerRow = fieldSheet.createRow(0);
        headerRow.createCell(27).setCellValue(OBJ_API);
    }
    
    @Test
    public void readFieldInfoFromExcel() {
        writeField(1, true,
                "fullname__c", "label", "数値", "3", "2", "description", "help text",
                "○", "", "○", "○", "", "", "Reference__c", "relation1", "relation2",
                "○", "", "○", "default", "", "*", "全ての文字をマスク", "display format");
        writeFieldText(2, false, "fullname2__c", "テキスト", "5", "", "");
        writeFieldText(3, true,  "fullname3__c", "テキスト", "3", "", "");
        writeFieldPicklist(4, true, "fullname4__c", "複数選択リスト", "", "", "", "GlobalPicklist");
        writeFieldPicklist(5, true, "fullname5__c", "選択リスト", "", "", "", "");
        writeFieldPicklist(6, true, "fullname6__c", "選択リスト", "", "", "", "");

        writePicklist(1, "fullname5__c", "Value1", "○");
        writePicklist(2, "fullname5__c", "Value2", "");
        writePicklist(3, "fullname6__c", "Value3", "");

        FieldData data = new FieldData(book);
        CustomField[] fields = (CustomField[])data.read();

        // field count
        assertThat(fields.length, is(5));
        // first field
        assertField(fields[0],
                "fullname__c", "label", FieldType.Number, 5, 2, "description", "help text",
                true, false, true, null, false, "Reference__c", "relation1", "relation2",
                DeleteConstraint.SetNull, false, true, "default", 0,
                EncryptedFieldMaskChar.asterisk, EncryptedFieldMaskType.all, "display format");
        // second field
        assertFieldTextType(fields[1], "fullname3__c", FieldType.Text, 3);
        // third field
        assertGlobalPicklist(fields[2], "GlobalPicklist");
        // 4th field（only check picklist）
        assertPicklist(fields[3], false, 0, 2, "Value1", true);
        assertPicklist(fields[3], false, 1, 2, "Value2", false);
        // 5th field（only check picklist）
        assertPicklist(fields[4], false, 0, 1, "Value3", false);
    }
    
    @Test
    public void readFieldInfoLongTextArea() {
        writeField(1, true,
                "fullname__c", "long text area", "ロングテキストエリア", "", "", "", "",
                "", "", "", "", "", "", "", "", "",
                "", "", "", "", "3", "", "", "");

        FieldData data = new FieldData(book);
        CustomField[] fields = (CustomField[])data.read();

        // field count
        assertThat(fields.length, is(1));
        // first field
        assertField(fields[0],
                "fullname__c", "long text area", FieldType.LongTextArea, 0, 0, null, null,
                false, false, false, null, false, null, null, null,
                null, false, false, null, 3,
                null, null, null);
    }
    
    @Test
    public void writeFieldInfoToExcel() {
        // prepare
        final CustomField field1 = createField("field1__c", "label1", FieldType.Text,
                3, "field description", "inline help text", true, true, false, false, true,
                "Reference2__c", "RelationName", "RelationLabel", DeleteConstraint.SetNull,
                false, false, "default value", 0,
                EncryptedFieldMaskChar.X, EncryptedFieldMaskType.all, "format");
        final CustomField field2 = createNumericField("field2__c", FieldType.Number, 3, 2);
        final CustomField field3 = createGlobalPicklistField("field3__c", "label3", FieldType.MultiselectPicklist,
                "GlobalPicklistName");
        final CustomField field4 = createPicklistField("field4__c", "label4", FieldType.Picklist,
                true, new Object[]{"Select1", true, "Select2", false});
        final CustomField field5 = createPicklistField("field5__c", "label5", FieldType.Picklist,
                false, new Object[]{"Select3", true});

        FieldData data = new FieldData(book);
        data.write(new Metadata[]{field1, field2, field3, field4, field5});
        
        // assert field sheet
        assertFieldSheet(1, "field1__c", "label1", "テキスト", "3", "", "field description", "inline help text",
                "○", "△", "", "", "", "○", "Reference2__c", "RelationName", "RelationLabel",
                "○", "", "", "default value", "", "X", "全ての文字をマスク", "format");
        assertFieldSheetTypeNumeric(2, "field2__c", "数値", "1", "2");
        assertFieldSheetTypePicklist(3, "field3__c", "複数選択リスト", "", "GlobalPicklistName");
        assertFieldSheetTypePicklist(4, "field4__c", "選択リスト", "○", "");
        assertFieldSheetTypePicklist(5, "field5__c", "選択リスト", "", "");
        // assert picklist sheet
        assertPicklistSheet(1, "field4__c", "label4", "Select1", "○");
        assertPicklistSheet(2, "field4__c", "label4", "Select2", "");
        assertPicklistSheet(3, "field5__c", "label5", "Select3", "○");
    }
    
    @Test
    public void getTargetMetadataReturnFields() {
        // Connector mock
        CustomObject object = new CustomObject();
        object.setFullName("MockObject__c");
        CustomField field1 = createBaseField("MockField1__c", "one", FieldType.Text);
        CustomField field2 = createBaseField("MockField2__c", "two", FieldType.Text);
        object.setFields(new CustomField[]{field1, field2});
        Connector mock = mock(Connector.class);
        when(mock.readMetadata(anyString(), any())).thenReturn(new Metadata[]{object});

        FieldData data = new FieldData(book);
        data.conn = mock;
        
        // assert
        assertThat(data.getTargetMetadata(), is(new Metadata[]{field1, field2}));
    }
    
    private void assertFieldSheet(int rowNum, Object... field) {
        Row row = createFirstRowAndAssertNumber(rowNum);

        assertThat(row.getCell(3).getStringCellValue(), is(field[0]));
        assertThat(row.getCell(10).getStringCellValue(), is(field[1]));
        assertThat(row.getCell(17).getStringCellValue(), is(field[2]));
        assertThat(row.getCell(23).getStringCellValue(), is(field[3]));
        assertThat(row.getCell(26).getStringCellValue(), is(field[4]));
        assertThat(row.getCell(28).getStringCellValue(), is(field[5]));
        assertThat(row.getCell(40).getStringCellValue(), is(field[6]));
        assertThat(row.getCell(52).getStringCellValue(), is(field[7]));
        assertThat(row.getCell(54).getStringCellValue(), is(field[8]));
        assertThat(row.getCell(56).getStringCellValue(), is(field[9]));
        assertThat(row.getCell(58).getStringCellValue(), is(field[10]));
        assertThat(row.getCell(62).getStringCellValue(), is(field[11]));
        assertThat(row.getCell(69).getStringCellValue(), is(field[12]));
        assertThat(row.getCell(71).getStringCellValue(), is(field[13]));
        assertThat(row.getCell(78).getStringCellValue(), is(field[14]));
        assertThat(row.getCell(85).getStringCellValue(), is(field[15]));
        assertThat(row.getCell(92).getStringCellValue(), is(field[16]));
        assertThat(row.getCell(96).getStringCellValue(), is(field[17]));
        assertThat(row.getCell(100).getStringCellValue(), is(field[18]));
        assertThat(row.getCell(104).getStringCellValue(), is(field[19]));
        assertThat(row.getCell(111).getStringCellValue(), is(field[20]));
        assertThat(row.getCell(113).getStringCellValue(), is(field[21]));
        assertThat(row.getCell(115).getStringCellValue(), is(field[22]));
        assertThat(row.getCell(121).getStringCellValue(), is(field[23]));
    }

    private void assertFieldSheetTypeNumeric(int rowNum, Object... field) {
        Row row = createFirstRowAndAssertNumber(rowNum);
        assertThat(row.getCell(3).getStringCellValue(), is(field[0]));
        assertThat(row.getCell(17).getStringCellValue(), is(field[1]));
        assertThat(row.getCell(23).getStringCellValue(), is(field[2]));
        assertThat(row.getCell(26).getStringCellValue(), is(field[3]));
        assertThat(row.getCell(62).getStringCellValue(), is(StringUtils.EMPTY));
    }

    private void assertFieldSheetTypePicklist(int rowNum, Object... field) {
        Row row = createFirstRowAndAssertNumber(rowNum);
        assertThat(row.getCell(3).getStringCellValue(), is(field[0]));
        assertThat(row.getCell(17).getStringCellValue(), is(field[1]));
        assertThat(row.getCell(58).getStringCellValue(), is(field[2]));
        assertThat(row.getCell(62).getStringCellValue(), is(field[3]));
    }
    
    private Row createFirstRowAndAssertNumber(int rowNum) {
        Row row = fieldSheet.getRow(rowNum + 6);
        assertThat(row.getCell(1).getStringCellValue(), is(String.valueOf(rowNum)));
        return row;
    }
    
    private void assertPicklistSheet(int rowNum, Object... picklist) {
        Row row = picklistSheet.getRow(rowNum + 5);
        assertThat(row.getCell(1).getStringCellValue(), is(String.valueOf(rowNum)));
        assertThat(row.getCell(3).getStringCellValue(), is(picklist[0]));
        assertThat(row.getCell(10).getStringCellValue(), is(picklist[1]));
        assertThat(row.getCell(17).getStringCellValue(), is(picklist[2]));
        assertThat(row.getCell(25).getStringCellValue(), is(picklist[3]));
    }
    
    private void writeField(int rowNum, boolean target, String... fields) {
        Row row = createFieldExcelRow(rowNum, target);
        row.createCell(3).setCellValue(fields[0]);
        row.createCell(10).setCellValue(fields[1]);
        row.createCell(17).setCellValue(fields[2]);
        row.createCell(23).setCellValue(fields[3]);
        row.createCell(26).setCellValue(fields[4]);
        row.createCell(28).setCellValue(fields[5]);
        row.createCell(40).setCellValue(fields[6]);
        row.createCell(52).setCellValue(fields[7]);
        row.createCell(54).setCellValue(fields[8]);
        row.createCell(56).setCellValue(fields[9]);
        row.createCell(58).setCellValue(fields[10]);
        row.createCell(62).setCellValue(fields[11]);
        row.createCell(69).setCellValue(fields[12]);
        row.createCell(71).setCellValue(fields[13]);
        row.createCell(78).setCellValue(fields[14]);
        row.createCell(85).setCellValue(fields[15]);
        row.createCell(92).setCellValue(fields[16]);
        row.createCell(96).setCellValue(fields[17]);
        row.createCell(100).setCellValue(fields[18]);
        row.createCell(104).setCellValue(fields[19]);
        row.createCell(111).setCellValue(fields[20]);
        row.createCell(113).setCellValue(fields[21]);
        row.createCell(115).setCellValue(fields[22]);
        row.createCell(121).setCellValue(fields[23]);
    }

    private void writeFieldText(int rowNum, boolean target, String... fields) {
        Row row = createFieldExcelRow(rowNum, target);
        row.createCell(3).setCellValue(fields[0]);
        row.createCell(17).setCellValue(fields[1]);
        row.createCell(23).setCellValue(fields[2]);
        row.createCell(28).setCellValue(fields[3]);
        row.createCell(40).setCellValue(fields[4]);
    }
    
    private void writeFieldPicklist(int rowNum, boolean target, String... fields) {
        Row row = createFieldExcelRow(rowNum, target);
        row.createCell(3).setCellValue(fields[0]);
        row.createCell(17).setCellValue(fields[1]);
        row.createCell(28).setCellValue(fields[2]);
        row.createCell(40).setCellValue(fields[3]);
        row.createCell(58).setCellValue(fields[4]);
        row.createCell(62).setCellValue(fields[5]);
    }
    
    private Row createFieldExcelRow(int rowNum, boolean target) {
        Row row = fieldSheet.createRow(rowNum + 6);
        row.createCell(0).setCellValue(target ? "a" : "");
        return row;
    }
    
    private void assertField(CustomField field, Object... fieldInfo) {
        assertThat(field.getFullName(), is(String.format("%s.%s", OBJ_API, fieldInfo[0])));
        assertThat(field.getLabel(), is(fieldInfo[1]));
        assertThat(field.getType(), is(fieldInfo[2]));
        assertThat(field.getPrecision(), is(fieldInfo[3]));
        assertThat(field.getScale(), is(fieldInfo[4]));
        assertThat(field.getDescription(), is(fieldInfo[5]));
        assertThat(field.getInlineHelpText(), is(fieldInfo[6]));
        assertThat(field.getRequired(), is(fieldInfo[7]));
        assertThat(field.getUnique(), is(fieldInfo[8]));
        assertThat(field.getExternalId(), is(fieldInfo[9]));
        assertThat(field.getValueSet(), is(fieldInfo[10]));
        assertThat(field.getTrackHistory(), is(fieldInfo[11]));
        assertThat(field.getReferenceTo(), is(fieldInfo[12]));
        assertThat(field.getRelationshipName(), is(fieldInfo[13]));
        assertThat(field.getRelationshipLabel(), is(fieldInfo[14]));
        assertThat(field.getDeleteConstraint(), is(fieldInfo[15]));
        assertThat(field.getWriteRequiresMasterRead(), is(fieldInfo[16]));
        assertThat(field.getReparentableMasterDetail(), is(fieldInfo[17]));
        assertThat(field.getDefaultValue(), is(fieldInfo[18]));
        assertThat(field.getVisibleLines(), is(fieldInfo[19]));
        assertThat(field.getMaskChar(), is(fieldInfo[20]));
        assertThat(field.getMaskType(), is(fieldInfo[21]));
        assertThat(field.getDisplayFormat(), is(fieldInfo[22]));

    }

    private void assertFieldTextType(CustomField field, Object... fieldInfo) {
        assertThat(field.getFullName(), is(String.format("%s.%s", OBJ_API, fieldInfo[0])));
        assertThat(field.getType(), is(fieldInfo[1]));
        assertThat(field.getLength(), is(fieldInfo[2]));
    }

    private void writePicklist(int rowNum, String... picklist) {
        Row row = picklistSheet.createRow(rowNum + 5);
        row.createCell(3).setCellValue(picklist[0]);
        row.createCell(17).setCellValue(picklist[1]);
        row.createCell(25).setCellValue(picklist[2]);
    }
    
    private void assertGlobalPicklist(CustomField field, String globalName) {
        assertThat(field.getValueSet().getValueSetName(), is(globalName));
    }
    
    private void assertPicklist(CustomField field, boolean sorted, int pickIdx, Object... picklist) {
        ValueSet valSet = field.getValueSet();
        assertThat(valSet.getValueSetName(), nullValue());
        
        ValueSetValuesDefinition def = valSet.getValueSetDefinition();
        assertThat(def.getValue().length, is(picklist[0]));
        assertThat(def.getSorted(), is(sorted));
        assertThat(def.getValue()[pickIdx].getFullName(), is(picklist[1]));
        assertThat(def.getValue()[pickIdx].getDefault(), is(picklist[2]));
    }
    
    private CustomField createField(String fullName, String label, FieldType type, Object... fieldInfo) {
        final CustomField field = createBaseField(fullName, label, type); 
        
        field.setLength((int)fieldInfo[0]);
        field.setDescription((String)fieldInfo[1]);
        field.setInlineHelpText((String)fieldInfo[2]);
        field.setRequired((boolean)fieldInfo[3]);
        field.setUnique((boolean)fieldInfo[4]);
        field.setCaseSensitive((boolean)fieldInfo[5]);
        field.setExternalId((boolean)fieldInfo[6]);
        field.setTrackHistory((boolean)fieldInfo[7]);
        field.setReferenceTo((String)fieldInfo[8]);
        field.setRelationshipName((String)fieldInfo[9]);
        field.setRelationshipLabel((String)fieldInfo[10]);
        field.setDeleteConstraint((DeleteConstraint)fieldInfo[11]);
        field.setWriteRequiresMasterRead((boolean)fieldInfo[12]);
        field.setReparentableMasterDetail((boolean)fieldInfo[13]);
        field.setDefaultValue((String)fieldInfo[14]);
        field.setVisibleLines((int)fieldInfo[15]);
        field.setMaskChar((EncryptedFieldMaskChar)fieldInfo[16]);
        field.setMaskType((EncryptedFieldMaskType)fieldInfo[17]);
        field.setDisplayFormat((String)fieldInfo[18]);
        
        return field;
    }
    
    private CustomField createNumericField(String fullName, FieldType type, int precision, int scale) {
        final CustomField field = createBaseField(fullName, "", type); 
        field.setPrecision(precision);
        field.setScale(scale);

        return field;
    }

    private CustomField createGlobalPicklistField(String fullName, String label, FieldType type, String globalName) {
        final CustomField field = createBaseField(fullName, label, type); 
        ValueSet valSet = new ValueSet();
        valSet.setValueSetName(globalName);
        field.setValueSet(valSet);
        return field;
    }
    
    private CustomField createPicklistField(String fullName, String label, FieldType type,
            boolean sorted, Object... picklist) {
        final CustomField field = createBaseField(fullName, label, type); 

        List<CustomValue> customValues = new ArrayList<CustomValue>();
        for (int i = 0; i < picklist.length; i = i + 2) {
            CustomValue value = new CustomValue();
            value.setFullName((String)picklist[i]);
            value.setDefault((Boolean)picklist[i + 1]);
            customValues.add(value);
        }
        ValueSetValuesDefinition def = new ValueSetValuesDefinition();
        def.setSorted(sorted);
        def.setValue(customValues.toArray(new CustomValue[]{}));
        
        ValueSet valueSet = new ValueSet();
        valueSet.setValueSetDefinition(def);
        
        field.setValueSet(valueSet);
        return field;
    }
    
    private CustomField createBaseField(String fullName, String label, FieldType type) {
        CustomField field = new CustomField();
        field.setFullName(fullName);
        field.setLabel(label);
        field.setType(type);
        return field;
    }
}
