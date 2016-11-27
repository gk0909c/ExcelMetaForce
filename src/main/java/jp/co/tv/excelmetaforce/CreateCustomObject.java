package jp.co.tv.excelmetaforce;

import jp.co.tv.excelmetaforce.excel.ObjectData;
import jp.co.tv.excelmetaforce.runner.Creater;

public final class CreateCustomObject {
    private CreateCustomObject() {}

    public static void main(String... args) {
        new Creater(ObjectData.class).create();
    }
}
