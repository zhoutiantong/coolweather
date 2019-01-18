package com.example.coolweather.gson;

import com.google.gson.annotations.SerializedName;

//basin实体类
public class Basic {

    //city表示城市名
    @SerializedName("city")
    public String cityName;

    //id表示城市对应天气的id
    @SerializedName("id")
    public String weatherId;

    public Update update;

    public class Update{

        //update中的loc表示天气的更新时间
        @SerializedName("loc")
        public String updateTime;
    }
}
