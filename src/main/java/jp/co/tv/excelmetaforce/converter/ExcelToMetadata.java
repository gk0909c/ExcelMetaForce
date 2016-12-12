package jp.co.tv.excelmetaforce.converter;

import org.apache.commons.lang3.StringUtils;

import com.sforce.soap.metadata.DeleteConstraint;
import com.sforce.soap.metadata.DeploymentStatus;
import com.sforce.soap.metadata.EncryptedFieldMaskChar;
import com.sforce.soap.metadata.EncryptedFieldMaskType;
import com.sforce.soap.metadata.FieldType;
import com.sforce.soap.metadata.SharingModel;
import com.sforce.soap.metadata.ValueSet;

public class ExcelToMetadata extends BaseConverter {
    public DeleteConstraint convDeleteConstraint(String val) {
        return convEnumValue(DeleteConstraint.class, DELETE_CONSTRAINT, val);
    }

    public DeploymentStatus convDeploymentStatus(String val) {
        return convEnumValue(DeploymentStatus.class, DEPLOYMENT_STATUS, val);
    }
    
    public FieldType convFieldType(String val) {
        return convEnumValue(FieldType.class, FIELD_TYPE, val);
    }

    public EncryptedFieldMaskChar convMaskChar(String val) {
        return convEnumValue(EncryptedFieldMaskChar.class, MASK_CHAR, val);
    }
    
    public EncryptedFieldMaskType convMaskType(String val) {
        return convEnumValue(EncryptedFieldMaskType.class, MASK_TYPE, val);
    }
    
    private <E extends Enum<E>> E convEnumValue(Class<E> cls, String kind, String val) {
        String key = getMappingKey(kind, val);
        return key == null ? null : Enum.valueOf(cls, key);
    }
    
    public SharingModel convSharingModel(String val) {
        return convEnumValue(SharingModel.class, SHARING_MODEL, val);
    }

    public boolean getUnique(String val) {
        return StringUtils.isEmpty(val) ? false : true;
    }

    public boolean getCaseSensitive(String val) {
        return MARU.equals(val) ? true : false;
    }
    
    /**
     * if global picklist set, return it;
     * 
     * @param val global picklist name
     * @return global picklist
     */
    public ValueSet getGlobalPick(String val) {
        if (StringUtils.isEmpty(val)) return null;
        
        ValueSet valueSet = new ValueSet();
        valueSet.setValueSetName(val);
        
        return valueSet;
    }
    
    public Integer getVisibleLines(String val) {
        return StringUtils.isEmpty(val) ? 0 : Integer.parseInt(val);
    }
}
