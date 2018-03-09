package com.nineShadow.util;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

public class imageDownload {
//    public static void main(String[] args) {
//        String url = "http://wx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTKCGt4SpiaqSYAlicC4AKHjWNVzBRV7pBZzricZmA9ttGFxic1q9LiazTvMhJiaJSR4b5eKrFNK20RrY1tg/0";
//        downloadPicture(url,123);
//    }
    //链接url下载图片
    public static void downloadPicture(String urlList,Integer id) {
        URL url = null;
        int imageNumber = 0;

        try {
            url = new URL(urlList);
            DataInputStream dataInputStream = new DataInputStream(url.openStream());

            String imageName =  "F:/test"+id+".jpg";

            FileOutputStream fileOutputStream = new FileOutputStream(new File(imageName));
            ByteArrayOutputStream output = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];
            int length;

            while ((length = dataInputStream.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
            byte[] context=output.toByteArray();
            fileOutputStream.write(output.toByteArray());
            dataInputStream.close();
            fileOutputStream.close();
        } catch (MalformedURLException e) {
//            e.printStackTrace();
        } catch (IOException e) {
//            e.printStackTrace();
        }
    }
}