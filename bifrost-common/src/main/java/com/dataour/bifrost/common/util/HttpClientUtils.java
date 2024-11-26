package com.dataour.bifrost.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.dataour.bifrost.common.enums.RespCodeEnums;
import com.dataour.bifrost.common.module.response.Resp;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.*;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.springframework.util.CollectionUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.nio.charset.CodingErrorAction;
import java.security.KeyManagementException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Slf4j
public class HttpClientUtils {

    public final static String CODE_UTF8 = "UTF-8";
    public final static int connectTimeout = 20000;
    private static PoolingHttpClientConnectionManager connManager = null;
    private static CloseableHttpClient httpclient = null;

    static {
        try {
            SSLContext sslContext = SSLContexts.createDefault();
            sslContext.init(null, new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }}, null);
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create().register("http", PlainConnectionSocketFactory.INSTANCE).register("https", new SSLConnectionSocketFactory(sslContext)).build();
            connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
            httpclient = HttpClients.custom().setConnectionManager(connManager).build();
            // Create socket configuration
            SocketConfig socketConfig = SocketConfig.custom().setTcpNoDelay(true).build();
            connManager.setDefaultSocketConfig(socketConfig);
            // Create message constraints
            MessageConstraints messageConstraints = MessageConstraints.custom().setMaxHeaderCount(200).setMaxLineLength(2000).build();
            // Create connection configuration
            ConnectionConfig connectionConfig = ConnectionConfig.custom().setMalformedInputAction(CodingErrorAction.IGNORE).setUnmappableInputAction(CodingErrorAction.IGNORE).setCharset(Consts.UTF_8).setMessageConstraints(messageConstraints).build();
            connManager.setDefaultConnectionConfig(connectionConfig);
            connManager.setMaxTotal(200);
            connManager.setDefaultMaxPerRoute(connManager.getMaxTotal());
        } catch (KeyManagementException e) {
            log.error("KeyManagementException", e);
        }
    }

    public static Resp<String> postMap(String url, Map<String, Object> map, String encoding) {
        return post(url, JSON.toJSONString(map).replace("\\", ""), encoding);
    }

    public static Resp<String> post(String url, String params, String encoding) {
        log.info("URl:{}", url);
        log.info("Params:{}", JSONObject.toJSONString(params));
        HttpPost post = new HttpPost(url);
        try {
            post.setHeader("Content-type", "application/json");
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(connectTimeout).setConnectTimeout(connectTimeout).setConnectionRequestTimeout(connectTimeout).setExpectContinueEnabled(false).build();
            post.setConfig(requestConfig);
            post.setEntity(new StringEntity(params, encoding));
            log.info("[HttpUtils Post] begin invoke url:" + url + " , params:" + params);
            try (CloseableHttpResponse response = httpclient.execute(post)) {
                HttpEntity entity = response.getEntity();
                try {
                    if (entity != null) {
                        String str = EntityUtils.toString(entity, encoding);
                        log.info("[HttpUtils Post]Debug response, url :" + url + " , response string :" + str);
                        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                            return Resp.success(str);
                        } else {
                            return Resp.error(RespCodeEnums.BIZ_ERROR, "Error from url:" + url + " , response string :" + str);
                        }
                    }
                } finally {
                    if (entity != null) {
                        entity.getContent().close();
                    }
                }
            }
        } catch (UnsupportedEncodingException e) {
            log.error("UnsupportedEncodingException", e);
        } catch (Exception e) {
            log.error("Exception", e);
        } finally {
            post.releaseConnection();
        }
        return Resp.error(RespCodeEnums.BIZ_ERROR, "Unknown Error from url:" + url);
    }

    public static String get(String url, Map<String, Object> params, Map<String, Object> headers) {
        log.info("URl:{}", url);
        log.info("Params:{}", JSONObject.toJSONString(params));
        String responseString = null;
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(connectTimeout).setConnectTimeout(connectTimeout).setConnectionRequestTimeout(connectTimeout).build();
        URI up = buildUri(url, params);
        HttpGet get = new HttpGet(up);
        if (!CollectionUtils.isEmpty(headers)) {
            headers.forEach((k, v) -> {
                get.setHeader(k, v.toString());
            });
        }
        log.info("[HttpUtils Get] begin invoke:" + up.toString());
        get.setConfig(requestConfig);
        try {
            try (CloseableHttpResponse response = httpclient.execute(get)) {
                if (HttpStatus.SC_OK != response.getStatusLine().getStatusCode()) {
                    log.warn("[HttpClientUtil] invoke failed, response status: " + response.getStatusLine().getStatusCode());
                    return null;
                }
                HttpEntity entity = response.getEntity();
                try {
                    if (entity != null) {
                        responseString = EntityUtils.toString(entity, Consts.UTF_8);
                    }
                } finally {
                    if (entity != null) {
                        entity.getContent().close();
                    }
                }
            } catch (Exception e) {
                log.error(String.format("[HttpClientUtil Get]get response error, url:%s", up.toString()), e);
                return responseString;
            }
            log.info(String.format("[HttpClientUtil Get]Debug url:%s , response string %s:", up.toString(), responseString));
        } catch (Exception e) {
            log.error(String.format("[HttpClientUtil Get]invoke get error, url:%s", up.toString()), e);
        } finally {
            get.releaseConnection();
        }
        return responseString;
    }

    /***
     * 拼接调用的URL
     *
     * @param url 基础url
     * @param parametersMap 业务数据，作为参数
     * @return
     */
    private static URI buildUri(String url, Map parametersMap) {
        if (null == parametersMap || parametersMap.isEmpty()) {
            return URI.create(url);
        }
        List<String> list = new ArrayList<>(parametersMap.size());

        for (Object o : parametersMap.entrySet()) {
            Map.Entry entry = (Map.Entry) o;
            list.add(entry.getKey().toString().trim() + "=" + entry.getValue().toString().trim());
        }

        return list.isEmpty() ? URI.create(url) : URI.create(url + "?" + StringUtils.join(list, "&"));
    }

    /**
     * HTTPS请求
     *
     * @param reqURL
     * @param params
     * @return
     */
    public static String connectPostHttps(String reqURL, Map<String, Object> params) {

        String responseContent = null;

        HttpPost httpPost = new HttpPost(reqURL);
        try {
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(connectTimeout).setConnectTimeout(connectTimeout).setConnectionRequestTimeout(connectTimeout).build();

            List<NameValuePair> formParams = new ArrayList<>();
            httpPost.setConfig(requestConfig);
            // 绑定到请求 Entry
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                formParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));
            }
            httpPost.setEntity(new UrlEncodedFormEntity(formParams, Consts.UTF_8));
            try (CloseableHttpResponse response = httpclient.execute(httpPost)) {
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode != HttpStatus.SC_OK) {
                    log.warn("[HttpClientUtil] invoke failed, response status: " + statusCode);
                    return null;
                }
                // 执行POST请求
                HttpEntity entity = response.getEntity(); // 获取响应实体
                try {
                    if (null != entity) {
                        responseContent = EntityUtils.toString(entity, Consts.UTF_8);
                        return responseContent;
                    } else {
                        log.warn("[HttpClientUtil] invoke failed, response output is null!");
                        return null;
                    }
                } finally {
                    if (entity != null) {
                        entity.getContent().close();
                    }
                }
            }
        } catch (ClientProtocolException e) {
            log.error("[HttpClientUtil] ClientProtocolException", e);
        } catch (IOException e) {
            log.error("[HttpClientUtil] IOException", e);
        } finally {
            httpPost.releaseConnection();
        }
        return null;
    }
}

