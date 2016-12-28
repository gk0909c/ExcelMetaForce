package jp.co.tv.excelmetaforce.excel;

import java.util.Map;
import java.util.function.Function;

import org.yaml.snakeyaml.Yaml;

@SuppressWarnings("unchecked")
public class CellInfo {
    public enum Dropdown {
        MARU_SANKAKU,
        MARU,
        FIELD_TYPE,
        DELETE_CONSTRAINT,
        MASK_CHAR,
        MASK_TYPE,
        NONE
    }
    
    private int row;
    private final int col;
    private final int colSpan;
    /** if list sheet, header cell is not incrementable */
    private final boolean header;
    private final Dropdown dropdown;
    private static final Map<String, Map<String, String>> MAPPING;
    
    /**
     * init 
     * 
     * @param row excel row 
     * @param col excel col
     * @param colSpan merge col span
     */
    public CellInfo(int row, int col, int colSpan) {
        this.row = row;
        this.col = col;
        this.colSpan = colSpan;
        this.header = false;
        this.dropdown = Dropdown.NONE;
    }
    
    /**
     * init with this is header cell or not
     * 
     * @param row excel row 
     * @param col excel col
     * @param colSpan merge col span
     * @param header is this cell is header cell
     */
    public CellInfo(int row, int col, int colSpan, boolean header) {
        this.row = row;
        this.col = col;
        this.colSpan = colSpan;
        this.header = header;
        this.dropdown = Dropdown.NONE;
    }
    
    /**
     * init with dropdown type
     * 
     * @param row excel row 
     * @param col excel col
     * @param colSpan merge col span
     * @param dropdown specify input dropdown
     */
    public CellInfo(int row, int col, int colSpan, Dropdown dropdown) {
        this.row = row;
        this.col = col;
        this.colSpan = colSpan;
        this.header = false;
        this.dropdown = dropdown;
    }
    
    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public int getColSpan() {
        return colSpan;
    }
    
    public boolean isHeader() {
        return header;
    }
    
    public void setRow(int row) {
        this.row = row;
    }

    /**
     * get dropdown list. if not dropdown list, return null.
     * 
     * @return dropdown list as string array
     */
    public String[] getDropdown() {
        Function<Map<String, String>, String[]> mapConverter = map -> {
            return map.values().toArray(new String[]{});
        };
        
        switch (dropdown) {
        case MARU_SANKAKU: return new String[]{"○", "△"};
        case MARU: return new String[]{"○"};
        case FIELD_TYPE: return mapConverter.apply(MAPPING.get("fieldType"));
        case DELETE_CONSTRAINT: return mapConverter.apply(MAPPING.get("deleteConstraint"));
        case MASK_CHAR: return mapConverter.apply(MAPPING.get("maskChar"));
        case MASK_TYPE: return mapConverter.apply(MAPPING.get("maskType"));
        default: return null;
        }
    }

    static {
        Yaml yaml = new Yaml(); 
        MAPPING = yaml.loadAs(ClassLoader.getSystemResourceAsStream("mapping.yml"), Map.class);
    }
}
