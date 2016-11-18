package jp.co.tv.excelmetaforce.excel;

public class CellInfo {
    public static final int TYPE_STRING = 0;
    public static final int TYPE_NUMBER = 1;
    public static final int TYPE_BOOLEAN = 2;

    private int row;
    private int col;
    private int colSpan;
    private int type;
    private Object value;
    
    public CellInfo(int row, int col, int colSpan, int type) {
        this.row = row;
        this.col = col;
        this.colSpan = colSpan;
        this.type = type;
    }
    
    public void setValue(Object value) {
        this.value = value;
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

    public boolean isStringType() {
        return type == TYPE_STRING;
    }

    public boolean isNumberType() {
        return type == TYPE_NUMBER;
    }
    
    public boolean isBooleanType() {
        return type == TYPE_BOOLEAN;
    }
    
}
