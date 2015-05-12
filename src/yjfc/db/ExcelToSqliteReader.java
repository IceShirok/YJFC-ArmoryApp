package yjfc.db;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelToSqliteReader {
    
    public final static String DB_NUMB = "number";
    public final static String DB_SIZE = "size";
    public final static String DB_COND = "condition";
    public final static String DB_HAND = "handed";
    
    public static List<CheckoutItemPOJO> parseExcel(String filename) throws IOException {
        FileInputStream file = null;
        XSSFWorkbook workbook = null;
        List<CheckoutItemPOJO> aList = new ArrayList<>();
        
        try {
            file = new FileInputStream(new File(filename));
            workbook = new XSSFWorkbook(file);
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
            int numSheets = workbook.getNumberOfSheets();

            for(int i=0; i<numSheets; i++) {
                XSSFSheet sheet = workbook.getSheetAt(i);

                Row row = sheet.getRow(0);
                Cell cell = row.getCell(0);
                String symbol = null;
                if(cell.toString().toLowerCase().contains("symbol")) {
                    symbol = row.getCell(1).toString().trim();

                    row = sheet.getRow(1);
                    int num = -1;
                    int cond = -1;
                    int size = -1;
                    int hand = -1;
                    int temp = 0;
                    Iterator<Cell> it = row.cellIterator();
                    while(it.hasNext()) {
                        Cell c = it.next();
                        if(c == null) {
                            continue;
                        }
                        if(c.toString().toLowerCase().contains(DB_NUMB)) {
                            num = temp;
                        } else if(c.toString().toLowerCase().contains(DB_SIZE)) {
                            size = temp;
                        } else if(c.toString().toLowerCase().contains(DB_COND)) {
                            cond = temp;
                        } else if(c.toString().toLowerCase().contains(DB_HAND)) {
                            hand = temp;
                        }
                        temp++;
                    }
                    
                    int rowNum = 1;
                    Row rowTemp = sheet.getRow(rowNum);
                    while((rowTemp = sheet.getRow(++rowNum)) != null) {
                        if(rowTemp.getCell(cond).toString() != null
                                && !rowTemp.getCell(cond).toString().equals("GOOD")) {
                            continue;
                        }

                        CheckoutItemPOJO item = new CheckoutItemPOJO();
                        item.setType(symbol);

                        try {
                            String result = evaluator.evaluate(rowTemp.getCell(num)).formatAsString();
                            item.setNum((int)Double.parseDouble(result));
                        } catch(Exception e) {
                            System.out.println("parse error for num!");
                        }
                        
                        if(size >= 0) {
                            item.setSize(rowTemp.getCell(size).toString());
                        }
                        if(hand >= 0) {
                            item.setHanded(rowTemp.getCell(hand).toString());
                        }
                        aList.add(item);
                    }
                }
            }
        } finally {
        	if(file != null) {
        		file.close();
        	}
        	if(workbook != null) {
        		workbook.close();
        	}
        }
        return aList;
    }
    
}
