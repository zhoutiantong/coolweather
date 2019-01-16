package com.example.coolweather.db;

import org.litepal.crud.LitePalSupport;

//创建实体类County，用来生成County数据表
public class County extends LitePalSupport {

    //int变量id是每个实体类中都应该有的字段，因为数据表每一行都应该有id
    private int id;

    //String变量countyName用来记录先的名字，对应数据表中 县 的这一列
    private String countyName;

    //String变量weatherId用来记录县对应的天气id，对应数据表中天气id这一列
    private String weatherId;

    //int变量cityId用来当前市所属市的id值，对应当前数据表中的所属市的id值这一列
    private int cityId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public String getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }
}
