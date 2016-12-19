package jp.co.tv.excelmetaforce.excel;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

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
    @Rule
    public ExpectedException notImplement = ExpectedException.none();
    
    @Test
    public void testRead() {
        Workbook book = new XSSFWorkbook();
        Sheet fieldSheet = book.createSheet(FieldData.SHEET_NAME);
        Sheet picklistSheet = book.createSheet(FieldData.PicklistData.SHEET_NAME);
        writeTestSheet(fieldSheet);
        writePicklistSheet(picklistSheet);

        FieldData data = new FieldData(book);
        CustomField[] fields = (CustomField[])data.read();

        // field count
        assertThat(fields.length, is(5));

        // first field
        assertThat(fields[0].getFullName(), is("fullname__c"));
        assertThat(fields[0].getLabel(), is("label"));
        assertThat(fields[0].getType(), is(FieldType.Number));
        assertThat(fields[0].getPrecision(), is(5));
        assertThat(fields[0].getScale(), is(2));
        assertThat(fields[0].getDescription(), is("description"));
        assertThat(fields[0].getInlineHelpText(), is("help text"));
        assertThat(fields[0].getRequired(), is(true));
        assertThat(fields[0].getUnique(), is(false));
        assertThat(fields[0].getExternalId(), is(true));
        assertThat(fields[0].getValueSet(), nullValue());
        assertThat(fields[0].getTrackHistory(), is(false));
        assertThat(fields[0].getReferenceTo(), is("Reference__c"));
        assertThat(fields[0].getRelationshipName(), is("relation1"));
        assertThat(fields[0].getRelationshipLabel(), is("relation2"));
        assertThat(fields[0].getDeleteConstraint(), is(DeleteConstraint.SetNull));
        assertThat(fields[0].getWriteRequiresMasterRead(), is(false));
        assertThat(fields[0].getReparentableMasterDetail(), is(true));
        assertThat(fields[0].getDefaultValue(), is("default"));
        assertThat(fields[0].getVisibleLines(), is(0));
        assertThat(fields[0].getMaskChar(), is(EncryptedFieldMaskChar.asterisk));
        assertThat(fields[0].getMaskType(), is(EncryptedFieldMaskType.all));
        assertThat(fields[0].getDisplayFormat(), is("display format"));

        // second field
        assertThat(fields[1].getFullName(), is("fullname3__c"));
        assertThat(fields[1].getType(), is(FieldType.Text));
        assertThat(fields[1].getLength(), is(3));

        // third field
        assertThat(fields[2].getValueSet().getValueSetName(), is("GlobalPicklist"));

        // 4th field
        assertThat(fields[3].getValueSet().getValueSetName(), nullValue());
        ValueSetValuesDefinition picklist1 = fields[3].getValueSet().getValueSetDefinition();
        assertThat(picklist1.getSorted(), is(false));
        assertThat(picklist1.getValue().length, is(2));
        assertThat(picklist1.getValue()[0].getFullName(), is("Value1"));
        assertThat(picklist1.getValue()[0].getDefault(), is(true));
        assertThat(picklist1.getValue()[1].getFullName(), is("Value2"));
        assertThat(picklist1.getValue()[1].getDefault(), is(false));

        // 5th field（only check picklist）
        ValueSetValuesDefinition picklist2 = fields[4].getValueSet().getValueSetDefinition();
        assertThat(picklist2.getValue().length, is(1));
        assertThat(picklist2.getValue()[0].getFullName(), is("Value3"));
        assertThat(picklist2.getValue()[0].getDefault(), is(false));
    }
    
    @Test
    public void testWrite() {
        final CustomField field1 = createBaseField("field1__c");

        final CustomField field2 = createBaseField("field2__c");
        field2.setType(FieldType.Number);
        field2.setPrecision(3);
        field2.setScale(2);
        field2.setValueSet(null);

        final CustomField field3 = createBaseField("field3__c");
        field3.setType(FieldType.MultiselectPicklist);
        ValueSet globalValueSet = new ValueSet();
        globalValueSet.setValueSetName("GlobalPicklistName");
        field3.setValueSet(globalValueSet);

        final CustomField field4 = createBaseField("field4__c");
        field4.setLabel("picklist field label");
        field4.setType(FieldType.Picklist);
        ValueSet valueSet = new ValueSet();
        valueSet.setValueSetDefinition(getPicklist());
        field4.setValueSet(valueSet);

        final CustomField field5 = createBaseField("field5__c");
        field5.setType(FieldType.Picklist);
        ValueSet valueSet2 = new ValueSet();
        valueSet2.setValueSetDefinition(getPicklist());
        field5.setValueSet(valueSet2);

        Workbook book = new XSSFWorkbook();
        Sheet fieldSheet = book.createSheet(FieldData.SHEET_NAME);
        final Sheet picklistSheet = book.createSheet(FieldData.PicklistData.SHEET_NAME);
        FieldData data = new FieldData(book);
        data.write(new Metadata[]{field1, field2, field3, field4, field5});
        
        // first field
        assertThat(fieldSheet.getRow(7).getCell(1).getStringCellValue(), is("1"));
        assertThat(fieldSheet.getRow(7).getCell(3).getStringCellValue(), is("field1__c"));
        assertThat(fieldSheet.getRow(7).getCell(10).getStringCellValue(), is("field label"));
        assertThat(fieldSheet.getRow(7).getCell(17).getStringCellValue(), is("テキスト"));
        assertThat(fieldSheet.getRow(7).getCell(23).getStringCellValue(), is("3"));
        assertThat(fieldSheet.getRow(7).getCell(26).getStringCellValue(), is(""));
        assertThat(fieldSheet.getRow(7).getCell(28).getStringCellValue(), is("field description"));
        assertThat(fieldSheet.getRow(7).getCell(40).getStringCellValue(), is("inline help text"));
        assertThat(fieldSheet.getRow(7).getCell(52).getStringCellValue(), is("○"));
        assertThat(fieldSheet.getRow(7).getCell(54).getStringCellValue(), is("△"));
        assertThat(fieldSheet.getRow(7).getCell(56).getStringCellValue(), is(""));
        assertThat(fieldSheet.getRow(7).getCell(58).getStringCellValue(), is(""));
        assertThat(fieldSheet.getRow(7).getCell(62).getStringCellValue(), is(""));
        assertThat(fieldSheet.getRow(7).getCell(69).getStringCellValue(), is("○"));
        assertThat(fieldSheet.getRow(7).getCell(71).getStringCellValue(), is("Reference2__c"));
        assertThat(fieldSheet.getRow(7).getCell(78).getStringCellValue(), is("RelationName"));
        assertThat(fieldSheet.getRow(7).getCell(85).getStringCellValue(), is("RelationLabel"));
        assertThat(fieldSheet.getRow(7).getCell(92).getStringCellValue(), is("○"));
        assertThat(fieldSheet.getRow(7).getCell(96).getStringCellValue(), is(""));
        assertThat(fieldSheet.getRow(7).getCell(100).getStringCellValue(), is(""));
        assertThat(fieldSheet.getRow(7).getCell(104).getStringCellValue(), is("default value"));
        assertThat(fieldSheet.getRow(7).getCell(111).getStringCellValue(), is(""));
        assertThat(fieldSheet.getRow(7).getCell(113).getStringCellValue(), is("X"));
        assertThat(fieldSheet.getRow(7).getCell(115).getStringCellValue(), is("全ての文字をマスク"));
        assertThat(fieldSheet.getRow(7).getCell(121).getStringCellValue(), is("format"));

        // second field
        assertThat(fieldSheet.getRow(8).getCell(1).getStringCellValue(), is("2"));
        assertThat(fieldSheet.getRow(8).getCell(17).getStringCellValue(), is("数値"));
        assertThat(fieldSheet.getRow(8).getCell(23).getStringCellValue(), is("1"));
        assertThat(fieldSheet.getRow(8).getCell(26).getStringCellValue(), is("2"));
        assertThat(fieldSheet.getRow(8).getCell(62).getStringCellValue(), is(""));

        // thrid field
        assertThat(fieldSheet.getRow(9).getCell(1).getStringCellValue(), is("3"));
        assertThat(fieldSheet.getRow(9).getCell(17).getStringCellValue(), is("複数選択リスト"));
        assertThat(fieldSheet.getRow(9).getCell(58).getStringCellValue(), is(""));
        assertThat(fieldSheet.getRow(9).getCell(62).getStringCellValue(), is("GlobalPicklistName"));

        // 4th field
        assertThat(fieldSheet.getRow(10).getCell(1).getStringCellValue(), is("4"));
        assertThat(fieldSheet.getRow(10).getCell(58).getStringCellValue(), is(""));
        assertThat(fieldSheet.getRow(10).getCell(62).getStringCellValue(), is(""));

        // pick list sheet
        assertThat(picklistSheet.getRow(6).getCell(1).getStringCellValue(), is("1"));
        assertThat(picklistSheet.getRow(6).getCell(3).getStringCellValue(), is("field4__c"));
        assertThat(picklistSheet.getRow(6).getCell(10).getStringCellValue(), is("picklist field label"));
        assertThat(picklistSheet.getRow(6).getCell(17).getStringCellValue(), is("Select1"));
        assertThat(picklistSheet.getRow(6).getCell(25).getStringCellValue(), is("○"));

        assertThat(picklistSheet.getRow(7).getCell(1).getStringCellValue(), is("2"));
        assertThat(picklistSheet.getRow(7).getCell(3).getStringCellValue(), is("field4__c"));
        assertThat(picklistSheet.getRow(7).getCell(10).getStringCellValue(), is("picklist field label"));
        assertThat(picklistSheet.getRow(7).getCell(17).getStringCellValue(), is("Select2"));
        assertThat(picklistSheet.getRow(7).getCell(25).getStringCellValue(), is(""));

        assertThat(picklistSheet.getRow(8).getCell(1).getStringCellValue(), is("3"));
        assertThat(picklistSheet.getRow(8).getCell(3).getStringCellValue(), is("field5__c"));
        assertThat(picklistSheet.getRow(8).getCell(17).getStringCellValue(), is("Select1"));
        assertThat(picklistSheet.getRow(8).getCell(25).getStringCellValue(), is("○"));
    }
    
    private void writeTestSheet(Sheet fieldSheet) {
        Row row1 = fieldSheet.createRow(7);
        row1.createCell(0).setCellValue("a");
        row1.createCell(3).setCellValue("fullname__c");
        row1.createCell(10).setCellValue("label");
        row1.createCell(17).setCellValue("数値");
        row1.createCell(23).setCellValue("3");
        row1.createCell(26).setCellValue("2");
        row1.createCell(28).setCellValue("description");
        row1.createCell(40).setCellValue("help text");
        row1.createCell(52).setCellValue("○");
        row1.createCell(54).setCellValue("");
        row1.createCell(56).setCellValue("○");
        row1.createCell(58).setCellValue("○");
        row1.createCell(62).setCellValue("Global");
        row1.createCell(69).setCellValue("");
        row1.createCell(71).setCellValue("Reference__c");
        row1.createCell(78).setCellValue("relation1");
        row1.createCell(85).setCellValue("relation2");
        row1.createCell(92).setCellValue("○");
        row1.createCell(96).setCellValue("");
        row1.createCell(100).setCellValue("○");
        row1.createCell(104).setCellValue("default");
        row1.createCell(111).setCellValue("");
        row1.createCell(113).setCellValue("*");
        row1.createCell(115).setCellValue("全ての文字をマスク");
        row1.createCell(121).setCellValue("display format");

        Row row2 = fieldSheet.createRow(8);
        row2.createCell(3).setCellValue("fullname2__c");
        row2.createCell(17).setCellValue("テキスト");
        row2.createCell(23).setCellValue("3");

        Row row3 = fieldSheet.createRow(9);
        row3.createCell(0).setCellValue("a");
        row3.createCell(3).setCellValue("fullname3__c");
        row3.createCell(17).setCellValue("テキスト");
        row3.createCell(23).setCellValue("3");

        Row row4 = fieldSheet.createRow(10);
        row4.createCell(0).setCellValue("a");
        row4.createCell(3).setCellValue("fullname4__c");
        row4.createCell(17).setCellValue("複数選択リスト");
        row4.createCell(23).setCellValue("");
        row4.createCell(58).setCellValue("");
        row4.createCell(62).setCellValue("GlobalPicklist");

        Row row5 = fieldSheet.createRow(11);
        row5.createCell(0).setCellValue("a");
        row5.createCell(3).setCellValue("fullname5__c");
        row5.createCell(17).setCellValue("選択リスト");
        row5.createCell(23).setCellValue("");
        row5.createCell(58).setCellValue("");
        row5.createCell(62).setCellValue("");

        Row row6 = fieldSheet.createRow(12);
        row6.createCell(0).setCellValue("a");
        row6.createCell(3).setCellValue("fullname6__c");
        row6.createCell(17).setCellValue("選択リスト");
        row6.createCell(23).setCellValue("");
        row6.createCell(58).setCellValue("");
        row6.createCell(62).setCellValue("");
    }

    private void writePicklistSheet(Sheet picklistSheet) {
        Row row1 = picklistSheet.createRow(6);
        row1.createCell(3).setCellValue("fullname5__c");
        row1.createCell(17).setCellValue("Value1");
        row1.createCell(25).setCellValue("○");

        Row row2 = picklistSheet.createRow(7);
        row2.createCell(3).setCellValue("fullname5__c");
        row2.createCell(17).setCellValue("Value2");
        row2.createCell(25).setCellValue("");

        Row row3 = picklistSheet.createRow(8);
        row3.createCell(3).setCellValue("fullname6__c");
        row3.createCell(17).setCellValue("Value3");
        row3.createCell(25).setCellValue("");
    }
    
    private CustomField createBaseField(String fullName) {
        CustomField baseField = new CustomField();
        
        baseField.setFullName(fullName);
        baseField.setLabel("field label");
        baseField.setType(FieldType.Text);
        baseField.setLength(3);
        baseField.setDescription("field description");
        baseField.setInlineHelpText("inline help text");
        baseField.setRequired(true);
        baseField.setUnique(true);
        baseField.setCaseSensitive(false);
        baseField.setExternalId(false);
        ValueSet globalPicklist = new ValueSet();
        globalPicklist.setValueSetName("globalPicklist");
        baseField.setValueSet(globalPicklist);
        baseField.setTrackHistory(true);
        baseField.setReferenceTo("Reference2__c");
        baseField.setRelationshipName("RelationName");
        baseField.setRelationshipLabel("RelationLabel");
        baseField.setDeleteConstraint(DeleteConstraint.SetNull);
        baseField.setWriteRequiresMasterRead(false);
        baseField.setReparentableMasterDetail(false);
        baseField.setDefaultValue("default value");
        baseField.setVisibleLines(0);
        baseField.setMaskChar(EncryptedFieldMaskChar.X);
        baseField.setMaskType(EncryptedFieldMaskType.all);
        baseField.setDisplayFormat("format");
        
        return baseField;
    }
    
    private ValueSetValuesDefinition getPicklist() {
        CustomValue val1 = new CustomValue();
        val1.setFullName("Select1");
        val1.setDefault(true);
        CustomValue val2 = new CustomValue();
        val2.setFullName("Select2");
        val2.setDefault(false);
        
        ValueSetValuesDefinition picklist = new ValueSetValuesDefinition();
        picklist.setSorted(false);
        picklist.setValue(new CustomValue[]{val1, val2});
        
        return picklist;
    }
    
    @Test
    public void testGetTargetMetadata() {
        Workbook book = new XSSFWorkbook();
        final Sheet sheet = book.createSheet(FieldData.SHEET_NAME);
        sheet.createRow(0).createCell(27).setCellValue("ObjApiName__c");
        sheet.createRow(1).createCell(27).setCellValue("obj label");
        
        // Connector mock
        CustomObject object = new CustomObject();
        object.setFullName("MockObject__c");
        CustomField field1 = new CustomField();
        field1.setFullName("MockField1__c");
        CustomField field2 = new CustomField();
        field2.setFullName("MockField2__c");
        object.setFields(new CustomField[]{field1, field2});
        Connector mock = mock(Connector.class);
        when(mock.readMetadata(anyString(), any())).thenReturn(new Metadata[]{object});

        FieldData data = new FieldData(book);
        data.conn = mock;
        assertThat(data.getTargetMetadata(), is(new Metadata[]{field1, field2}));
    }
}
