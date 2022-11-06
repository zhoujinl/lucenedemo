package lucenedemo.utils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import lucenedemo.entity.CommonEntity;
import java.lang.reflect.*;

import lucenedemo.dao.IBaseDao;

public class ExcelUtil {

    private static final String XLS = "xls";
    private static final String XLSX = "xlsx";

    public static CellStyle creatCellStyle(SXSSFWorkbook workbook, boolean head) {
        CellStyle style = workbook.createCellStyle();
        //对齐方式设置
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBottomBorderColor(IndexedColors.BLACK.getIndex()); // 下边框
        style.setBorderLeft(BorderStyle.THIN);
        style.setLeftBorderColor(IndexedColors.BLACK.getIndex()); // 左边框
        style.setBorderRight(BorderStyle.THIN);
        style.setRightBorderColor(IndexedColors.BLACK.getIndex()); // 右边框
        style.setBorderTop(BorderStyle.THIN);
        style.setTopBorderColor(IndexedColors.BLACK.getIndex()); // 上边框
        style.setWrapText(true);// 设置自动换行
        if (head) {
            //设置背景颜色
            style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            //粗体字设置
            Font font = workbook.createFont();
            font.setBold(true);
            style.setFont(font);
        }
        return style;
    }

    public static <E> void exportExcel(CommonEntity data, HttpServletResponse response, IBaseDao dao, String[] headList,
                                       Map<String, String> headAndDescribeMap) {
        SXSSFWorkbook workbook = new SXSSFWorkbook(100);

        // 获取总个数
        Integer totalCount = dao.likeSelectCount(data).intValue();

        if (totalCount == 0) {
            ExcelUtil.export(response, workbook);
            return;
        }

        int totalPage = 0;

        int sheetCount = 0;
        // 分页查询 查询数据库的次数
        if (totalCount % 5000 == 0) {
            totalPage = totalCount / 5000;
        } else {
            totalPage = totalCount / 5000 + 1;
        }

        // sheet页的个数
        if (totalCount % 50000 == 0) {
            sheetCount = totalCount / 50000;
        } else {
            sheetCount = totalCount / 50000 + 1;
        }

        int k = 0;
        // 记录当前sql的开始页码
        int m = 0;
        // 记录总条数
        int n = 0;

        CellStyle headCellStyle = ExcelUtil.creatCellStyle(workbook, true);
        CellStyle bodyCellStyle = ExcelUtil.creatCellStyle(workbook, false);

        for (int i = 0; i < sheetCount; i++) {

            Sheet sh = workbook.createSheet("sheet" + String.valueOf(i + 1));

            Row row = sh.createRow(0);
            // 设置表头
            for (int headIndex = 0; headIndex < headList.length; headIndex++) {
                sh.setColumnWidth(headIndex, 6000);
                Cell cell = row.createCell(headIndex);
                cell.setCellStyle(headCellStyle);
                cell.setCellValue(headList[headIndex]);
            }

            for (; m < totalPage; m++) {
                if ((k + 1) * 5000 > 50000) {
                    // 重置k 退出当前循环
                    k = 0;
                    m--;
                    break;
                }
                // 查询数据库的数据
                // sql中的start
                data.setStart(m * 5000);
                // 5000条
                data.setPageSize(5000);

                List<E> resultList = dao.likeSelect(data);

                // 设置内容
                for (int resultIndex = 0; resultIndex < resultList.size(); resultIndex++) {

                    E entity = resultList.get(resultIndex);
                    Row currentRow = sh.createRow(n + 1);

                    int headIndex = 0;
                    // 创建每个单元格
                    for (Map.Entry<String, String> headAndDescribe : headAndDescribeMap.entrySet()) {
                        Cell cell = currentRow.createCell(headIndex);
                        cell.setCellStyle(bodyCellStyle);
                        String currentValue = "";
                        String describe;
                        if (entity == null) {
                            currentValue = "无";
                            describe = "";
                        } else {
                            String headName = headAndDescribe.getKey();
                            try {
                                Field declaredField = entity.getClass().getDeclaredField(headName);
                                declaredField.setAccessible(true);
                                currentValue = String.valueOf(declaredField.get(entity));
                            } catch (Exception e) {
                                e.printStackTrace();
                                throw new RuntimeException(e);
                            }
                            describe = headAndDescribe.getValue();
                        }

                        // 必然是状态码
                        if (describe.contains("#") && describe.contains("&")) {

                            String[] arr = describe.split("#");

                            Map<String, String> arrMap = new HashMap<>();

                            for (String value : arr) {

                                String[] statusArr = value.split("&");
                                // 数据库中的值当作key，显示在Excel中的值当作value
                                arrMap.put(statusArr[1], statusArr[0]);
                            }
                            cell.setCellValue(arrMap.get(currentValue));
                            // 布尔类型
                        } else if (describe.contains("#")) {

                            String[] arr = describe.split("#");
                            Map<String, String> arrMap = new HashMap<>();
                            // 数据库中的值当作key，显示在Excel中的值当作value
                            arrMap.put(arr[0], "是");
                            arrMap.put(arr[1], "否");
                            cell.setCellValue(arrMap.get(currentValue));
                        } else {
                            cell.setCellValue(currentValue);
                        }
                        headIndex++;
                    }
                    n++;
                }

                k++;
            }
        }

        export(response, workbook);
    }
    


