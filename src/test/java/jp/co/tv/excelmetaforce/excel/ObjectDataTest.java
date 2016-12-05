package jp.co.tv.excelmetaforce.excel;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import com.sforce.soap.metadata.CustomField;
import com.sforce.soap.metadata.CustomObject;
import com.sforce.soap.metadata.DeploymentStatus;
import com.sforce.soap.metadata.FieldType;
import com.sforce.soap.metadata.Metadata;
import com.sforce.soap.metadata.SharingModel;

public class ObjectDataTest {
    @Test
    public void testRead() {
        Workbook book = new XSSFWorkbook();
        Sheet objectSheet = book.createSheet("オブジェクト定義");
        objectSheet.createRow(0).createCell(27).setCellValue("FullName");
        objectSheet.createRow(1).createCell(27).setCellValue("ObjectLabel");
        objectSheet.createRow(7).createCell(1).setCellValue("Object Description");
        objectSheet.createRow(16).createCell(10).setCellValue("○");
        objectSheet.createRow(17).createCell(10).setCellValue("○");
        objectSheet.createRow(18).createCell(10).setCellValue("○");
        objectSheet.createRow(19).createCell(10).setCellValue("○");
        objectSheet.createRow(20).createCell(10).setCellValue("公開（更新可能）");
        objectSheet.createRow(21).createCell(10).setCellValue("○");
        objectSheet.createRow(22).createCell(10).setCellValue("○");

        objectSheet.createRow(27).createCell(10).setCellValue("TestNameField");
        objectSheet.createRow(28).createCell(10).setCellValue("自動採番");
        objectSheet.createRow(29).createCell(10).setCellValue("Test-{00000}");
        objectSheet.createRow(30).createCell(10).setCellValue("1");

        ObjectData data = new ObjectData(book);
        CustomObject object = (CustomObject)data.read()[0];

        assertThat(object.getFullName(), is("FullName"));
        assertThat(object.getLabel(), is("ObjectLabel"));
        assertThat(object.getDescription(), is("Object Description"));

        assertThat(object.getEnableReports(), is(true));
        assertThat(object.getEnableActivities(), is(true));
        assertThat(object.getAllowInChatterGroups(), is(true));
        assertThat(object.getEnableHistory(), is(true));
        assertThat(object.getSharingModel(), is(SharingModel.ReadWrite));
        assertThat(object.getDeploymentStatus(), is(DeploymentStatus.Deployed));
        assertThat(object.getEnableSearch(), is(true));

        CustomField name = object.getNameField();

        assertThat(name.getLabel(), is("TestNameField"));
        assertThat(name.getType(), is(FieldType.AutoNumber));
        assertThat(name.getDisplayFormat(), is("Test-{00000}"));
        assertThat(name.getStartingNumber(), is(1));
    }
    
    @Test
    public void testWriteAndBaseInfo() {
        Workbook book = new XSSFWorkbook();
        final Sheet sheet = book.createSheet("オブジェクト定義");
        sheet.createRow(0).createCell(27).setCellValue("ObjApiName__c");
        sheet.createRow(1).createCell(27).setCellValue("obj label");
        
        CustomObject obj = new CustomObject();
        obj.setDescription("test description");
        obj.setEnableReports(true);
        obj.setEnableActivities(false);
        obj.setAllowInChatterGroups(true);
        obj.setEnableHistory(true);
        obj.setSharingModel(SharingModel.Private);
        obj.setDeploymentStatus(DeploymentStatus.Deployed);
        obj.setEnableSearch(false);
        CustomField name = new CustomField();
        name.setLabel("name label");
        name.setType(FieldType.Text);
        obj.setNameField(name);
        
        ObjectData data = new ObjectData(book);
        assertThat(data.getMetadataType(), is("CustomObject"));
        assertThat(data.getTargetMetadata(), is(new String[]{"ObjApiName__c"}));
        data.write(new Metadata[]{obj});
        
        assertThat(sheet.getRow(7).getCell(1).getStringCellValue(), is("test description"));
        assertThat(sheet.getRow(16).getCell(10).getStringCellValue(), is("○"));
        assertThat(sheet.getRow(17).getCell(10).getStringCellValue(), is(""));
        assertThat(sheet.getRow(18).getCell(10).getStringCellValue(), is("○"));
        assertThat(sheet.getRow(19).getCell(10).getStringCellValue(), is("○"));
        assertThat(sheet.getRow(20).getCell(10).getStringCellValue(), is("非公開"));
        assertThat(sheet.getRow(21).getCell(10).getStringCellValue(), is("○"));
        assertThat(sheet.getRow(22).getCell(10).getStringCellValue(), is(""));
        
        assertThat(sheet.getRow(27).getCell(10).getStringCellValue(), is("name label"));
        assertThat(sheet.getRow(28).getCell(10).getStringCellValue(), is("テキスト"));
        assertThat(sheet.getRow(29).getCell(10).getStringCellValue(), is(""));
        assertThat(sheet.getRow(30).getCell(10).getStringCellValue(), is(""));
    }
}
