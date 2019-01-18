package com.example.coolweather.gson;

import com.google.gson.annotations.SerializedName;

//Now实体类
public class Now {

    //tmp表示当前气温
    @SerializedName("tmp")
    public String temperature;

    @SerializedName("cond")
    public More more;

    public class More{

        //txt用于表示天气概况
        @SerializedName("txt")
        public String info;
    }

}
