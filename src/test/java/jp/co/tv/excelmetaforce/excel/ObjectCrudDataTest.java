package jp.co.tv.excelmetaforce.excel;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Before;
import org.junit.Test;

import com.sforce.soap.metadata.Metadata;
import com.sforce.soap.metadata.Profile;
import com.sforce.soap.metadata.ProfileObjectPermissions;

import jp.co.tv.excelmetaforce.sfdc.Connector;

public class ObjectCrudDataTest {
    private static final String OBJ_NAME = "CrudObj__c";
    private Workbook book;
    private Sheet crudSheet;

    /**
     * create book instance, sheet instance.
     * and write object api name.
     */
    @Before
    public void setup() {
        book = new XSSFWorkbook();
        crudSheet = book.createSheet(ObjectCrudData.SHEET_NAME);
        crudSheet.createRow(0).createCell(27).setCellValue(OBJ_NAME);
    }
    
    @Test
    public void readObjectCrudSheet() {
        // prepare
        writeCrudRow(1, "TestProfile1", "○", "", "○", "", "○", "");
        writeCrudRow(2, "TestProfile2", "", "○", "", "○", "", "○");

        ObjectCrudData data = new ObjectCrudData(book);
        final Profile[] profiles = (Profile[])data.read();

        // assert
        assertThat(profiles.length, is(2));
        assertCrud(profiles[0], "TestProfile1", true, false, true, false, true, false);
        assertCrud(profiles[1], "TestProfile2", false, true, false, true, false, true);
    }
    
    @Test
    public void writeObjectCrudSheet() {
        // prepare
        Profile profile1 = createProfile("TestProfile3", true, false, true, false, true, false);
        Profile profile2 = createProfile("TestProfile4", false, true, false, true, false, true);

        ObjectCrudData data = new ObjectCrudData(book);
        data.write(new Metadata[]{profile1, profile2});

        assertCrudSheet(1, "TestProfile3", "○", "", "○", "", "○", "");
        assertCrudSheet(2, "TestProfile4", "", "○", "", "○", "", "○");
    }
    
    @Test
    public void testGetTargetMetadata() {
        // Connector mock
        Profile profile1 = new Profile();
        profile1.setFullName("TestProfile5");
        Profile profile2 = new Profile();
        profile2.setFullName("TestProfile6");
        Connector mock = mock(Connector.class);
        when(mock.readMetadata(anyString(), any())).thenReturn(new Metadata[]{profile1, profile2});

        ObjectCrudData data = new ObjectCrudData(book);
        data.conn = mock;
        assertThat(data.getTargetMetadata(), is(new Metadata[]{profile1, profile2}));
    }
    
    private void writeCrudRow(int rowNum, String profileName, String... cruds) {
        Row row = crudSheet.createRow(rowNum + 5);
        row.createCell(0).setCellValue("a");
        row.createCell(3).setCellValue(profileName);
        row.createCell(10).setCellValue(cruds[0]);
        row.createCell(14).setCellValue(cruds[1]);
        row.createCell(18).setCellValue(cruds[2]);
        row.createCell(22).setCellValue(cruds[3]);
        row.createCell(26).setCellValue(cruds[4]);
        row.createCell(30).setCellValue(cruds[5]);
    }
    
    private void assertCrud(Profile profile, String profileName, boolean... cruds) {
        ProfileObjectPermissions crud = profile.getObjectPermissions()[0];
        assertThat(profile.getFullName(), is(profileName));
        assertThat(crud.getAllowRead(), is(cruds[0]));
        assertThat(crud.getAllowCreate(), is(cruds[1]));
        assertThat(crud.getAllowEdit(), is(cruds[2]));
        assertThat(crud.getAllowDelete(), is(cruds[3]));
        assertThat(crud.getViewAllRecords(), is(cruds[4]));
        assertThat(crud.getModifyAllRecords(), is(cruds[5]));
    }
    
    private Profile createProfile(String profilelName, boolean... cruds) {
        ProfileObjectPermissions permissions = new ProfileObjectPermissions();
        permissions.setObject(OBJ_NAME);
        permissions.setAllowRead(cruds[0]);
        permissions.setAllowCreate(cruds[1]);
        permissions.setAllowEdit(cruds[2]);
        permissions.setAllowDelete(cruds[3]);
        permissions.setViewAllRecords(cruds[4]);
        permissions.setModifyAllRecords(cruds[5]);
        
        Profile profile = new Profile();
        profile.setFullName(profilelName);
        profile.setObjectPermissions(new ProfileObjectPermissions[]{permissions});
        
        return profile;
    }
    
    private void assertCrudSheet(int rowNum, String profileName, String... cruds) {
        Row row = crudSheet.getRow(rowNum + 5); 
        
        assertThat(row.getCell(3).getStringCellValue(), is(profileName));
        assertThat(row.getCell(10).getStringCellValue(), is(cruds[0]));
        assertThat(row.getCell(14).getStringCellValue(), is(cruds[1]));
        assertThat(row.getCell(18).getStringCellValue(), is(cruds[2]));
        assertThat(row.getCell(22).getStringCellValue(), is(cruds[3]));
        assertThat(row.getCell(26).getStringCellValue(), is(cruds[4]));
        assertThat(row.getCell(30).getStringCellValue(), is(cruds[5]));
    }
}
