package com.lim.IoUtils;

import org.apache.commons.lang.StringUtils;
import java.io.*;
import java.util.ArrayList;
import java.util.List;


//文件读写，流用inputStream或BufferedInputStream后者效率高。
// 字符用bufferead/bufferwrite,或者转化成byte[]写入。
public class FileUtils {

    // 从resources 读取string
    public String readFromResources(String resourcesPath) throws IOException{
        String line;
        InputStream inputStream = ClassLoader.getSystemResourceAsStream(resourcesPath);
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(inputStream, "UTF-8"));
        StringBuilder stringBuilder = new StringBuilder();
        while ((line=bufferedReader.readLine())!=null){
            stringBuilder.append(line);
            stringBuilder.append("\n");
        }
        inputStream.close();
        bufferedReader.close();
        return stringBuilder.toString();
    }

    // 从resources 读取 byte [],最大是int的容量
    public byte[] readBytesFromResources(String resourcesPath){
        InputStream inputStream;
        byte [] bytes=null;
        int length;
        inputStream = ClassLoader.getSystemResourceAsStream(resourcesPath);
        try {
            length = inputStream.available();
            bytes = new byte[length];
        }catch (IOException e){
            e.printStackTrace();
        }
        return bytes;
    }

    // 从resources 读取 List byte [] 大文件
    public List<byte[]> readBytesFromResourcesBig(String resourcesPath){
        List<byte[]> list = new ArrayList<>();
        InputStream inputStream;
        byte [] bytes=new byte[1024*1024];
        inputStream = ClassLoader.getSystemResourceAsStream(resourcesPath);
        try {
            while (inputStream.read(bytes) != -1) {
                list.add(bytes);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return list;
    }

    // 获取输出在resources的文件名
    private String getPath(String resourcesPath){
        List<String> pathList = new ArrayList<>();
        pathList.add(System.getProperty("user.dir"));
        pathList.add("src");
        pathList.add("main");
        pathList.add("resources");
        String basePath = StringUtils.join(pathList,File.separator);
        return basePath + File.separator + resourcesPath;
    }

    // 写入byte[]到文件
    // 文件不存在自动创建
    public boolean write2Resources(byte[] bytes,String resourcesPath,boolean append){
        String realPath = getPath(resourcesPath);
        try {
            OutputStream outputStream = new FileOutputStream(realPath,append);
            outputStream.write(bytes);
            outputStream.close();
        }catch (IOException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    // 写入string到文件
    // 文件不存在自动创建
    public boolean write2Resources(String string,String resourcesPath,boolean append){
        String realPath = getPath(resourcesPath);
        try{
            OutputStream outputStream = new FileOutputStream(realPath);
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
            bufferedWriter.write(string);
            bufferedWriter.close();
            outputStream.close();
            return true;
        }catch (IOException e){
            e.printStackTrace();
        }
        return false;
    }

}
