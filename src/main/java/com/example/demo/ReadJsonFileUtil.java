package com.example.demo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 读取json文件，并将json转化为Map返回
 */
public class ReadJsonFileUtil {

    public static Map getMap(String path){
        Map map = new HashMap();
        try {
            //路径
            ClassPathResource classPathResource = new ClassPathResource(path);
            //读取文件信息
            String str = IOUtils.toString(new InputStreamReader(classPathResource.getInputStream(),"UTF-8"));
            //转换为Map对象
            map = JSONObject.parseObject(str, HashMap.class);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    public static void main (String[] args){
        //获取20户数据
        Map result = ReadJsonFileUtil.getMap("data.json");
        Object residentShorts = result.get("residentShorts");
        JSONArray jsonArray = JSON.parseArray(residentShorts.toString());
        for (Object o : jsonArray) {
            HashMap omap = JSONObject.parseObject(o.toString(), HashMap.class);
            System.out.println(omap);
        }
    }
}
