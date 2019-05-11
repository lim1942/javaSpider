package com.lim.download;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpHost;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.conn.util.PublicSuffixMatcher;
import org.apache.http.conn.util.PublicSuffixMatcherLoader;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.impl.cookie.DefaultCookieSpecProvider;
import org.apache.http.impl.cookie.RFC6265CookieSpecProvider;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Download {

    private HttpHost proxy;
    private JSONObject headers;
    private RequestConfig requestConfig;
    private BasicCookieStore cookieStore;
    private CloseableHttpClient httpClient;
    private CloseableHttpResponse response;
    private SSLConnectionSocketFactory sslConnectionSocketFactory;
    private Registry<CookieSpecProvider> cookieSpecProviderRegistry;


    public Download(){

        // 获取存储cookie的对象
        cookieStore = new BasicCookieStore();

        // 设置cookie策略
        PublicSuffixMatcher publicSuffixMatcher = PublicSuffixMatcherLoader.getDefault();
        cookieSpecProviderRegistry = RegistryBuilder.<CookieSpecProvider>create()
                .register(CookieSpecs.DEFAULT,
                        new DefaultCookieSpecProvider(publicSuffixMatcher))
                .register(CookieSpecs.STANDARD,
                        new RFC6265CookieSpecProvider(publicSuffixMatcher))
                .register("easy", new EasySpecProvider())
                .build();

        // 信任代理的所有证书
        SSLContextBuilder builder = new SSLContextBuilder();
        try {
            builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        try {
            sslConnectionSocketFactory = new SSLConnectionSocketFactory(
                    builder.build());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

        // 超时跳转设置
        requestConfig = RequestConfig.custom().
                setRedirectsEnabled(true).
                setConnectTimeout(30 * 1000).
                setConnectionRequestTimeout(30 * 1000).
                setSocketTimeout(30 * 1000).
                setCircularRedirectsAllowed(true).
                setMaxRedirects(100).
                setCookieSpec(CookieSpecs.STANDARD).build();

        // 获取一个httpClient对象,没代理
        httpClient = HttpClients.custom()
                .setDefaultCookieSpecRegistry(cookieSpecProviderRegistry)
                .setSSLSocketFactory(sslConnectionSocketFactory)
                .setRedirectStrategy(new LaxRedirectStrategy())
                .setDefaultRequestConfig(requestConfig)
                .setDefaultCookieStore(cookieStore)
                .build();
    }

    // 设置代理
    public void setProxy(String hostName, int proxyPort) {
        proxy = new HttpHost(hostName, proxyPort, "http");
        httpClient = HttpClients.custom().setProxy(proxy).setSSLSocketFactory(sslConnectionSocketFactory)
                .setRedirectStrategy(new LaxRedirectStrategy())
                .setDefaultRequestConfig(requestConfig)
                .setDefaultCookieSpecRegistry(cookieSpecProviderRegistry)
                .setDefaultCookieStore(cookieStore).build();
    }

    // 取消代理
    public void cancelProxy() {
        httpClient = HttpClients.custom().setSSLSocketFactory(sslConnectionSocketFactory)
                .setRedirectStrategy(new LaxRedirectStrategy())
                .setDefaultRequestConfig(requestConfig)
                .setDefaultCookieSpecRegistry(cookieSpecProviderRegistry)
                .setDefaultCookieStore(cookieStore).build();
    }

    // 设置cookie
    public void addCookies(JSONObject cookieJObject) {
        if (cookieJObject != null && !cookieJObject.isEmpty()) {
            Set<String> keySet = cookieJObject.keySet();
            for (String key : keySet) {
                String value = cookieJObject.getString(key);
                cookieStore.addCookie(new BasicClientCookie(key, value));
            }
        }
    }

    // 获取cookie
    public JSONObject getCookies() {
        Set<Cookie> cookieSet = new HashSet<>();
        List<Cookie> cookies = cookieStore.getCookies();
        JSONObject jsonObject = new JSONObject();
        for (Cookie cookie : cookies) {
            jsonObject.put(cookie.getName(), cookie.getValue());
            cookieSet.add(cookie);
        }
        return jsonObject;
    }

    // 设置请求头
    public void setHeaders(JSONObject headers){
        this.headers = headers;
    }
    private HttpRequestBase setHeaders(HttpRequestBase request) {
        Set<String> keySet = headers.keySet();
        for (String key : keySet) {
            String value = headers.getString(key);
            request.setHeader(key, value);
        }
        return request;
    }

    // 进行请求
    private PyResponse getResponse(HttpRequestBase request){
        PyResponse pyResponse = null;
        if(!(headers == null)){
            request = setHeaders(request);
        }
        try{
            response = httpClient.execute(request);
            pyResponse = new PyResponse(response);
        }catch (ClientProtocolException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
        return pyResponse;
    }

    // get
    public PyResponse get(String url){
        HttpGet httpGet = new HttpGet(url);
        return getResponse(httpGet);
    }

    // post
    public PyResponse post(String url){
        HttpPost httpPost = new HttpPost(url);
        return getResponse(httpPost);
    }

    // postData
    public PyResponse post(String url, JSONObject postBody){
        List<BasicNameValuePair> pairList = new ArrayList<>();
        HttpPost httpPost = new HttpPost(url);
        Set<String> keySet = postBody.keySet();
        for (String key : keySet) {
            pairList.add(new BasicNameValuePair(key, postBody.getString(key)));
        }
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(pairList, "utf-8"));
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }
        return getResponse(httpPost);
    }

    // postJson
    public PyResponse postJson(String url, JSONObject postBody){
        HttpPost httpPost = new HttpPost(url);
        StringEntity postEntity = new StringEntity(postBody.toString(),"utf-8");
        postEntity.setContentEncoding("UTF-8");
        httpPost.setEntity(postEntity);
        return getResponse(httpPost);
    }

}
