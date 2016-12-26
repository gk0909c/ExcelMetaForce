package jp.co.tv.excelmetaforce.excel;

public class CellInfo {
    private int row;
    private final int col;
    private final int colSpan;
    /** if list sheet, header cell is not incrementable */
    private final boolean header;
    
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
    }
    
    /**
     * init 
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
}
