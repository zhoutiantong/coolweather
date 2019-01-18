package com.example.coolweather.gson;

import com.google.gson.annotations.SerializedName;

//Suggestion实体类
public class Suggestion {

    //comf用于表示舒适度的数组
    @SerializedName("comf")
    public Comfort comfort;

    //cw用于表示洗车指数的数组
    @SerializedName("cw")
    public CarWash carWash;

    //sport用于表示运动建议的数组
    public Sport sport;

    public class Comfort{
        //txt表示舒适度的文本内容
        @SerializedName("txt")
        public String info;
    }

    public class CarWash{
        //txt表示洗车指数的文本内容
        @SerializedName("txt")
        public String info;
    }

    public class Sport{
        //txt表示运动建议的文本内容
        @SerializedName("txt")
        public String info;
    }
}
