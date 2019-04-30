package com.dave.util;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.IOException;
import java.util.Iterator;

public class TestExcelReader {


    public static void main(String[] args) throws IOException {
        String XLSX_FILE_PATH = "C:\\Users\\Administrator\\Desktop\\wangpan.xlsx";

        new ExcelReader(XLSX_FILE_PATH) {
            @Override
            public void visit(Iterator<Row> rowIterator) {
                rowIterator.forEachRemaining(row ->{
                    Long id = getLong(row.getCell(0));
                    String title = getString(row.getCell(1));
                    System.out.println(id + "\t" + title);
                });
            }

            @Override
            public Sheet getSheet(Workbook workbook) {
                return workbook.getSheetAt(1);
            }

            @Override
            public void after(Workbook workbook) {

            }
        };
    }
}
