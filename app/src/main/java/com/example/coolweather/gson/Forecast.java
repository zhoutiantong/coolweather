package com.example.coolweather.gson;

import com.google.gson.annotations.SerializedName;

//Forecast实体类
public class Forecast {

    //date用于表示天气预报日期
    public String date;

    //cond表示天气概况的数组名
    @SerializedName("cond")
    public More more;

    //tmp表示当天最高最低气温的数组名
    @SerializedName("tmp")
    public Temperature temperature;

    public class Temperature{

        //max用于表示当天最高气温
        public String max;

        //min用于表示当天最低气温
        public String min;

    }

    public class More{

        //txt_d用于表示天气概况
        @SerializedName("txt_d")
        public String info;

    }
}
