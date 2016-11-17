package jp.co.tv.excelmetaforce.excel;

public class CellData {
    private int row;
    private int col;
    private int colSpan;
    private Object value;
    
    public CellData(int row, int col, int colSpan) {
        this.row = row;
        this.col = col;
        this.colSpan = colSpan;
    }
    
    public void setValue(Object value) {
        this.value = value;
    }
    
}
