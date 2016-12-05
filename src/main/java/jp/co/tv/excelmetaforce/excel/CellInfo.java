package jp.co.tv.excelmetaforce.excel;

public class CellInfo {
    private int row;
    private final int col;
    private final int colSpan;
    
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
    
    public void setRow(int row) {
        this.row = row;
    }
}
