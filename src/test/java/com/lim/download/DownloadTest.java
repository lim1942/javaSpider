package com.lim.download;


import com.alibaba.fastjson.JSONObject;
import com.lim.IoUtils.FileUtils;
import org.junit.Test;

public class DownloadTest
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue()
    {
        Download download = new Download();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("aa","aa");
        download.setProxy("127.0.0.1",8888);
        PyResponse response =  download.postJson("http://www.bing.com/",jsonObject);
        byte[] bytes = response.getContent();
        FileUtils fileUtils = new FileUtils();
//        fileUtils.write2Resources(bytes);
        System.out.println(fileUtils.readBytesFromResources("test2.txt").length);
    }
}
