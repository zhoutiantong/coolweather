package com.example.coolweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    //项目中的每个活动都应该重写onCreate()方法
    protected void onCreate(Bundle savedInstanceState) {
        //调用父类的onCreate()方法
        super.onCreate(savedInstanceState);
        //将activity_main.xml布局加载进来
        setContentView(R.layout.activity_main);
        //通过PreferenceManager类的getDefaultSharedPreferences()方法得到SharedPreferences对象。
        //getDefaultSharedPreferences()方法接收一个context参数
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //SharedPreferences对象调用getString方法读取键名为weather的数据，读取缓存的数据
        //如果传入的键找不到对应的值，以null为默认值返回,当传入的键值不为null时，进入if代码块
        if (prefs.getString("weather",null) != null){
            //构建Intent的实例，参数为显性Intent，表示当前活动跳转到下一个活动
            Intent intent = new Intent(this,WeatherActivity.class);
            //调用startActivity()方法开启活动的跳转，参数传入Intent对象
            startActivity(intent);
            //活动跳转后将当前活动销毁
            finish();
        }
    }
}
