package jp.co.tv.excelmetaforce.converter;

import java.util.Map;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.apache.commons.lang3.StringUtils;
import org.yaml.snakeyaml.Yaml;

import com.sforce.soap.metadata.FieldType;

public class BaseConverter {
    protected static final String MARU = "○";
    protected static final String SANKAKU = "△";

    protected static final String DELETE_CONSTRAINT = "deleteConstraint";
    protected static final String DEPLOYMENT_STATUS = "deploymentStatus";
    protected static final String FIELD_TYPE = "fieldType";
    protected static final String MASK_CHAR = "maskChar";
    protected static final String MASK_TYPE = "maskType";
    protected static final String SHARING_MODEL = "sharingModel";
    
    private final Map<String, Map<String, String>> mapping;

    @SuppressWarnings("unchecked")
    public BaseConverter() {
        Yaml yaml = new Yaml(); 
        mapping = yaml.loadAs(ClassLoader.getSystemResourceAsStream("mapping.yml"), Map.class);
    }

    protected String getMappingValue(String kind, String key) {
        Map<String, String> map = mapping.get(kind);
        return map.get(key);
    }
    
    /**
     * if val is empty, return null
     * 
     * @param kind mapping type
     * @param val mapped value for key
     * @return mapped key
     */
    protected String getMappingKey(String kind, String val) {
        if (StringUtils.isEmpty(val)) return null;
        
        BidiMap<String ,String> association = new DualHashBidiMap<>(mapping.get(kind));
        return association.getKey(val);
    }

    /**
     * check passed type need length setting.
     * 
     * @param type target 
     * @return is numeric
     */
    public boolean needLengthType(FieldType type) {
        return FieldType.Text.equals(type)
                || FieldType.Number.equals(type)
                || FieldType.Currency.equals(type)
                || FieldType.Percent.equals(type);
    }

    /**
     * check passed type is numeric type.
     * 
     * @param type target 
     * @return is numeric
     */
    public boolean isNumericType(FieldType type) {
        return FieldType.Number.equals(type)
                || FieldType.Currency.equals(type)
                || FieldType.Percent.equals(type);
    }
}
