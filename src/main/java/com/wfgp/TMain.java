package com.wfgp;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TMain {

    static {
        try {
            File file = new File(Constants.appDir + "log\\logging.properties");
            if (!file.exists())
                Constants.writeLogProperties();
        } catch (Exception e) {
        }
        System.setProperty("java.util.logging.config.file",
                Constants.appDir + "log\\logging.properties");
    }
    private static Logger logger = LoggerFactory.getLogger(TMain.class);

    public static List<ExcelData> summaryDatas = new ArrayList<>();
    public static Map<String, List<CustomerExpressData>> customerDatas = new HashMap<>();
    public static Map<String, String> customers;
    public static String templates = "templates.xlsx";

    public static Map<String, Result> customersResult = new HashMap<>();
    //public static List<Confs> confs = new ArrayList<>();

    public static List<ExcelData> parseFile(final String dir, File[] files) throws Exception {
        for (File file : files) {
            logger.info("parse : " + file.getAbsoluteFile());
            CSVParser parser = null;
            parser = CSVParser.parse(file, Charset.forName("gbk"), CSVFormat.EXCEL);
            getRequiredDatas(parser);
        }
        return summaryDatas;
    }

    public static String getCellData(CSVRecord csvRecord, int idx){
        String data = "";
        try {
            data = csvRecord.get(idx);
            data = data.trim();
        } catch (ArrayIndexOutOfBoundsException e){
            logger.error("no csv record at cell " + idx);
        }
        return data;
    }

    public static List<ExcelData> getRequiredDatas(CSVParser parser){
        summaryDatas.clear();
        customerDatas.clear();
        if (parser == null) {
            logger.error("no CSVParser instance ...");
            return summaryDatas;
        }

        for (CSVRecord csvRecord : parser) {
            final String customerContent = getCellData(csvRecord, 1);
            final String trackNumContent = getCellData(csvRecord, 4);
            final String offContent = getCellData(csvRecord, 10);
            final String addr = getAddr(customerContent);

            if (addr.isEmpty() || trackNumContent.isEmpty()) {
                continue;
            }
            summaryDatas.add(new ExcelData(trackNumContent, addr, offContent));

            final String dateContent = getCellData(csvRecord, 0);
            final String senderContent = getCellData(csvRecord, 3);
            final String destContent = getCellData(csvRecord, 6);
            final String countContent = getCellData(csvRecord, 8);
            final String weightContent = getCellData(csvRecord, 9);

            List<CustomerExpressData> cusData = customerDatas.get(customerContent);
            if (cusData == null){
                cusData = new ArrayList<>();
                customerDatas.put(customerContent, cusData);
            }

            cusData.add(new CustomerExpressData(dateContent, trackNumContent, countContent,
                    senderContent, destContent, weightContent, offContent));
        }
        System.out.println(summaryDatas.size());
        return summaryDatas;
    }

    public static boolean deleteDir(File folderOrFile) {
        boolean ret = false;
        if (folderOrFile.isDirectory()) {
            File[] listOfFiles = folderOrFile.listFiles();
            for(File file : listOfFiles) {
                ret = deleteDir(file);
            }
        }
        ret = folderOrFile.delete();
        return ret;
    }

    public static void setCustomers() throws IOException {
        PropertiesReader reader = new PropertiesReader(Constants.appDir);
        customers = reader.getCusomters();

        for (String key: customers.keySet()) {
            File checkFile = new File(Constants.output_data + key);
            deleteDir(checkFile);
            checkFile.mkdir();
        }

        File checkFile = new File(Constants.output_data + "total");
        deleteDir(checkFile);
        checkFile.mkdir();
    }

    public static String getAddr(String customer) {
        for (String key: customers.keySet()) {
            if (customer.indexOf(key) != -1) {
                return customers.get(key);
            }
        }

        return "";
    }

    public static String getCustomerName(String customer) {
        for (String key: customers.keySet()) {
            if (customer.indexOf(key) != -1) {
                return key;
            }
        }

        return "";
    }

    public static void writeFile(String fileName, List<ExcelData> datas) throws Exception {
        logger.info("write output file " + fileName);
        Workbook workbook = new XSSFWorkbook();
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

        int rows = 0;
        for (ExcelData data: datas) {
            rows++;
            Row row = sheet.createRow(rows);
            Cell cell0 = row.createCell(0);
            cell0.setCellValue(data.trackNumber);
            Cell cell1 = row.createCell(1);
            cell1.setCellValue(data.addr);
            Cell cell2 = row.createCell(2);
            cell2.setCellValue("中转费");

            Cell cell3 = row.createCell(3);
            cell3.setCellValue("-" + data.amount);
        }
        FileOutputStream outputStream = new FileOutputStream(fileName);
        workbook.write(outputStream);
        workbook.close();
    }

    public static CellStyle createStyle(Sheet sheet, int rowIdx) {
        final Font font = sheet.getWorkbook ().createFont ();
        if (rowIdx == 0) {
            font.setFontName("楷体_GB2312");
            font.setBold(true);
            font.setFontHeightInPoints((short) 22);
        } else if (rowIdx == 1) {
            font.setFontName ("宋体");
            font.setFontHeightInPoints((short) 11);
        }
        final CellStyle style = sheet.getWorkbook ().createCellStyle ();
        style.setFont ( font );
        return style;
    }

    public static String getDistrictNumber(final String customer)
    {
        return "";
    }

    public static List<String> getSheetTitles(final String customer, final String beginDateStr,
                                              final String endDateStr) {
        //final String districtNum = getDistrictNumber(customer);
        List<String> mergedC = new ArrayList<>();
        mergedC.add("百世快递-杭州上城六部 ── 业务结算清单");

        // (致:汪焰良（老杭派）-承包区[590007] 日期:2018.11.02-2018.11.02(未付款))
        mergedC.add("(致:" + customer + " 日期:" + beginDateStr +
                "-" + endDateStr  + "(未付款))");
        mergedC.add("");
        return mergedC;
    }

    public static  List<String> getHeaders() {
        List<String> hs = new ArrayList<>();
        hs.add("序号"); hs.add("发件日期"); hs.add("运单号码");
        hs.add("件数"); hs.add("发件人"); hs.add("目的地");
        hs.add("重量"); hs.add("运费");
        return hs;
    }

    public static  void getRowData(final CustomerExpressData data,
                                   Result result, Result singleV) {
        int count = 0;
        try {
            count = Integer.valueOf(data.count);
        } catch (NumberFormatException e){}

        double weight = 0;
        try {
            weight = Double.valueOf(data.weight);
        } catch (NumberFormatException e){}

        double amount = 0;
        try {
            amount  = Double.valueOf(data.freight);
        } catch (NumberFormatException e){}

        result.cout += count;
        result.setWeight(result.getWeight().add(BigDecimal.valueOf(weight)));
        result.setAmount(result.getAmount().add(BigDecimal.valueOf(amount)));
        singleV.cout = count;
        singleV.setWeight(BigDecimal.valueOf(weight));
        singleV.setAmount(BigDecimal.valueOf(amount));
    }

    public static String copyFile(final String cuNameForDir, final String customer) throws Exception {
        final String fileName = customer.replaceAll(
                "[/<>:\"|?*]", "_");
        //System.out.println(customer + ":" + customer.length());
        String dstFile = Constants.output_data + cuNameForDir +"\\" + fileName + ".xlsx";

        File original = new File(Constants.templateDir + templates);

        Path copied = Paths.get(dstFile);
        Path originalPath = original.toPath();
        Files.copy(originalPath, copied, StandardCopyOption.REPLACE_EXISTING);
        return dstFile;
    }

    public static Cell createCellIfAbsent(Row row, int idx) {
        Cell cell = row.getCell(idx);
        if (cell == null) {
            cell = row.createCell(idx);
        }
        return cell;
    }

    public static Row createRowIfAbsent(Sheet sheet, int idx) {
        Row row = sheet.getRow(idx);
        if (row == null) {
            row = sheet.createRow(idx);
        }
        return row;
    }

    public static void writeItem(List<CustomerExpressData> datas, final String customer) throws Exception {
        if(datas == null || datas.isEmpty()) {
            System.err.println("no data to write out");
            return;
        }
        String cuNameForDir = getCustomerName(customer);
        String fileName = copyFile(cuNameForDir, customer);
        InputStream inp = new FileInputStream(fileName);

        Workbook wb = WorkbookFactory.create(inp);
        Sheet sheet = wb.getSheet("LIST");

        {
            List<String> mergedC = getSheetTitles(customer, datas.get(0).dateStr,
                    datas.get(datas.size()-1).dateStr);
            Row row = sheet.getRow(1);
            Cell cell = createCellIfAbsent(row, 1);
            cell.setCellValue(mergedC.get(1));
            //cell.setCellStyle(createStyle(sheet, 1));
        }

        int rowidx = 0;
        Result result = new Result();
        Result singleV = new Result();
        for(;rowidx < datas.size(); rowidx++){
            final CustomerExpressData data = datas.get(rowidx);
            getRowData(datas.get(rowidx), result, singleV);
            Row row = createRowIfAbsent(sheet,rowidx +4);
            Cell cellO = createCellIfAbsent(row,0);
            cellO.setCellValue(rowidx+1);

            cellO = createCellIfAbsent(row,1);
            cellO.setCellValue(data.dateStr);

            cellO = createCellIfAbsent(row,2);
            cellO.setCellValue(data.trackNumber);

            cellO = createCellIfAbsent(row,3);
            cellO.setCellValue(singleV.cout);

            cellO = createCellIfAbsent(row,4);
            cellO.setCellValue(data.sender);

            cellO = createCellIfAbsent(row,5);
            cellO.setCellValue(data.dest);

            cellO = createCellIfAbsent(row,6);
            cellO.setCellValue(singleV.weight.setScale(2, RoundingMode.UP).doubleValue());

            cellO = createCellIfAbsent(row,7);
            cellO.setCellValue(singleV.amount.setScale(2, RoundingMode.UP).doubleValue());
        }
        Row row = createRowIfAbsent(sheet,rowidx +4);
        Cell headerCell = createCellIfAbsent(row,0);
        headerCell.setCellValue("合计");

        headerCell = createCellIfAbsent(row,3);
        headerCell.setCellValue(result.cout);

        headerCell = createCellIfAbsent(row,6);
        headerCell.setCellValue(result.weight.setScale(2, RoundingMode.UP).toString());

        headerCell = createCellIfAbsent(row,7);
        headerCell.setCellValue(result.amount.setScale(2, RoundingMode.UP).toString());

        if (cuNameForDir.isEmpty()) {
            System.err.println(customer + " directory is empty");
        }

        Result resFromMap = customersResult.get(cuNameForDir);
        if (resFromMap == null){
            resFromMap = new Result();
            customersResult.put(cuNameForDir, resFromMap);
        }
        resFromMap.setWeight(resFromMap.getWeight().add(result.weight));
        resFromMap.cout += result.cout;
        resFromMap.setAmount(resFromMap.getAmount().add(result.amount));

        //System.out.println(customer + ":" + customer.length());
        FileOutputStream fileOut = new FileOutputStream(fileName);
        wb.write(fileOut);
        fileOut.close();
    }

    public static void writeTotal() throws Exception {
        FileOutputStream fileOut = new FileOutputStream(Constants.output_data + "total\\customers.txt");
        Writer w = new OutputStreamWriter(fileOut, "gbk");
        BufferedWriter bw = new BufferedWriter(w);
        bw.write("客户， 件数， 重量， 运费\r\n");
        for (String key: customersResult.keySet()) {
            Result r = customersResult.get(key);
            bw.write(key + "," + r.cout + ", " +
                    r.weight.setScale(2, RoundingMode.UP).toString() + ", " +
                    r.amount.setScale(2, RoundingMode.UP).toString() + "\r\n");
        }
        bw.close();
    }

    public static boolean isTrackNumber(final String content){
        return true;
    }

    public static void main(String[] args) throws Exception {
        setCustomers();

        long sTime = System.currentTimeMillis();
        File folder = new File(Constants.input_data);
        File[] listOfFiles = folder.listFiles();
        parseFile(Constants.appDir, listOfFiles);
        writeFile(Constants.output_data + "total\\output.xlsx", summaryDatas);

        for (String key : customerDatas.keySet()) {
            writeItem(customerDatas.get(key), key);
        }
        writeTotal();

        System.out.println("spend time : " + ((System.currentTimeMillis() - sTime)/1000) + " seconds");
    }
}

