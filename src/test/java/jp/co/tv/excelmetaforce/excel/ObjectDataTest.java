package jp.co.tv.excelmetaforce.excel;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Before;
import org.junit.Test;

import com.sforce.soap.metadata.CustomField;
import com.sforce.soap.metadata.CustomObject;
import com.sforce.soap.metadata.DeploymentStatus;
import com.sforce.soap.metadata.FieldType;
import com.sforce.soap.metadata.Metadata;
import com.sforce.soap.metadata.SharingModel;

import jp.co.tv.excelmetaforce.sfdc.Connector;

public class ObjectDataTest {
    private Workbook book;
    private Sheet objectSheet;
    
    @Before
    public void setup() {
        book = new XSSFWorkbook();
        objectSheet = book.createSheet(ObjectData.SHEET_NAME);
        objectSheet.createRow(0).createCell(27).setCellValue("ObjApiName__c");
        objectSheet.createRow(1).createCell(27).setCellValue("obj label");
    }

    @Test
    public void readObjectSheet() {
        // prepare
        writeObjectSheetData("FullName", "ObjectLabel", "Object Description",
                "○", "○", "○", "○", "公開（更新可能）", "リリース済", "○");
        writeObjectSheetNameField("TestNameField", "自動採番", "Test-{00000}", "1");

        ObjectData data = new ObjectData(book);
        CustomObject object = (CustomObject)data.read()[0];

        // assert
        assertObject(object, "FullName", "ObjectLabel", "Object Description",
                true, true, true, true, SharingModel.ReadWrite, DeploymentStatus.Deployed, true);
        assertNameField(object.getNameField(), "TestNameField", FieldType.AutoNumber, "Test-{00000}", 1);
    }

    @Test
    public void readObjectSheetTextNameField() {
        writeObjectSheetNameField("TestNameField2", "テキスト", "", "");

        ObjectData data = new ObjectData(book);
        CustomObject object = (CustomObject)data.read()[0];

        assertNameField(object.getNameField(), "TestNameField2", FieldType.Text, null, 0);
    }
    
    @Test
    public void writeObjectSheet() {
        
        CustomObject obj = createCustomObject("test description", true, false, true, true,
                SharingModel.Private, DeploymentStatus.Deployed, false);
        obj.setNameField(createNameField("name label", FieldType.Text));
        
        ObjectData data = new ObjectData(book);
        data.write(new Metadata[]{obj});
        
        assertObjectSheet("test description", "○", "", "○", "○", "非公開", "リリース済", "");
        assertObjectSheetNameField("name label", "テキスト", "", "");
    }
    
    @Test
    public void targetMetadataReturnsObject() {
        // Connector mock
        CustomObject object = new CustomObject();
        object.setFullName("MockObject__c");
        Connector mock = mock(Connector.class);
        when(mock.readMetadata(anyString(), any())).thenReturn(new Metadata[]{object});

        ObjectData data = new ObjectData(book);
        data.conn = mock;
        assertThat(data.getTargetMetadata(), is(new Metadata[]{object}));
    }
    
    private void writeObjectSheetData(String... info) {
        objectSheet.createRow(0).createCell(27).setCellValue(info[0]);
        objectSheet.createRow(1).createCell(27).setCellValue(info[1]);
        objectSheet.createRow(7).createCell(1).setCellValue(info[2]);
        objectSheet.createRow(16).createCell(11).setCellValue(info[3]);
        objectSheet.createRow(17).createCell(11).setCellValue(info[4]);
        objectSheet.createRow(18).createCell(11).setCellValue(info[5]);
        objectSheet.createRow(19).createCell(11).setCellValue(info[6]);
        objectSheet.createRow(20).createCell(11).setCellValue(info[7]);
        objectSheet.createRow(21).createCell(11).setCellValue(info[8]);
        objectSheet.createRow(22).createCell(11).setCellValue(info[9]);
    }
    
    private void writeObjectSheetNameField(String... info) {
        objectSheet.createRow(27).createCell(11).setCellValue(info[0]);
        objectSheet.createRow(28).createCell(11).setCellValue(info[1]);
        objectSheet.createRow(29).createCell(11).setCellValue(info[2]);
        objectSheet.createRow(30).createCell(11).setCellValue(info[3]);
    }
    
    private void assertObject(CustomObject object, Object... info) {
        assertThat(object.getFullName(), is(info[0]));
        assertThat(object.getLabel(), is(info[1]));
        assertThat(object.getDescription(), is(info[2]));
        assertThat(object.getEnableReports(), is(info[3]));
        assertThat(object.getEnableActivities(), is(info[4]));
        assertThat(object.getAllowInChatterGroups(), is(info[5]));
        assertThat(object.getEnableHistory(), is(info[6]));
        assertThat(object.getSharingModel(), is(info[7]));
        assertThat(object.getDeploymentStatus(), is(info[8]));
        assertThat(object.getEnableSearch(), is(info[9]));
    }
    
    private void assertNameField(CustomField field, Object... info) {
        assertThat(field.getLabel(), is(info[0]));
        assertThat(field.getType(), is(info[1]));
        assertThat(field.getDisplayFormat(), is(info[2]));
        assertThat(field.getStartingNumber(), is(info[3]));
    }
    
    private void assertObjectSheet(String... info) {
        assertThat(objectSheet.getRow(7).getCell(1).getStringCellValue(), is(info[0]));
        assertThat(objectSheet.getRow(16).getCell(11).getStringCellValue(), is(info[1]));
        assertThat(objectSheet.getRow(17).getCell(11).getStringCellValue(), is(info[2]));
        assertThat(objectSheet.getRow(18).getCell(11).getStringCellValue(), is(info[3]));
        assertThat(objectSheet.getRow(19).getCell(11).getStringCellValue(), is(info[4]));
        assertThat(objectSheet.getRow(20).getCell(11).getStringCellValue(), is(info[5]));
        assertThat(objectSheet.getRow(21).getCell(11).getStringCellValue(), is(info[6]));
        assertThat(objectSheet.getRow(22).getCell(11).getStringCellValue(), is(info[7]));
    }

    private void assertObjectSheetNameField(String... info) {
        assertThat(objectSheet.getRow(27).getCell(11).getStringCellValue(), is(info[0]));
        assertThat(objectSheet.getRow(28).getCell(11).getStringCellValue(), is(info[1]));
        assertThat(objectSheet.getRow(29).getCell(11).getStringCellValue(), is(info[2]));
        assertThat(objectSheet.getRow(30).getCell(11).getStringCellValue(), is(info[3]));
    }
    
    private CustomObject createCustomObject(Object... info) {
        CustomObject obj = new CustomObject();
        obj.setDescription((String)info[0]);
        obj.setEnableReports((boolean)info[1]);
        obj.setEnableActivities((boolean)info[2]);
        obj.setAllowInChatterGroups((boolean)info[3]);
        obj.setEnableHistory((boolean)info[4]);
        obj.setSharingModel((SharingModel)info[5]);
        obj.setDeploymentStatus((DeploymentStatus)info[6]);
        obj.setEnableSearch((boolean)info[7]);
        return obj;
    }
    
    private CustomField createNameField(Object... info) {
        CustomField field = new CustomField();
        field.setLabel((String)info[0]);
        field.setType((FieldType)info[1]);
        return field;
    }
}
