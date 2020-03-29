package com.wfgp;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

public class Test {

    public static void writeFile(String fileName) throws Exception {
        System.out.println("write output file " + fileName);
        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet("sheet");

        Row header = sheet.createRow(0);
        Cell headerCell = header.createCell(0);
        headerCell.setCellValue("运单编号");

        headerCell = header.createCell(1);
        headerCell.setCellValue("开户站点");

        headerCell = header.createCell(2);
        headerCell.setCellValue("结算类型");

        headerCell = header.createCell(3);
        headerCell.setCellValue("结算金额");
        DataFormat df = workbook.createDataFormat();
        CellStyle cs = workbook.createCellStyle();
        cs.setDataFormat(df.getFormat("%"));

        Row row = sheet.createRow(1);
        Cell cell0 = row.createCell(0);
        cell0.setCellValue(0.37);
        cell0.setCellStyle(cs);
        Cell cell1 = row.createCell(1);
        cell1.setCellValue("上城六部一分部");
        Cell cell2 = row.createCell(2);
        cell2.setCellValue("中转费");

        Cell cell3 = row.createCell(3);
        cell3.setCellValue("-23.8");

        FileOutputStream outputStream = new FileOutputStream(fileName);
        workbook.write(outputStream);
        workbook.close();
    }
    public static void main(String[] args) throws Exception {
        InputStream input = new FileInputStream("D:\\wfgp_util\\customer_districtNumber.txt");
        Properties props = new Properties();
        props.load(new InputStreamReader(input, Charset.forName("gbk")));
        Enumeration<String> enums = (Enumeration<String>) props.propertyNames();
        List<Confs> datas = new ArrayList<>();
        while (enums.hasMoreElements()) {
            String key = enums.nextElement();
            String value = props.getProperty(key);
            datas.add(new Confs(key, value));
            //System.out.println(key + " : " + value);
        }

        System.out.println(datas.size());
        for (Confs conf: datas) {
            System.out.println(conf.customer + " : " + conf.number);
        }

        double f = 0.0825;
        System.out.println(f * 10.00);

        float ft = 0.0825f;
        System.out.println(ft * 10.00);

        writeFile("D:\\wfgp_util\\text.xls");
        write();

    }

    public static void write() throws Exception {
        System.out.println("Generating...");

        for (Workbook wb : new Workbook[] {new HSSFWorkbook(), new XSSFWorkbook()}) {
            Sheet sheet = wb.createSheet("Data Sheet");
            Row row = sheet.createRow(0);
            Cell cell = row.createCell(3);

            DataFormat df = wb.createDataFormat();
            CellStyle cs = wb.createCellStyle();
            cs.setDataFormat(df.getFormat("%"));
            cell.setCellValue(0.37);
            cell.setCellStyle(cs);

            String output = "D:\\wfgp_util\\text2.xls";
            if (wb instanceof XSSFWorkbook) { output += "x"; }
            FileOutputStream out = new FileOutputStream(output);
            wb.write(out);
            out.close();
        }

        System.out.println("Done");
    }
}
