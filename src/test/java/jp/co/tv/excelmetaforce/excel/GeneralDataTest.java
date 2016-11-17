package jp.co.tv.excelmetaforce.excel;


import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import static org.hamcrest.CoreMatchers.*; 
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class GeneralDataTest {
    private Workbook book;
    @Before
    public void setup() {
        book = new XSSFWorkbook();
        Sheet sheet = book.createSheet("表紙");
        sheet.createRow(30).createCell(12).setCellValue("TestObj__c");
        sheet.createRow(32).createCell(12).setCellValue("TestLabel");
    }

    @Test
    public void testGetApi() {
        GeneralData data = new GeneralData(book);
        assertThat(data.getObjApi(), is("TestObj__c"));
    }

    @Test
    public void testGetLabel() {
        GeneralData data = new GeneralData(book);
        assertThat(data.getObjLabel(), is("TestLabel"));
    }
}
