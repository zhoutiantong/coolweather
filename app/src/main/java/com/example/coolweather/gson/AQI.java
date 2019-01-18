package com.example.coolweather.gson;

//AQI实体类
public class AQI {

    public AQICity city;

    public class AQICity{

        //aqi用于表示空气aqi指数
        public String aqi;

        //PM25用于表示空气中PM2.5指数
        public String pm25;
    }
}
