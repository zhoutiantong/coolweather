package com.example.coolweather.db;

import org.litepal.crud.LitePalSupport;

//创建实体类City，用来生成City数据表
public class City extends LitePalSupport {

    //int变量id是每个实体类中都应该有的字段，因为数据表每一行都应该有id
    private int id;

    //String变量cityName用来记录市的名字，对应数据表中 市 的这一列
    private String cityName;

    //int变量cityCode用来记录市的代号，对应数据表中 市的代号 这一列
    private int cityCode;

    //int变量provinceId用来当前市所属省的id值，对应当前数据表中的所属省的id值这一列
    private int provinceId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public int getCityCode() {
        return cityCode;
    }

    public void setCityCode(int cityCode) {
        this.cityCode = cityCode;
    }

    public int getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }
}
