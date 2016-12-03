package jp.co.tv.excelmetaforce.converter;

import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import com.sforce.soap.metadata.DeploymentStatus;
import com.sforce.soap.metadata.FieldType;
import com.sforce.soap.metadata.SharingModel;

// TODO Converterの共通部分は別クラスに以上する
public class MetadataToExcel {
    private final Map<String, Map<String, String>> associations;
    
    /**
     * constructor
     */
    @SuppressWarnings("unchecked")
    public MetadataToExcel() {
        Yaml yaml = new Yaml(); 
        associations = yaml.loadAs(ClassLoader.getSystemResourceAsStream("association.yml"), Map.class);
    }

    /**
     * convert sharing model for read sfdc
     * 
     * @param val sharing model
     * @return string
     */
    public String convertSharingModel(SharingModel val) {
        return getAssociationValue("sharingModel", val.name());
    }

    public String convertDeployment(DeploymentStatus val) {
        return DeploymentStatus.Deployed.equals(val) ? "○" : "";
    }

    /**
     * convert field type for read sfdc
     * 
     * @param val field type
     * @return field type string
     */
    public String convertType(FieldType val) {
        return getAssociationValue("fieldType", val.name());
    }

    private String getAssociationValue(String kind, String key) {
        Map<String, String> map = associations.get(kind);
        return map.get(key);
    }
}
