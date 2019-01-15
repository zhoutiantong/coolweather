package com.example.coolweather.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;

//创建一个公共的类，用来发送网络请求
public class HttpUtil {
    //使用OkHttp发送HTTP请求
    //并提供一个静态方法，当发起网络请求的时候只需要简单的调用一下这个方法
    public static void sendOkHttpRequest(String address , okhttp3.Callback callback){
        //创建一个OkHttpClient的实例
        OkHttpClient client = new OkHttpClient();
        //创建Request对象发起一条HTTP请求
        Request request = new Request.Builder()
                //通过url()方法设置目标的网络地址
                .url(address)
                //build()方法开始发起请求
                .build();
        //调用OkHttpClient实例的nuwCall()方法创建一个Call对象，
        //并使用Call对象调用enqueue()方法来发送请求并获取服务器返回的数据,enqueue()方法返回一个Response对象
        //Response对象就是返回的数据了
        client.newCall(request).enqueue(callback);
    }
}
