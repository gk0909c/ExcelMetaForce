package jp.co.tv.excelmetaforce.converter;

import org.apache.commons.lang3.StringUtils;

import com.sforce.soap.metadata.DeleteConstraint;
import com.sforce.soap.metadata.DeploymentStatus;
import com.sforce.soap.metadata.EncryptedFieldMaskChar;
import com.sforce.soap.metadata.EncryptedFieldMaskType;
import com.sforce.soap.metadata.FieldType;
import com.sforce.soap.metadata.SharingModel;

public class MetadataToExcel extends BaseConverter {

    /**
     * if null, return empty. others, depend on mapping.
     * 
     * @param val delete constraint
     * @return value on excel
     */
    public String convDeleteConstraint(DeleteConstraint val) {
        if (val == null) return StringUtils.EMPTY;
        return getMappingValue(DELETE_CONSTRAINT, val.name());
    }

    public String convDeploymentStatus(DeploymentStatus val) {
        return getMappingValue(DEPLOYMENT_STATUS, val.name());
    }

    public String convFieldType(FieldType val) {
        return getMappingValue(FIELD_TYPE, val.name());
    }

    public String convMaskChar(EncryptedFieldMaskChar val) {
        return getMappingValue(MASK_CHAR, val.name());
    }

    public String convMaskType(EncryptedFieldMaskType val) {
        return getMappingValue(MASK_TYPE, val.name());
    }

    public String convSharingModel(SharingModel val) {
        return getMappingValue(SHARING_MODEL, val.name());
    }

    
    /**
     * ○：unique && caseSensitive, △：unique, "": other
     * 
     * @param unique unique
     * @param caseSensitive case sensitive
     * @return converted
     */
    public String getUnique(boolean unique, boolean caseSensitive) {
        if (unique && caseSensitive) {
            return MARU;
        } else if (unique) {
            return SANKAKU;
        } else {
            return StringUtils.EMPTY;
        }
    }
}
