package com.example.demo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

public class Demo {
    public static void main(String[] args) {

        //excel文件路径
        String excelPath = "C:\\Users\\86598\\Desktop\\新建 XLS 工作表.xls";

        try {
            File excel = new File(excelPath);
            if (excel.isFile() && excel.exists()) {   //判断文件是否存在

                String[] split = excel.getName().split("\\.");  //.是特殊字符，需要转义！！！！！
                Workbook wb;
                //根据文件后缀（xls/xlsx）进行判断
                if ("xls".equals(split[1])) {
                    FileInputStream fis = new FileInputStream(excel);   //文件流对象
                    wb = new HSSFWorkbook(fis);
                } else if ("xlsx".equals(split[1])) {
                    wb = new XSSFWorkbook(excel);
                } else {
                    System.out.println("文件类型错误!");
                    return;
                }


                //开始解析
                Sheet sheet = wb.getSheetAt(0);     //读取sheet 0

                //获取20户数据
                Map result = ReadJsonFileUtil.getMap("data.json");
                Object residentShorts = result.get("residentShorts");
                JSONArray jsonArray = JSON.parseArray(residentShorts.toString());
                for (Object o : jsonArray) {
                    HashMap omap = JSONObject.parseObject(o.toString(), HashMap.class);
                    String status =omap.get("adminStatus").toString();
                    if (status.equals("3")) {
                        continue;
                    }
                    String residentNo = (String) omap.get("residentNo");
                    //遍历行
                    Row row = sheet.getRow(Integer.parseInt(residentNo)-1);
                    if (row != null) {
                        int firstCellIndex = row.getFirstCellNum();
                        int lastCellIndex = row.getLastCellNum();
                        for (int cIndex = firstCellIndex; cIndex < lastCellIndex; cIndex++) {   //遍历列
                            Cell cell = row.getCell(cIndex);
                            if (cIndex == 0) {
                                omap.put("d1Name", cell.toString());
                            }
                            if (cIndex == 2) {
                                omap.put("d2HouseholderRel", cell.toString().substring(0, 1));

                            }
                            if (cIndex == 3) {
                                omap.put("d3IdCardNo", cell.toString().trim());

                            }
                            if (cIndex == 4) {
                                String s = "";
                                if (cell.toString().trim().equals("男")) {
                                    s = "1";
                                } else if (cell.toString().trim().equals("女")) {
                                    s = "2";
                                }
                                omap.put("d4Sex", s);
                            }
                            if (cIndex == 5) {
                                String[] strs = cell.toString().trim().split("-");
                                omap.put("d5BirthYear", strs[0].trim());
                                omap.put("d5BirthMonth", strs[1].trim());
                            }
                            if (cIndex == 6) {
                                if (cell.toString().trim().equals("汉族")) {
                                    omap.put("d6EthnicGroup", "01");
                                }
                            }
                            if (cIndex == 8) {
                                omap.put("d7LiveLocation", "4");
                                omap.put("d7LiveLocationCode", "-1");
                                omap.put("d7LiveProvince", "-1");
                                omap.put("d7LiveCity", "-1");
                                omap.put("d7LiveCounty", "-1");
                            }
                            if (cIndex == 9) {
                                omap.put("d8RegisterLocation", "1");
                                omap.put("d8RegisterLocationCode", "-1");
                                omap.put("d8RegisterProvince", "-1");
                                omap.put("d8RegisterCity", "-1");
                                omap.put("d8RegisterCounty", "-1");

                            }
                            if (cIndex == 10) {
                                omap.put("d9LeaveDate", cell.toString().trim().substring(0, 1));

                            }
                            if (cIndex == 11) {
                                omap.put("d10LeaveReason", cell.toString().trim().substring(0, 1));

                            }
                        }
                        omap.put("d11EducationLevel", "7");
                        omap.put("d12Literacy", "1");


//                        datamap.put("adCode", "420106012002");
//                        datamap.put("orgCode", "420106012000");
//                        datamap.put("censusSubdistrictId", "1301361950236733448");
//                        datamap.put("censusSubdistrictCode", "420106012002061");
//                        datamap.put("province", "42");
//                        datamap.put("city", "01");
//                        datamap.put("county", "06");
//                        datamap.put("town", "012");

//                        datamap.put("buildingId", "1301362369298034765");
//                        datamap.put("residenceUnitId", "1318048311780380674_1603875328940006");
//                        datamap.put("householdId", "1318048311780380674_1604042846436129");
//                        datamap.put("householdCode", "420106012002061023");
//                        datamap.put("instructorId", "1306183682760847362");
//                        datamap.put("investigatorId", "1306183682760847362");
//                        datamap.put("householdStatus", "1");
//                        datamap.put("isSampled", "0");

//                        datamap.put("processLocation", "420106012002061 水果湖街道北环路社区居委会第061普查小区");


                        omap.put("age", String.valueOf(2020 - Integer.parseInt(String.valueOf(omap.get("d5BirthYear")))));

                        omap.put("adminStatus", "3");
//                        datamap.put("objectStatus", "1");
//                        datamap.put("verifyStatus", "1");
//                        datamap.put("ownerId", "1306183682760847362");

//                        datamap.put("creatorId", "1306183682760847362");
//                        datamap.put("creatorName", "朱佩佩");
//                        datamap.put("updaterId", "1306183682760847362");

//                        datamap.put("updaterName", "朱佩佩");
//                        datamap.put("gatherSource", "0");
                        omap.put("entireAdminStatus", "2");
                        omap.put("provinceName", "湖北省");
                        omap.put("cityName", "武汉市");
                        omap.put("countyName", "武昌区");
                        omap.put("townName", "水果湖街道");

                        omap.put("villageName", "北环路社区居委会");
                        omap.put("buildingNo", "0210");
                        omap.put("residenceUnitNo", "3");

                        omap.put("instructorName", "朱佩佩");
                        omap.put("investigatorName", "朱佩佩");
                        omap.put("userId", "1306183682760847362");

                        System.out.println(omap);

                        String httpsUrl = "https://cj.rkpc.stats.gov.cn/cjv1/api/7rp-prod-gather/miniapp-formal/residentShort/update";

                        Map<String, String> header = new HashMap<>();
                        header.put("User-Agent:", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.143 Safari/537.36 MicroMessenger/7.0.9.501 NetType/WIFI MiniProgramEnv/Windows WindowsWechat");
                        header.put("token", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJ3ZWIiLCJpc3MiOiI3cnAtbWluaWFwcCIsImV4cCI6MTYwNDg3NzIxNCwidXNlcklkIjoiMTMwNjE4MzY4Mjc2MDg0NzM2MiIsImlhdCI6MTYwNDg0MTIxNCwianRpIjoiZTg4YTRiZTVlZjZjNDBmNDk5YzA5YzBkZDIyYzQ5NWUiLCJ1c2VybmFtZSI6IuacseS9qeS9qSJ9.gKYDXrjV-B-212EHLIM3pLdxxWsIHz9a4_EgVoy2QaI");
                        header.put("content-type", "application/x-www-form-urlencoded");

//                        HashMap<String, String> paramMap = (HashMap<String, String>) omap.clone();
                        String response = HttpClientUtils.doPostRequest(httpsUrl, header, omap, null);
                        System.out.println(response);
                    }
                }
            } else {
                System.out.println("找不到指定的文件");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Map<String, String> mapStringToMap(String str) {
        str = str.substring(1, str.length() - 1);
        String[] strs = str.split(",");
        Map<String, String> map = new HashMap<String, String>();
        for (String string : strs) {
            String[] sss = string.split("=");
            if (sss.length < 2) {
                continue;
            }
            String key = string.split("=")[0];
            String value = string.split("=")[1];
            map.put(key, value);
        }
        return map;
    }
}
