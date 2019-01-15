package com.example.coolweather.db;

import org.litepal.crud.LitePalSupport;

//创建实体类Province，用来生成Province数据表
public class Province extends LitePalSupport {

    //int变量id是每个实体类中都应该有的字段，因为数据表每一行都应该有id
    private int id;

    //String变量provinceName用来记录省的名字，对应数据表中省的这一列
    private String provinceName;

    //int变量provinceCode用来记录省的代号，对应数据表中省的代号这一列
    private int provinceCode;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public int getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(int provinceCode) {
        this.provinceCode = provinceCode;
    }
}
