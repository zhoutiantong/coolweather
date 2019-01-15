package com.example.coolweather.util;

import android.text.TextUtils;

import com.example.coolweather.db.City;
import com.example.coolweather.db.County;
import com.example.coolweather.db.Province;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//创建实体工具类，用来解析json字符串
public class Utility {

    //解析和处理服务器返回的省级数据
    public static boolean handleProvinceResponse(String response){
        //使用TextUtils.isEmpty()方法判断调用handleProvinceResponse()方法传入的String参数response是否为null
        if (!TextUtils.isEmpty(response)){
            try {
                //由于服务器中定义的是一个JSON数组，因此这里首先是将服务器返回的数据传入到一个JSONArray对象中
                JSONArray allProvince = new JSONArray(response);
                //写一个for循环遍历JSONArray
                for (int i = 0; i < allProvince.length(); i++){
                    //从JSONArray中取出的每一个元素都是一个JSONObject对象，因此先实例化一个JSONObject
                    //参数传入需要遍历数组的下标
                    JSONObject provinceObject = allProvince.getJSONObject(i);
                    //构建实体类Province的实例对象
                    Province province = new Province();
                    //将解析出来的 记录省的名字provinceName 数据和 记录省的代号provinceCode 数据组装成实体类对象
                    province.setProvinceName(provinceObject.getString("name"));
                    province.setProvinceCode(provinceObject.getInt("id"));
                    //最后Province对象调用save()方法将数据存储到数据库当中
                    province.save();
                }
                //JSON数据解析完毕后返回true
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        //服务器返回的数据为null时，返回false；
        return false;
    }

    //解析和处理服务器返回的市级数据
    public static boolean handleCityResponse(String response , int provinceId){
        //使用TextUtils.isEmpty()方法判断调用handleProvinceResponse()方法传入的String参数response是否为null
        if (!TextUtils.isEmpty(response)){
            try {
                //由于服务器中定义的是一个JSON数组，因此这里首先是将服务器返回的数据传入到一个JSONArray对象中
                JSONArray allCities = new JSONArray(response);
                //写一个for循环遍历JSONArray
                for (int i = 0; i < allCities.length(); i++){
                    //从JSONArray中取出的每一个元素都是一个JSONObject对象，因此先实例化一个JSONObject
                    //参数传入需要遍历数组的下标
                    JSONObject cityObject = allCities.getJSONObject(i);
                    //构建实体类City的实例对象
                    City city = new City();
                    //将解析出来的记录市的名字cityName数据、
                    //记录市的代号cityCode数据、
                    //记录当前市所属省的id值ProvinceId
                    //组合在一起组装成实体类对象
                    city.setCityName(cityObject.getString("name"));
                    city.setCityCode(cityObject.getInt("id"));
                    city.setProvinceId(provinceId);
                    //最后City对象调用save()方法将数据存储到数据库当中
                    city.save();
                }
                //JSON数据解析完毕后返回true
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        //服务器返回的数据为null时，返回false；
        return false;
    }

    //解析和处理服务器返回的县级数据
    public static boolean handleCountyResponse(String response , int cityId){
        //使用TextUtils.isEmpty()方法判断调用handleProvinceResponse()方法传入的String参数response是否为null
        if (!TextUtils.isEmpty(response)){
            try {
                //由于服务器中定义的是一个JSON数组，因此这里首先是将服务器返回的数据传入到一个JSONArray对象中
                JSONArray allCities = new JSONArray(response);
                //写一个for循环遍历JSONArray
                for (int i = 0; i < allCities.length(); i++){
                    //从JSONArray中取出的每一个元素都是一个JSONObject对象，因此先实例化一个JSONObject
                    //参数传入需要遍历数组的下标
                    JSONObject cityObject = allCities.getJSONObject(i);
                    //构建实体类City的实例对象
                    County county = new County();
                    //将解析出来的记录县的名字countyName数据
                    county.setCountyName(cityObject.getString("name"));
                    //记录县的代号weatherId数据
                    county.setWeatherId(cityObject.getString("weather_id"));
                    //记录当前市所属市的id值CityId
                    county.setCityId(cityId);
                    //将实体类添加的数据组合在一起组装成实体类对象
                    //最后County对象调用save()方法将数据存储到数据库当中
                    county.save();
                }
                //JSON数据解析完毕后返回true
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        //服务器返回的数据为null时，返回false；
        return false;
    }
}
