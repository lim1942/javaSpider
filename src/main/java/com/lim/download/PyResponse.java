package com.lim.download;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import java.io.IOException;

public class PyResponse {

    private CloseableHttpResponse response;
    private String text = null;


    PyResponse(CloseableHttpResponse response){
        this.response = response;
    }

    public int getStatusCode(){
        return response.getStatusLine().getStatusCode();
    }

    @Override
    protected void finalize() throws IOException{
        System.out.println("over.....");
        response.close();
    }

    public JSONObject getHeader(){
        JSONObject jsonObject = new JSONObject();
        Header[] headers = response.getAllHeaders();
        for(Header header : headers){
            String key = header.getName();
            String value = header.getValue();
            jsonObject.put(key,value);
        }
        return jsonObject;
    }

    public String getText(){
        try{
            text =  EntityUtils.toString(response.getEntity());
        }catch (IOException e){
            e.printStackTrace();
        }
        return text;
    }

    public String getText(String charSet){
        try{
            text =  EntityUtils.toString(response.getEntity(),charSet);
        }catch (IOException e){
            e.printStackTrace();
        }
        return text;
    }

    public byte[] getContent(){
        byte[] content = null;
        try {
            content = EntityUtils.toByteArray(response.getEntity());
        } catch (IOException e){
            e.printStackTrace();
        }
        return content;
    }


}
