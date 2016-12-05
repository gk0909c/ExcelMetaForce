package jp.co.tv.excelmetaforce.converter;

import java.util.Map;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.apache.commons.lang3.StringUtils;
import org.yaml.snakeyaml.Yaml;

import com.sforce.soap.metadata.DeleteConstraint;
import com.sforce.soap.metadata.DeploymentStatus;
import com.sforce.soap.metadata.EncryptedFieldMaskChar;
import com.sforce.soap.metadata.EncryptedFieldMaskType;
import com.sforce.soap.metadata.FieldType;
import com.sforce.soap.metadata.SharingModel;
import com.sforce.soap.metadata.ValueSet;

public class ExcelToMetadata {
    private final Map<String, Map<String, String>> associations;
    private static final String MARU = "○";
    
    /**
     * constructor
     */
    @SuppressWarnings("unchecked")
    public ExcelToMetadata() {
        Yaml yaml = new Yaml(); 
        associations = yaml.loadAs(ClassLoader.getSystemResourceAsStream("association.yml"), Map.class);
    }
    
    /**
     * convert sharing model for regist sfdc
     * 
     * @param val sharing model on excel
     * @return SharingModel
     */
    public SharingModel convertSharingModel(String val) {
        if (StringUtils.isEmpty(val)) {
            return SharingModel.FullAccess;
        }

        String key = getAssociationKey("sharingModel", val);
        return SharingModel.valueOf(key);
    }

    public DeploymentStatus convertDeployment(String val) {
        return MARU.equals(val) ? DeploymentStatus.Deployed : DeploymentStatus.InDevelopment;
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
    
    /**
     * ○ to setnull, ×, restrict
     * 
     * @param val value on excel
     * @return metadata value
     */
    public DeleteConstraint deleteConstraint(String val) {
        if (MARU.equals(val)) {
            return DeleteConstraint.SetNull;
        } else if ("×".equals(val)) {
            return DeleteConstraint.Restrict;
        }
        
        return null;
    }
    
    /**
     * convert mask char(* or X)
     * 
     * @param val value on excel
     * @return metadata value
     */
    public EncryptedFieldMaskChar getMaskChar(String val) {
        if ("*".equals(val)) {
            return EncryptedFieldMaskChar.asterisk;
        } else if ("X".equals(val)) {
            return EncryptedFieldMaskChar.X;
        } else {
            return null;
        }
    }
    
    /**
     * convert mask type
     * 
     * @param val value on excel
     * @return metadata value
     */
    public EncryptedFieldMaskType getMaskType(String val) {
        if (StringUtils.isEmpty(val)) {
            return null;
        }

        String key = getAssociationKey("maskType", val);
        return EncryptedFieldMaskType.valueOf(key);
    }
    
    /**
     * convert field type for regst sfdc
     * 
     * @param val field type on excel
     * @return FieldType
     */
    public FieldType convertType(String val) {
        String key = getAssociationKey("fieldType", val);
        return FieldType.valueOf(key);
    }
    
    /**
     * get arg field is numeric type
     * 
     * @param type FieldType
     * @return is numeric type
     */
    // TODO 逆方向も同じなので、まとめる。
    public boolean isNumericType(FieldType type) {
        return FieldType.Number.equals(type)
                || FieldType.Currency.equals(type)
                || FieldType.Percent.equals(type);
    }
    
    public Integer getVisibleLines(String val) {
        return StringUtils.isEmpty(val) ? 0 : Integer.parseInt(val);
    }
    
    private String getAssociationKey(String kind, String val) {
        BidiMap<String ,String> association = new DualHashBidiMap<>(associations.get(kind));
        return association.getKey(val);
    }
}
