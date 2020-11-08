package com.example.demo;

import org.apache.commons.collections4.MapUtils;
import org.apache.http.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/*
 * Http/Https请求的工具类
 * */
public class HttpClientUtils {
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(HttpClientUtils.class);
    static StringBuffer cookielocal = new StringBuffer();//保存cookie值 ，使得在新建httpClient时保持会话
    //static CloseableHttpClient httpClient = null;  可以使用同一个client规避不同client需要添加cookie
/*
    接下来在登陆的的请求中调用 setCookieStore(HttpResponse httpResponse)方法，保存会话cookie；
    如果后面出现新的httpclient时，给请求添加请求头，例如：
    httpGet.setHeader("cookie",cookielocal.substring(0,cookielocal.length()-1).toString());
*/

    public static String doPostRequest(String url, Map<String, String> header, Map<String, String> params, HttpEntity httpEntity) {
        String resultStr = "";
        if (StringUtils.isEmpty(url)) {
            return resultStr;
        }
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse httpResponse = null;
        try {
            httpClient = SSLClientCustom.getHttpClient();
            HttpPost httpPost = new HttpPost(url);
            //请求头header信息
            if (MapUtils.isNotEmpty(header)) {
                for (Map.Entry<String, String> stringStringEntry : header.entrySet()) {
                    httpPost.setHeader(stringStringEntry.getKey(), stringStringEntry.getValue());
                }
            }
            //请求参数信息
            if (MapUtils.isNotEmpty(params)) {
                List<NameValuePair> pairList = new ArrayList<>();
                for (Map.Entry<String, String> stringStringEntry : params.entrySet()) {
                    pairList.add(new BasicNameValuePair((String) stringStringEntry.getKey(),  String.valueOf(stringStringEntry.getValue())));
                }
                UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(pairList, Consts.UTF_8);
                httpPost.setEntity(urlEncodedFormEntity);
            }
            //实体设置
            if (httpEntity != null) {
                httpPost.setEntity(httpEntity);
            }
            //发起请求
            httpResponse = httpClient.execute(httpPost);
            setCookieStore(httpResponse);
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                HttpEntity httpResponseEntity = httpResponse.getEntity();
                resultStr = EntityUtils.toString(httpResponseEntity);
                logger.info("请求正常，请求地址：{},响应结果:{}", url, resultStr);
            } else {
                StringBuffer stringBuffer = new StringBuffer();
                HeaderIterator headerIterator = httpResponse.headerIterator();
                while (headerIterator.hasNext()) {
                    stringBuffer.append("\t" + headerIterator.next());
                }
                logger.info("异常信息:请求地址:{},响应状态:{},请求返回结果:{}", url, httpResponse.getStatusLine().getStatusCode(), stringBuffer);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            HttpClientUtils.closeConnection(httpClient, httpResponse);
        }
        return resultStr;
    }

    public static String doGetRequest(String url, Map<String, String> stringStringMap, Map<String, String> header, Map<String, String> params) {
        String resultStr = "";
        if (StringUtils.isEmpty(url)) {
            return resultStr;
        }
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse httpResponse = null;
        try {
            httpClient = SSLClientCustom.getHttpClient();
            //请求参数信息
            if (MapUtils.isNotEmpty(params)) {
                url = url + buildUrl(params);
            }
            HttpGet httpGet = new HttpGet(url);
            System.err.println("获取的cookie值：" + cookielocal.substring(0, cookielocal.length() - 1).toString());
            httpGet.setHeader("cookie", cookielocal.substring(0, cookielocal.length() - 1).toString());//设定cookie信息在post请求中已经过去
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(50000)//连接超时
                    .setConnectionRequestTimeout(50000)//请求超时
                    .setSocketTimeout(50000)//套接字连接超时
                    .setRedirectsEnabled(true).build();//允许重定向
            httpGet.setConfig(requestConfig);
            if (MapUtils.isNotEmpty(header)) {
                for (Map.Entry<String, String> stringStringEntry : header.entrySet()) {
                    httpGet.setHeader(stringStringEntry.getKey(), stringStringEntry.getValue());
                }
            }
            //发起请求
            httpResponse = httpClient.execute(httpGet);
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                resultStr = EntityUtils.toString(httpResponse.getEntity(), Consts.UTF_8);
                logger.info("请求地址:{},响应结果:{}", url, resultStr);
            } else {
                StringBuffer stringBuffer = new StringBuffer();
                HeaderIterator headerIterator = httpResponse.headerIterator();
                while (headerIterator.hasNext()) {
                    stringBuffer.append("\t" + headerIterator.next());
                }
                logger.info("异常信息:请求响应状态:{},请求返回结果:{}", httpResponse.getStatusLine().getStatusCode(), stringBuffer);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            HttpClientUtils.closeConnection(httpClient, httpResponse);
        }
        return resultStr;
    }

    /**
     * 关掉连接释放资源
     */
    private static void closeConnection(CloseableHttpClient httpClient, CloseableHttpResponse httpResponse) {
        if (httpClient != null) {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (httpResponse != null) {
            try {
                httpResponse.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 构造get请求的参数
     *
     * @return
     */
    private static String buildUrl(Map<String, String> map) {
        if (MapUtils.isEmpty(map)) {
            return "";
        }
        StringBuffer stringBuffer = new StringBuffer("?");
        for (Map.Entry<String, String> stringStringEntry : map.entrySet()) {
            stringBuffer.append(stringStringEntry.getKey()).append("=").append(stringStringEntry.getValue()).append("&");
        }
        String result = stringBuffer.toString();
        if (!StringUtils.isEmpty(result)) {
            result = result.substring(0, result.length() - 1);//去掉结尾的&连接符
        }
        return result;
    }

    public static void setCookieStore(HttpResponse httpResponse) {

        Header[] headers = httpResponse.getHeaders("Set-Cookie");
        for (Header header : headers) {
            HeaderElement[] headerElementArray = header.getElements();
            for (HeaderElement headerElement : headerElementArray) {

                if (null != headerElement.getValue()) {
                    // 获取cookie并保存
                    cookielocal.append(headerElement.getName() + "=" + headerElement.getValue() + ";");
                }

            }
        }
    }
}