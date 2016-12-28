package jp.co.tv.excelmetaforce.excel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sforce.soap.metadata.Metadata;
import com.sforce.soap.metadata.Profile;
import com.sforce.soap.metadata.ProfileFieldLevelSecurity;

import jp.co.tv.excelmetaforce.converter.ExcelToMetadata;
import jp.co.tv.excelmetaforce.converter.MetadataToExcel;

public class FieldPermissionData extends SheetData {
    public static final String SHEET_NAME = "項目アクセス許可";
    private static final int START_ROW = 7;
    private static final int START_COL = 10;
    private static final int COL_SPAN = 7;
    private static final Logger LOGGER = LoggerFactory.getLogger(FieldPermissionData.class);
    
    private final CellInfo objectFullName = new CellInfo(0, 27, 0, true);

    private final CellInfo isTarget = new CellInfo(0, 0, 0);
    private final CellInfo rowNo = new CellInfo(0, 1, 2);
    private final CellInfo fieldName = new CellInfo(0, 3, 7);
    private final List<ProfileCol> profileCols;

    /**
     * init by profile setting
     * 
     * @param book workbook
     */
    public FieldPermissionData(Workbook book) {
        super(book, SHEET_NAME);
        
        profileCols = new ArrayList<ProfileCol>();
        for (int i = START_COL; true; i = i + COL_SPAN) {
            ProfileCol profileCol = new ProfileCol(i);
            
            if (profileCol.validCol()) {
                profileCols.add(profileCol);
            } else {
                break;
            }
        }
    }

    @Override
    public Metadata[] read() {
        int targetRow = START_ROW;
        final String objApi = excel.getStringValue(objectFullName);
        
        while (!excel.isEmpty(targetRow, fieldName.getCol())) {
            if (excel.isEmpty(targetRow, isTarget.getCol())) {
                targetRow++;
                continue;
            }
            updateRow(targetRow);
            
            final int  tmpTargetRow = targetRow;
            String fieldNameStr = excel.getStringValue(fieldName);
            profileCols.forEach(pCol -> {
                updateRow(tmpTargetRow, pCol);
                pCol.readPermission(String.format("%s.%s", objApi, fieldNameStr));
            });
            targetRow++;
        }
        
        return profileCols.stream().map(pCol -> pCol.getProfile()).toArray(Profile[]::new);
    }

    @Override
    public void write(Metadata... profiles) {
        final String objApi = excel.getStringValue(objectFullName);
        
        // create fieldName -> permission map per profile
        for (Metadata target : profiles) {
            final Profile profile = (Profile)target;
            
            ProfileFieldLevelSecurity[] securities = Stream.of(profile.getFieldPermissions())
                    .filter(security -> security.getField().startsWith(objApi))
                    .toArray(ProfileFieldLevelSecurity[]::new);
            
            profileCols.stream()
                .filter(pCols -> profile.getFullName().equals(pCols.getProfileName()))
                .collect(Collectors.toList()).get(0)
                .createFieldSecurityMap(securities);;
        }

        // write each field permission per profile
        Set<String> fieldSet = profileCols.get(0).getSecurityMap().keySet();
        fieldSet = new TreeSet<String>(fieldSet);

        int targetRow = START_ROW;
        int headerRowRange = START_ROW - 1;

        for (String field : fieldSet) {
            updateRow(targetRow);
            final int  tmpTargetRow = targetRow;

            excel.setValue(rowNo, targetRow - headerRowRange);
            excel.setValue(fieldName, field.split("\\.")[1]);
            profileCols.forEach(pCol -> {
                updateRow(tmpTargetRow, pCol);
                pCol.writePermission(field);   
            });

            targetRow++;
        }
    }

    @Override
    public Metadata[] getTargetMetadata() {
        String[] targetProfiles = profileCols.stream()
                .map(pCol -> pCol.getProfileName())
                .toArray(String[]::new);
        LOGGER.info(String.format("get Profiles: %s", StringUtils.join(targetProfiles, ",")));
        
        return conn.readMetadata("Profile", targetProfiles);
    }
    
    /**
     * profile column expression class
     */
    private class ProfileCol {
        private final int col;
        private final CellInfo fullName;
        private final CellInfo permission;
        private final ExcelToMetadata converterE2M;
        private final MetadataToExcel converterM2E;
        private final List<ProfileFieldLevelSecurity> permissions;
        private Map<String, String> securityMap;
        
        public ProfileCol(int col) {
            this.col = col;

            fullName = new CellInfo(START_ROW - 1, col, 0, true);
            permission = new CellInfo(0, col, COL_SPAN, CellInfo.Dropdown.MARU_SANKAKU);
            
            converterE2M = new ExcelToMetadata();
            converterM2E = new MetadataToExcel();
            permissions = new ArrayList<ProfileFieldLevelSecurity>();
        }
        
        public boolean validCol() {
            return !excel.isEmpty(START_ROW - 1, col);
        }
        
        public String getProfileName() {
            return excel.getStringValue(fullName);
        }

        public void readPermission(String fieldName) {
            ProfileFieldLevelSecurity security = new ProfileFieldLevelSecurity();
            security.setField(fieldName);
            String permissionStr = excel.getStringValue(permission);
            security.setEditable(converterE2M.getEditable(permissionStr));
            security.setReadable(converterE2M.getReadable(permissionStr));
            
            permissions.add(security);
        }
        
        public Profile getProfile() {
            Profile profile = new Profile();
            profile.setFullName(getProfileName());
            profile.setFieldPermissions(permissions.toArray(new ProfileFieldLevelSecurity[]{}));
            return profile;
        }
        
        public void createFieldSecurityMap(ProfileFieldLevelSecurity... securities) {
            securityMap = Stream.of(securities)
                .collect(Collectors.toMap(
                    ProfileFieldLevelSecurity::getField,
                    security -> converterM2E.getFieldPermission(security)));
        }
        
        public Map<String, String> getSecurityMap() {
            return securityMap;
        }
        
        public void writePermission(String field) {
            excel.setValue(permission, securityMap.get(field));
        }
    }
}
