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
import com.sforce.soap.metadata.SharingModel;

public class ObjectDataTest {
    private Workbook book;

    /**
     * Create excel data
     */
    @Before
    public void setup() {
        book = new XSSFWorkbook();
        Sheet objectSheet = book.createSheet("オブジェクト定義");
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
        objectSheet.createRow(30).createCell(10).setCellValue(1);
    }
    
    @Test
    public void testRead() {
        ObjectData data = new ObjectData(book);
        data.generalData = mock(GeneralData.class);

        CustomObject object = (CustomObject)data.read()[0];

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
}
