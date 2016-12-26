package jp.co.tv.excelmetaforce.excel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sforce.soap.metadata.Metadata;
import com.sforce.soap.metadata.Profile;
import com.sforce.soap.metadata.ProfileObjectPermissions;

public class ObjectCrudData extends SheetData {
    public static final String SHEET_NAME = "CRUD権限";
    private static final int START_ROW = 6;
    private static final Logger LOGGER = LoggerFactory.getLogger(ObjectCrudData.class);
    
    private final CellInfo objectFullName = new CellInfo(0, 27, 0, true);

    private final CellInfo isTarget = new CellInfo(0, 0, 0);
    private final CellInfo rowNo = new CellInfo(0, 1, 2);
    private final CellInfo fullName = new CellInfo(0, 3, 7);
    private final CellInfo readCell = new CellInfo(0, 10, 4);
    private final CellInfo create = new CellInfo(0, 14, 4);
    private final CellInfo edit = new CellInfo(0, 18, 4);
    private final CellInfo delete = new CellInfo(0, 22, 4);
    private final CellInfo allRead = new CellInfo(0, 26, 4);
    private final CellInfo allModify = new CellInfo(0, 30, 4);


    public ObjectCrudData(Workbook book) {
        super(book, SHEET_NAME);
    }

    @Override
    public Metadata[] read() {
        int targetRow = START_ROW;
        List<Profile> profiles = new ArrayList<Profile>();
        
        while (!excel.isEmpty(targetRow, fullName.getCol())) {
            if (excel.isEmpty(targetRow, isTarget.getCol())) {
                targetRow++;
                continue;
            }
            updateRow(targetRow);

            ProfileObjectPermissions objectPermission = new ProfileObjectPermissions();
            objectPermission.setAllowRead(excel.getBooleanValue(readCell));
            objectPermission.setAllowCreate(excel.getBooleanValue(create));
            objectPermission.setAllowEdit(excel.getBooleanValue(edit));
            objectPermission.setAllowDelete(excel.getBooleanValue(delete));
            objectPermission.setViewAllRecords(excel.getBooleanValue(allRead));
            objectPermission.setModifyAllRecords(excel.getBooleanValue(allModify));
            
            Profile profile = new Profile();
            profile.setFullName(excel.getStringValue(fullName));
            profile.setObjectPermissions(new ProfileObjectPermissions[]{objectPermission});

            targetRow++;
            profiles.add(profile);
        }
        
        return profiles.toArray(new Profile[]{});
    }

    @Override
    public void write(Metadata... profiles) {
        int targetRow = START_ROW;
        int headerRowRange = START_ROW - 1;

        for (Metadata target : profiles) {
            Profile profile = (Profile)target;
            final ProfileObjectPermissions objectPermission = getObjectPermission(profile);
            updateRow(targetRow);
            
            excel.setValue(rowNo, targetRow - headerRowRange);
            excel.setValue(fullName, profile.getFullName());
            excel.setValue(readCell, objectPermission.getAllowRead());
            excel.setValue(create, objectPermission.getAllowCreate());
            excel.setValue(edit, objectPermission.getAllowEdit());
            excel.setValue(delete, objectPermission.getAllowDelete());
            excel.setValue(allRead, objectPermission.getViewAllRecords());
            excel.setValue(allModify, objectPermission.getModifyAllRecords());

            targetRow++;
        }
    }

    @Override
    public Metadata[] getTargetMetadata() {
        List<String> targets = new ArrayList<String>();
        int targetRow = START_ROW;
        
        while (!excel.isEmpty(targetRow, fullName.getCol())) {
            updateRow(targetRow);
            targets.add(excel.getStringValue(fullName));
            targetRow++;
        }
        String[] targetProfiles = targets.toArray(new String[]{});
        LOGGER.info(String.format("get Profiles: %s", StringUtils.join(targets, ",")));
        
        
        return conn.readMetadata("Profile", targetProfiles);
    }
    
    private ProfileObjectPermissions getObjectPermission(Profile profile) {
        String objApi = excel.getStringValue(objectFullName);
        ProfileObjectPermissions[] objectPermissions = profile.getObjectPermissions();
        
        return Arrays.asList(objectPermissions).stream()
                .filter(permission -> permission.getObject().equals(objApi))
                .collect(Collectors.toList()).get(0);
    }
}
