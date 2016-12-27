package jp.co.tv.excelmetaforce.excel;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Before;
import org.junit.Test;

import com.sforce.soap.metadata.Metadata;
import com.sforce.soap.metadata.Profile;
import com.sforce.soap.metadata.ProfileFieldLevelSecurity;

import jp.co.tv.excelmetaforce.sfdc.Connector;

public class FieldPermissionDataTest {
    private static final String OBJ_NAME = "FieldPermissionObj__c";
    private static final int START_ROW = 7;
    private Workbook book;
    private Sheet permissionSheet;

    /**
     * create book instance, sheet instance.
     * and write object api name.
     */
    @Before
    public void setup() {
        book = new XSSFWorkbook();
        permissionSheet = book.createSheet(FieldPermissionData.SHEET_NAME);
        permissionSheet.createRow(0).createCell(27).setCellValue(OBJ_NAME);
    }

    @Test
    public void readProfilePermissions() {
        // prepare
        writeProfiles("Profile1", "Profile2", "Profile3");
        writeFieldPermission(1, true,  "Field1__c", "○", "△",    "");
        writeFieldPermission(2, true,  "Field2__c",    "", "○", "△");
        writeFieldPermission(3, false, "Field9__c", "○", "○", "○");
        writeFieldPermission(4, true,  "Field3__c", "△",    "", "○");
        
        FieldPermissionData data = new FieldPermissionData(book);
        Profile[] profiles = (Profile[])data.read();
        
        // assert
        assertThat(profiles.length, is(3));
        assertPermission(profiles[0], "Profile1", 3,
                "Field1__c", true, true, "Field2__c", false, false, "Field3__c", false, true);
        assertPermission(profiles[1], "Profile2", 3,
                "Field1__c", false, true, "Field2__c", true, true, "Field3__c", false, false);
        assertPermission(profiles[2], "Profile3", 3,
                "Field1__c", false, false, "Field2__c", false, true, "Field3__c", true, true);
    }
    
    @Test
    public void writeProfilePermissions() {
        // prepare
        writeProfiles("Profile4", "Profile5", "Profile6");
        final Profile profile1 = createProfileFieldSecurity("Profile4",
                "Field4__c", true, true, "Field5__c", false, true, "Field6__c", false, false);
        final Profile profile2 = createProfileFieldSecurity("Profile5",
                "Field4__c", false, false, "Field5__c", true, true, "Field6__c", false, true);
        final Profile profile3 = createProfileFieldSecurity("Profile6",
                "Field4__c", false, true, "Field5__c", false, false, "Field6__c", true, true);
        
        FieldPermissionData data = new FieldPermissionData(book);
        data.write(new Metadata[]{profile1, profile2, profile3});
        
        assertPermissionSheet(1, "Field4__c", "○",    "", "△");
        assertPermissionSheet(2, "Field5__c", "△", "○",    "");
        assertPermissionSheet(3, "Field6__c",    "", "△", "○");
    }
    
    @Test
    public void targetMetadataReturnsProfiles() {
        // prepare(not effective, but info log print these profiles)
        writeProfiles("Profile4", "Profile5", "Profile6");
        
        // mock
        Profile profile1 = new Profile();
        profile1.setFullName("TestProfile4");
        Profile profile2 = new Profile();
        profile2.setFullName("TestProfile5");
        Profile profile3 = new Profile();
        profile3.setFullName("TestProfile6");
        Connector mock = mock(Connector.class);
        when(mock.readMetadata(anyString(), any())).thenReturn(new Metadata[]{profile1, profile2, profile3});
        
        // assert
        FieldPermissionData data = new FieldPermissionData(book);
        data.conn = mock;
        assertThat(data.getTargetMetadata(), is(new Metadata[]{profile1, profile2, profile3}));
    }
    
    private void assertPermissionSheet(int rowNum, String fieldName, String... permissions) {
        Row row = permissionSheet.getRow(rowNum + 6);
        
        assertThat(row.getCell(3).getStringCellValue(), is(fieldName));
        assertThat(row.getCell(10).getStringCellValue(), is(permissions[0]));
        assertThat(row.getCell(17).getStringCellValue(), is(permissions[1]));
        assertThat(row.getCell(24).getStringCellValue(), is(permissions[2]));
    }
    
    private Profile createProfileFieldSecurity(String profileName, Object... securities) {
        List<ProfileFieldLevelSecurity> secList = new ArrayList<ProfileFieldLevelSecurity>();
        
        for (int securityIdx = 0; securityIdx < securities.length; securityIdx += 3) {
            ProfileFieldLevelSecurity security = new ProfileFieldLevelSecurity();
            security.setField(
                    String.format("%s.%s", OBJ_NAME, (String)securities[securityIdx]));
            security.setEditable((boolean)securities[securityIdx + 1]);
            security.setReadable((boolean)securities[securityIdx + 2]);
            secList.add(security);
        }
        
        Profile profile = new Profile();
        profile.setFullName(profileName);
        profile.setFieldPermissions(secList.toArray(new ProfileFieldLevelSecurity[]{}));
        
        return profile;
    }
    
    private void assertPermission(Profile profile, String profileName, int securityNum, Object... permissions) {
        assertThat(profile.getFullName(), is(profileName));
        
        ProfileFieldLevelSecurity[] securities = profile.getFieldPermissions();
        
        assertThat(String.format("%s field security count", profileName),
                securities.length, is(securityNum));
        
        for (int securityIdx = 0,
                permissionIdx = 0; securityIdx < securities.length; securityIdx++) {
            ProfileFieldLevelSecurity security = securities[securityIdx];
            
            assertThat(String.format("%s - %s - field name", profileName, security.getField()),
                    securities[securityIdx].getField(),
                    is(String.format("%s.%s", OBJ_NAME, permissions[permissionIdx])));
            assertThat(String.format("%s - %s - editable", profileName, security.getField()),
                    securities[securityIdx].getEditable(), is(permissions[permissionIdx + 1]));
            assertThat(String.format("%s - %s - readable", profileName, security.getField()),
                    securities[securityIdx].getReadable(), is(permissions[permissionIdx + 2]));
            
            permissionIdx = permissionIdx + 3;
        }
        
    }
    
    private void writeProfiles(String... profiles) {
        Row row = permissionSheet.createRow(6);

        for (int i = 0; i < profiles.length; i++) {
            row.createCell(getProfileCol(i)).setCellValue(profiles[i]);
        }
    }
    
    private void writeFieldPermission(int rowNum, boolean target, String fieldName, String... permissions) {
        Row row = permissionSheet.createRow(rowNum - 1 + START_ROW);
        row.createCell(0).setCellValue(target ? "a" : "");
        row.createCell(3).setCellValue(fieldName);
        
        for (int i = 0; i < permissions.length; i++) {
            row.createCell(getProfileCol(i)).setCellValue(permissions[i]);
        }
    }

    private int getProfileCol(int idx) {
        return idx * 7 + 10;
    }
}
