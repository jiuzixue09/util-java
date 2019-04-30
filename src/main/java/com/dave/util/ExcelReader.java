package com.dave.util;

import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;


public abstract class ExcelReader {
    private String filePath;



    // Create a DataFormatter to format and get each cell's value as String
    private DataFormatter dataFormatter = new DataFormatter();

    public ExcelReader(String filePath) throws IOException {
        this.filePath = filePath;

        process();
    }

    public abstract void visit(Iterator<Row> rowIterator);

    public abstract Sheet getSheet(Workbook workbook);

    public abstract void after(Workbook workbook);


    public void process() throws IOException {
        // Creating a Workbook from an Excel file (.xls or .xlsx)
        Workbook workbook = WorkbookFactory.create( new File(filePath));
        // Retrieving the number of sheets in the Workbook
        System.out.println("Workbook has " + workbook.getNumberOfSheets() + " Sheets : ");

        Sheet sheet = getSheet(workbook);

        // obtain a rowIterator and columnIterator and iterate over them
        System.out.println("\n\nIterating over Rows and Columns using Iterator\n");
        Iterator<Row> rowIterator = sheet.rowIterator();

        visit(rowIterator);

        after(workbook);
        // Closing the workbook
        workbook.close();
    }


    public String getString(Cell cell){
        return dataFormatter.formatCellValue(cell);
    }
    public Boolean getBoolean(Cell cell){ return cell.getBooleanCellValue(); }
    public Date getDate(Cell cell){ return cell.getDateCellValue();  }
    public Double getDouble(Cell cell){ return cell.getNumericCellValue(); }
    public Long getLong(Cell cell){ return getDouble(cell).longValue();}



}
