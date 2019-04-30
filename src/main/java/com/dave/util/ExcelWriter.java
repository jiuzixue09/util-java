package com.dave.util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;


public abstract class ExcelWriter {
    private String saveAs;

    // Create a DataFormatter to format and get each cell's value as String
    private DataFormatter dataFormatter = new DataFormatter();

    public ExcelWriter(String saveAs) throws IOException {
        this.saveAs = saveAs;

        process();
    }

    public abstract void addRows(Workbook workbook);

    public void process() throws IOException {
        // Obtain a workbook from the excel file
        Workbook workbook = new XSSFWorkbook();

        addRows(workbook);

        // Write the output to the file
        FileOutputStream fileOut = new FileOutputStream(saveAs);
        workbook.write(fileOut);
        fileOut.close();

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
