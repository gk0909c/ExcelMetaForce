package jp.co.tv.excelmetaforce.converter;

import java.util.Map;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.apache.commons.lang3.StringUtils;
import org.yaml.snakeyaml.Yaml;

import com.sforce.soap.metadata.DeploymentStatus;
import com.sforce.soap.metadata.FieldType;
import com.sforce.soap.metadata.SharingModel;

public class ExcelToMetadata {
    private final Map<String, Map<String, String>> associations;
    
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
        return "○".equals(val) ? DeploymentStatus.Deployed : DeploymentStatus.InDevelopment;
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
    
    private String getAssociationKey(String kind, String val) {
        BidiMap<String ,String> association = new DualHashBidiMap<>(associations.get(kind));
        return association.getKey(val);
    }
}
