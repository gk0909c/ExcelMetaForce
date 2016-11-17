package jp.co.tv.excelmetaforce;

import jp.co.tv.excelmetaforce.excel.ObjectData;
import jp.co.tv.excelmetaforce.runner.Reader;

public class CreateCustomObject {

    public static void main(String[] args) {
        new Reader(new ObjectData()).read();
    }
}
