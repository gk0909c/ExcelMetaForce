package jp.co.tv.excelmetaforce.excel;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.sforce.soap.metadata.CustomField;
import com.sforce.soap.metadata.DeleteConstraint;
import com.sforce.soap.metadata.EncryptedFieldMaskChar;
import com.sforce.soap.metadata.EncryptedFieldMaskType;
import com.sforce.soap.metadata.FieldType;
import com.sforce.soap.metadata.Metadata;
import com.sforce.soap.metadata.ValueSet;

public class FieldDataTest {
    @Rule
    public ExpectedException notImplement = ExpectedException.none();
    
    @Test
    public void testRead() {
        Workbook book = new XSSFWorkbook();
        Sheet fieldSheet = book.createSheet(FieldData.SHEET_NAME);
        writeTestSheet(fieldSheet);

        FieldData data = new FieldData(book);
        CustomField[] fields = (CustomField[])data.read();

        assertThat(fields.length, is(2));
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
        assertThat(fields[0].getValueSet().getValueSetName(), is("Global"));
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

        assertThat(fields[1].getFullName(), is("fullname3__c"));
        assertThat(fields[1].getType(), is(FieldType.Text));
        assertThat(fields[1].getLength(), is(3));
    }
    
    @Test
    public void testWrite() {
        CustomField field1 = createBaseField("field1__c");
        CustomField field2 = createBaseField("field1__c");
        field2.setType(FieldType.Number);
        field2.setPrecision(3);
        field2.setScale(2);

        Workbook book = new XSSFWorkbook();
        Sheet fieldSheet = book.createSheet(FieldData.SHEET_NAME);
        FieldData data = new FieldData(book);
        data.write(new Metadata[]{field1, field2});
        
        // Todo continue from here 
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
    }
    
    private CustomField createBaseField(String fullName) {
        CustomField baseField = new CustomField();
        
        baseField.setFullName(fullName);
        baseField.setLabel("field label");
        baseField.setType(FieldType.Text);
        baseField.setLength(3);
        baseField.setDescription("field description");
        baseField.setInlineHelpText("help text");
        baseField.setRequired(true);
        baseField.setUnique(true);
        baseField.setCaseSensitive(false);
        baseField.setExternalId(false);
        // TODO set sort picklist
        ValueSet globalPicklist = new ValueSet();
        globalPicklist.setValueSetName("globalPicklist");
        baseField.setValueSet(globalPicklist);
        baseField.setTrackFeedHistory(true);
        baseField.setReferenceTo("Reference__c");
        baseField.setRelationshipName("RelationName");
        baseField.setRelationshipLabel("RelationLabel");
        baseField.setDeleteConstraint(DeleteConstraint.SetNull);
        baseField.setWriteRequiresMasterRead(false);
        baseField.setReparentableMasterDetail(false);
        baseField.setDefaultValue("default");
        baseField.setVisibleLines(0);
        baseField.setMaskChar(EncryptedFieldMaskChar.X);
        baseField.setMaskType(EncryptedFieldMaskType.all);
        baseField.setDisplayFormat("format");
        
        return baseField;
    }
    
    @Test
    public void testGetMetadataTypeNotImplement() {
        Workbook book = new XSSFWorkbook();
        FieldData data = new FieldData(book);

        notImplement.expect(RuntimeException.class);
        data.getMetadataType();
    }

    @Test
    public void testGetTargetMetadataNotImplement() {
        Workbook book = new XSSFWorkbook();
        FieldData data = new FieldData(book);

        notImplement.expect(RuntimeException.class);
        data.getTargetMetadata();
    }
}
