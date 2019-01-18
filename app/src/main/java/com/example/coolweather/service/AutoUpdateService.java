package com.example.coolweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import com.example.coolweather.WeatherActivity;
import com.example.coolweather.gson.Weather;
import com.example.coolweather.util.HttpUtil;
import com.example.coolweather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

//AutoUpdateService是一个继承自Service的类，说明这是一个服务
public class AutoUpdateService extends Service {

    @Override
    //onBind()是Service中唯一一个抽象方法，所以必须在子类里实现
    //活动指挥服务去干什么，服务就去干什么
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    //onStartCommand()方法会在每次服务启动的时候调用
    //服务一旦启动就立刻去执行某个操作，这个逻辑写在onStartCommand()方法中
    public int onStartCommand(Intent intent, int flags, int startId) {
        //调用updateWeather()方法来更新天气
        updateWeather();
        //调用updateBingpic()方法来更新背景图片
        updateBingpic();
        //通过调用Context的getSystemService()方法获取到AlarmManager的实例，需要传入的参数为ALARM_SERVICE
        AlarmManager manager = (AlarmManager)getSystemService(ALARM_SERVICE);
        //设置八个小时的毫秒数
        int anHour = 8 * 60 * 60 * 1000;
        //SystemClock系统时间类调用elapsedRealtime()方法获取到系统启动到现在的时间，然后加上8个小时的毫秒数
        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
        //构建Intent的实例，意图为当前活动启动的时候跳转到AutoUpdateService服务
        Intent i = new Intent(this,AutoUpdateService.class);
        //通过PendingIntent.getService()方法获取PendingIntent的实例，该方法用于跳转活动
        //还有服务，广播，分别为getService()和getBroadcast()方法用例获取PendingIntent的实例
        //getService()方法接收四个参数(另外两种也是一样的参数)，第一个参数为Context
        //第二个参数一般用不到，通常传入0即可
        //第三个参数是一个Intent对象，可以通过这个构建出PendingIntent的意图
        //第四个参数用于确定PendingIntent的行为，一般情况下传入0即可
        PendingIntent pi = PendingIntent.getService(this,0,i,0);
        //AlarmManager的实例调用cancel()方法取消服务的提醒
        manager.cancel(pi);
        //AlarmManager的实例调用set()方法设置一个定时任务，该方法接收三个参数
        //第一个参数是一个整型参数，用于指定AlarmManager类的工作类型，
        //这里传入ELAPSED_REALTIME_WAKEUP表示让定时任务的出发时间从系统开机开始算起，唤醒CPU
        //第二个参数就是定时任务触发时间，以毫秒为单位，表示这里是开机至今的时间再加上延迟执行的时间
        //第三个参数是一个PendingIntent对象。
        //这样当定时任务被触发的时候，服务的onStartCommand()方法就可以得到执行
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);
        //返回父类的onStartCommand()方法
        return super.onStartCommand(intent, flags, startId);
    }

    //服务中更新天气信息
    private void updateWeather(){
        //通过PreferenceManager类的getDefaultSharedPreferences()方法得到SharedPreferences对象。
        //getDefaultSharedPreferences()方法接收一个context参数
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //SharedPreferences对象调用getString()方法读取键名为weather的数据，读取缓存的数据
        //如果传入的键找不到对应的值，以null为默认值返回
        //并用一个String对象保存下来
        String weatherString = prefs.getString("weather",null);
        if (weatherString != null){
            //有缓存时直接解析天气数据
            //调用Utility类方法handleWeatherResponse()方法将JSON数据解析成Weather实体类对象
            Weather weather = Utility.handleWeatherResponse(weatherString);
            //用Weather对象获取到Basic实体类中的城市对应天气的id
            String weatherId = weather.basic.weatherId;
            //声明一个String变量赋值查询某地的接口URL，URL的格式如下所示
            String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId +
                    "&key=fb0e22d7b17f4bd0947c2e0c0045093d";
            //调用类方法sendOkHtpRequest()来发送网络请求
            HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
                @Override
                //重写onFailure()方法对请求服务器发送异常状况时进行相应的处理
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                //服务器返回的数据会回调到onResponse()方法中
                public void onResponse(Call call, Response response) throws IOException {
                    //Response对象调用body()方法得到返回数据的具体内容，再调用string()方法将数据转换成字符串形式
                    //最后声明一个Sting变量保存下来
                    String responseText = response.body().string();
                    //调用类方法handleWeatherResponse()解析服务器返回的数据，
                    //在该方法的参数传入服务器返回数据的具体内容
                    //该方法的返回值类型为Weather，声明一个Weather变量保存下来
                    Weather weather = Utility.handleWeatherResponse(responseText);
                    //当服务器返回的数据不为null并且返回的数据里status字段等于ok，进入if代码块
                    if (weather != null && "ok".equals(weather.status)) {
                        //建立SharedPreferences存储
                        //通过PreferenceManager类的getDefaultSharedPreferences()方法得到SharedPreferences对象。
                        //getDefaultSharedPreferences()方法接收一个context参数
                        //SharedPreferences对象调用edit()方法获取一个SharedPreferences.Editor对象
                        SharedPreferences.Editor editor = PreferenceManager
                                .getDefaultSharedPreferences(AutoUpdateService.this).edit();
                        //SharedPreferences.Editor对象调用putString方法向SharedPreferences.Editor对象中添加数据
                        editor.putString("weather", responseText);
                        //SharedPreferences.Editor对象调用apply()方法将添加的数据提交
                        editor.apply();
                    }
                }
            });

        }
    }

    //服务中更新每日一图
    private void updateBingpic(){
        //声明一个String变量赋值查询每日一图的接口URL，URL的格式如下所示
        String requestBingPic = "http://guolin.tech/api/bing_pic";

        //调用类方法sendOkHtpRequest()来发送网络请求
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            //重写onFailure()方法对请求服务器发送异常状况时进行相应的处理
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            //服务器返回的数据会回调到onResponse()方法中
            public void onResponse(Call call, Response response) throws IOException {
                //Response对象调用body()方法得到返回数据的具体内容，再调用string()方法将数据转换成字符串形式
                //最后声明一个Sting变量保存下来
                String bingPic = response.body().string();
                //通过PreferenceManager类的getDefaultSharedPreferences()方法得到SharedPreferences对象。
                //getDefaultSharedPreferences()方法接收一个context参数
                //SharedPreferences对象调用edit()方法获取一个SharedPreferences.Editor对象
                SharedPreferences.Editor editor = PreferenceManager.
                        getDefaultSharedPreferences(AutoUpdateService.this).edit();
                //SharedPreferences.Editor对象调用putString方法向SharedPreferences.Editor对象中添加数据
                editor.putString("bing_pic",bingPic);
                //SharedPreferences.Editor对象调用apply()方法将添加的数据提交
                editor.apply();
            }
        });
    }
}