    public static void export(HttpServletResponse response, SXSSFWorkbook workbook) {
        BufferedOutputStream out = null;
        String filename = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "Datas.xlsx";
        // 设置文件名
        try {
            response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode(filename, "UTF-8"));
            out = new BufferedOutputStream(response.getOutputStream());
            workbook.write(out);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                // 处理在磁盘上备份此工作簿的临时文件
                workbook.close();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 根据文件后缀名类型获取对应的工作簿对象
     *
     * @param inputStream 读取文件的输入流
     * @param fileType    文件后缀名类型（xls或xlsx）
     * @return 包含文件数据的工作簿对象
     * @throws IOException
     */
    public static Workbook getWorkbook(InputStream inputStream, String fileType) throws IOException {
        Workbook workbook = null;
        if (fileType.equalsIgnoreCase(XLS)) {
            workbook = new HSSFWorkbook(inputStream);
        } else if (fileType.equalsIgnoreCase(XLSX)) {
            workbook = new XSSFWorkbook(inputStream);
        }
        return workbook;
    }


    /**
     * 读取Excel文件内容 每一行内容使用Map封装
     *
     * @param fileName 要读取的Excel文件所在路径
     * @return 读取结果列表，读取失败时返回null
     */
    public static List<Map<String, String>> readExcelToRowMap(String fileName) {

        // 读取excel中的数据
        Map<String, String> map = new LinkedHashMap<>();
        List<Map<String, String>> resultDataList = parseExcel(readExcel(fileName), map);

        return resultDataList;

    }

    public static List<Map<String, String>> readExcelToRowMap(File file) {

        // 读取excel中的数据
        Map<String, String> map = new LinkedHashMap<>();
        List<Map<String, String>> resultDataList = parseExcel(readExcel(file), map);

        return resultDataList;

    }

    /**
     * 读取Excel文件内容 每一行内容使用List封装
     *
     * @param fileName 要读取的Excel文件所在路径
     * @return 读取结果列表，读取失败时返回null
     */
    public static List<List<String>> readExcelToRowList(String fileName) {

        // 读取excel中的数据
        List<String> list = new ArrayList<>();
        List<List<String>> resultDataList = parseExcel(readExcel(fileName), list);

        return resultDataList;

    }

    public static List<List<String>> readExcelToRowList(File file) {

        // 读取excel中的数据
        List<String> list = new ArrayList<>();
        List<List<String>> resultDataList = parseExcel(readExcel(file), list);

        return resultDataList;

    }

    /**
     * 读取excel获得workbook
     *
     * @param e
     * @return
     */
    private static <E> Workbook readExcel(E e) {

        Workbook workbook = null;
        FileInputStream inputStream = null;

        try {

            String fileName = "";
            String fileType = "";
            File excelFile = null;
            if (e instanceof String) {
                // 获取Excel后缀名
                fileName = (String) e;
                fileType = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
                // 获取Excel文件
                excelFile = new File(fileName);
                if (!excelFile.exists()) {
                    return null;
                }
                inputStream = new FileInputStream(excelFile);
                workbook = getWorkbook(inputStream, fileType);
            } else if (e instanceof File) {
                fileName = ((File) e).getName();
                fileType = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
                // 获取Excel工作簿
                inputStream = new FileInputStream((File) e);
                workbook = getWorkbook(inputStream, fileType);
            }
            return workbook;
        } catch (Exception e1) {
            e1.printStackTrace();
            return null;
        } finally {
            try {
                if (null != workbook) {
                    workbook.close();
                }
                if (null != inputStream) {
                    inputStream.close();
                }
            } catch (Exception e2) {
                return null;
            }
        }
    }


    /**
     * 解析Excel数据
     *
     * @param workbook Excel工作簿对象
     * @return 解析结果
     */
    private static <E> List<E> parseExcel(Workbook workbook, E e) {

        if (workbook == null) {
            return null;
        }

        List<E> resultDataList = new ArrayList<>();
        // 解析sheet
        for (int sheetNum = 0; sheetNum < workbook.getNumberOfSheets(); sheetNum++) {
            Sheet sheet = workbook.getSheetAt(sheetNum);
            // 校验sheet是否合法
            if (sheet == null) {
                continue;
            }
            // 获取第一行数据
            int firstRowNum = sheet.getFirstRowNum();
            Row firstRow = sheet.getRow(firstRowNum);
            if (null == firstRow) {
                System.out.println(sheet.getSheetName() + "无数据");
                continue;
            }

            //获取head头部
            Row headRow = sheet.getRow(firstRowNum);
            //获取列数
            int numberOfCells = headRow.getPhysicalNumberOfCells();

            List<String> headList = new ArrayList<>();
            for (int i = 0; i < numberOfCells; i++) {
                Cell cell = headRow.getCell(i);
                String head = convertCellValueToString(cell);
                headList.add(head);
            }
            // 解析每一行的数据，构造数据对象
            int rowStart = firstRowNum + 1;

            int rowEnd = sheet.getPhysicalNumberOfRows();
            for (int rowNum = rowStart; rowNum < rowEnd; rowNum++) {
                Row row = sheet.getRow(rowNum);

                if (null == row) {
                    continue;
                }

                E resultData = null;
                if (e instanceof Map) {
                    resultData = (E) convertRowToDataMap(row, headList);
                } else {
                    resultData = (E) convertRowToDataList(row, headList);
                }

                if (null == resultData) {
                    continue;
                }
                resultDataList.add(resultData);
            }

        }

        return resultDataList;
    }


    /**
     * 将单元格内容转换为字符串
     *
     * @param cell
     * @return
     */

    private static String convertCellValueToString(Cell cell) {
        if (cell == null) {
            return "";
        }
        String returnValue = "";
        switch (cell.getCellType()) {
            case NUMERIC:   //数字
                if (DateUtil.isCellDateFormatted(cell)) {
                    Date date = cell.getDateCellValue();
                    DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// HH:mm:ss cellValue = df2.format(d);
                    returnValue = format.format(date);
                } else {
                    Double doubleValue = cell.getNumericCellValue();
                    // 格式化科学计数法，取一位整数
                    DecimalFormat df = new DecimalFormat("0");
                    returnValue = df.format(doubleValue);
                }
                break;
            case STRING:    //字符串
                returnValue = cell.getStringCellValue();
                break;
            case BOOLEAN:   //布尔
                Boolean booleanValue = cell.getBooleanCellValue();
                returnValue = booleanValue.toString();
                break;
            case BLANK:     // 空值
                break;
            case FORMULA:   // 公式
                returnValue = cell.getCellFormula();
                break;
            case ERROR:     // 故障
                break;
            default:
                break;
        }
        return returnValue;
    }

    /**
     * 提取每一行中需要的数据到map中
     *
     * @param row 行数据
     * @return 解析后的行数据对象
     */
    private static Map<String, String> convertRowToDataMap(Row row, List<String> headList) {

        Map<String, String> resultData = new LinkedHashMap<>();

        for (int i = 0; i < headList.size(); i++) {
            Cell cell = row.getCell(i);
            String data = convertCellValueToString(cell);
            resultData.put(headList.get(i), data);
        }

        return resultData;

    }

    /**
     * 提取每一行中需要的数据到List中
     *
     * @param row 行数据
     * @return 解析后的行数据对象
     */
    private static List<String> convertRowToDataList(Row row, List<String> headList) {

        List<String> resultData = new ArrayList<>();


        for (int i = 0; i < headList.size(); i++) {
            Cell cell = row.getCell(i);
            String data = convertCellValueToString(cell);
            resultData.add(data);
        }

        return resultData;

    }

}
