package com.example.coolweather;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.coolweather.gson.Forecast;
import com.example.coolweather.gson.Weather;
import com.example.coolweather.util.HttpUtil;
import com.example.coolweather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    private ScrollView weatherLayout;

    private TextView titleCity;

    private TextView titleUpdateTime;

    private TextView degreeText;

    private TextView weatherInfoText;

    private LinearLayout forecastLayout;

    private TextView aqiText;

    private TextView pm25Text;

    private TextView comfortText;

    private TextView carWashText;

    private TextView sportText;

    private ImageView bingPicImg;

    public SwipeRefreshLayout swipeRefresh;

    private String mWeatherId;

    //声明DrawerLayout全局变量,表示滑动菜单
    public DrawerLayout drawerLayout;

    //声明Button全局变量，表示导航按钮
    private Button navButton;

    @Override
    //项目中的每个活动都应该重写onCreate()方法
    protected void onCreate(Bundle savedInstanceState) {
        //调用父类的onCreate()方法
        super.onCreate(savedInstanceState);
        //让当前活动的背景图和系统状态栏融合，这个功能在5.0及以上系统才支持
        //这里if判断当前版本号大于或等于21，进入代码块
        if (Build.VERSION.SDK_INT >= 21){
            //调用getWindow().getDecorView()方法拿到当前活动的DecorView，
            //然后当前活动的DecorView再调用setSystemUiVisibility()方法来改变系统UI的显示
            //这里调用View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN和View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            //表示活动的布局会显示再状态栏上
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            //最后调用getWindow().setStatusBarColor()方法将系统状态栏设置为透明色
            //getWindow()获得系统的对象
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        //将activity_weather.xml布局加载进来
        setContentView(R.layout.activity_weather);
        //初始化各控件
        //构建DrawerLayout的实例，通过findViewById()方法
        //滑动菜单的布局控件
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        //构建Button的实例，通过findViewById()方法
        //导航按钮的布局控件
        navButton = (Button)findViewById(R.id.nav_button);
        //获取SwipeRefreshLayout的实例，通过findViewById()方法获取到布局文件中定义的元素，
        //来得到SwipeRefreshLayout的实例,下拉刷新的布局控件
        //因为findViewById()方法返回的是一个View对象所以需要强制转换
        swipeRefresh = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh);
        //SwipeRefreshLayout的实例调用setColorSchemeResources()方法来设置下拉刷新进度条的颜色
        //这里使用colorPrimary主题色作为进度条了
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        //构建ImageView（天气界面外层控件）的实例，通过findViewById()方法
        bingPicImg = (ImageView)findViewById(R.id.bing_pic_img);
        //构建ScrollView（天气界面外层控件）的实例，通过findViewById()方法
        weatherLayout = (ScrollView)findViewById(R.id.weather_layout);
        //构建TextView（天气界面头部布局中显示城市名控件）的实例，通过findViewById()方法
        titleCity = (TextView) findViewById(R.id.title_city);
        //构建TextView（天气界面头部布局中显示更新时间控件）的实例，通过findViewById()方法
        titleUpdateTime = (TextView) findViewById(R.id.title_update_time);
        //构建TextView（天气界面天气信息布局中显示当前气温控件）的实例，通过findViewById()方法
        degreeText = (TextView) findViewById(R.id.degree_text);
        //构建TextView（天气界面天气信息布局中显示天气概况控件）的实例，通过findViewById()方法
        weatherInfoText = (TextView) findViewById(R.id.weather_info_text);
        //构建TextView（天气界面未来几天天气信息的外部布局控件）的实例，通过findViewById()方法
        forecastLayout = (LinearLayout) findViewById(R.id.forecast_layout);
        //构建TextView（天气界面空气质量信息布局中显示AQI指数的控件）的实例，通过findViewById()方法
        aqiText = (TextView) findViewById(R.id.aqi_text);
        //构建TextView（天气界面空气质量信息布局中显示PM2.5指数的控件）的实例，通过findViewById()方法
        pm25Text = (TextView) findViewById(R.id.pm25_text);
        //构建TextView（天气界面生活建议信息布局中显示舒适度的控件）的实例，通过findViewById()方法
        comfortText = (TextView) findViewById(R.id.comfort_text);
        //构建TextView（天气界面生活建议信息布局中显示洗车指数的控件）的实例，通过findViewById()方法
        carWashText = (TextView) findViewById(R.id.car_wash_text);
        //构建TextView（天气界面生活建议信息布局中显示运动建议的控件）的实例，通过findViewById()方法
        sportText = (TextView) findViewById(R.id.sport_text);
        //通过PreferenceManager类的getDefaultSharedPreferences()方法得到SharedPreferences对象。
        //getDefaultSharedPreferences()方法接收一个context参数
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //SharedPreferences对象调用getString方法读取键名为weather的数据，读取缓存的数据
        //如果传入的键找不到对应的值，以null为默认值返回
        //并用一个String对象保存下来
        String weatherString = prefs.getString("weather",null);
        if (weatherString != null){
            //有缓存时直接解析天气数据
            //调用Utility类方法handleWeatherResponse()方法将JSON数据解析成Weather实体类对象
            Weather weather = Utility.handleWeatherResponse(weatherString);
            mWeatherId = weather.basic.weatherId;
            //调用showWeatherInfo()方法将解析的天气Weather实体类数据处理并展示在UI上
            showWeatherInfo(weather);
        }else {
            //无缓存时去服务器上查询天气
            //使用gerIntent()方法获取到用于启动当前活动的Intent，
            //然后调用getStringExtra()方法传入相应的键，得到传递过来的数据
            mWeatherId = getIntent().getStringExtra("weather_id");
            //调用ScrollView的setVisibility()方法，
            //参数传入INVISIBLE表示在服务器上查询参数时让ScrollView控件不可见，但是还占着原来的空间
            weatherLayout.setVisibility(View.INVISIBLE);
            //调用requestWeather()方法根据天气id去服务器上请求城市的天气信息
            requestWeather(mWeatherId);
        }
        //SwipeRefreshLayout的实例调用setOnRefreshListener()方法来设置一个下拉刷新的监听器，
        //当触发下拉刷新操作的时候就会回调这个监听器的onRefresh()方法
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //调用requestWeather()重新去服务器请求数据
                requestWeather(mWeatherId);
            }
        });
        //给导航按钮注册一个点击事件的监听，当按钮被点击时就会回调onClick()方法
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //调用openDrawer()方法将滑动菜单显示出来,为了保证代码中的行为和XML中定义的一致
                //这里使用的参数时GravityCompat.START
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        //SharedPreferences对象调用getString方法读取键名为bing_pic的数据，读取缓存的数据
        //如果传入的键找不到对应的值，以null为默认值返回
        //并用一个String对象保存下来
        String bingPic = prefs.getString("bing_pic",null);
        //当储存键名为bing_pic数据的对象不为null时，进入if代码块
        if (bingPic != null){
            //使用Glide加载图片
            //调用Glide.with()方法并传入一个Context、Activity、Fragment参数，
            //然后调用load()方法去加载图片，可以传入一个URL地址，也可以是本地路径，
            //最后调用into()方法将图片设置到具体某一个ImageView中
            Glide.with(this).load(bingPic).into(bingPicImg);
        }else {
            //否则调用loadBingPic()方法加载必应每日一图
            loadBingPic();
        }
    }

    //requestWeather()方法根据天气id去服务器上请求城市的天气信息
    public void requestWeather(final String weatherId){

        //声明一个String变量赋值查询某地的接口URL，URL的格式如下所示
        String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId +
                "&key=fb0e22d7b17f4bd0947c2e0c0045093d";

        //调用类方法sendOkHtpRequest()来发送网络请求
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            //重写onFailure()方法对请求服务器发送异常状况时进行相应的处理
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //弹出Toast提示，提示获取天气失败
                        Toast.makeText(WeatherActivity.this,"获取天气信息失败",
                                Toast.LENGTH_SHORT).show();
                        //最后调用SwipeRefreshLayout的实例的setRefreshing()方法，传入false，
                        //用于表示刷新事件结束，并隐藏进度条
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }

            @Override
            //服务器返回的数据会回调到onResponse()方法中
            public void onResponse(Call call, Response response) throws IOException {
                //Response对象调用body()方法得到返回数据的具体内容，再调用string()方法将数据转换成字符串形式
                //最后声明一个Sting常量保存下来
                final String responseText = response.body().string();
                //调用类方法handleWeatherResponse()解析服务器返回的数据，
                //在该方法的参数传入服务器返回数据的具体内容
                //该方法的返回值类型为Weather，声明一个Weather常量保存下来
                final Weather weather = Utility.handleWeatherResponse(responseText);
                //调用runOnUiThread()方法将子线程切换成主线程
                //因为接下来有更新UI的操作，不能再子线程更新UI
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //当服务器返回的数据为null并且返回的数据里status字段等于ok，进入if代码块
                        if (weather != null && "ok".equals(weather.status)){
                            //通过PreferenceManager类的getDefaultSharedPreferences()方法得到SharedPreferences对象。
                            //getDefaultSharedPreferences()方法接收一个context参数
                            //SharedPreferences对象调用edit()方法获取一个SharedPreferences.Editor对象
                            SharedPreferences.Editor editor = PreferenceManager
                                    .getDefaultSharedPreferences(WeatherActivity.this).edit();
                            //SharedPreferences.Editor对象调用putString方法向SharedPreferences.Editor对象中添加数据
                            editor.putString("weather",responseText);
                            //SharedPreferences.Editor对象调用apply()方法将添加的数据提交
                            editor.apply();
                            //调用showWeatherInfo()方法将解析的天气Weather实体类数据处理并展示在UI上
                            showWeatherInfo(weather);
                        }else {
                            //否则弹出Toast提示，获取天气信息失败
                            Toast.makeText(WeatherActivity.this,"获取天气信息失败",
                                    Toast.LENGTH_SHORT).show();
                        }
                        //最后调用SwipeRefreshLayout的实例的setRefreshing()方法，传入false，
                        //用于表示刷新事件结束，并隐藏进度条
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        });
        //调用loadBingPic()方法加载必应每日一图
        loadBingPic();
    }

    //loadBingPic()方法加载必应每日一图
    private void loadBingPic(){
        //声明一个String变量赋值查询每日一图的接口URL，URL的格式如下所示
        String requestBingPic = "http://guolin.tech/api/bing_pic";

        //调用类方法sendOkHtpRequest()来发送网络请求
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            //重写onFailure()方法对请求服务器发送异常状况时进行相应的处理
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            //服务器返回的数据会回调到onResponse()方法中
            public void onResponse(Call call, Response response) throws IOException {
                //Response对象调用body()方法得到返回数据的具体内容，再调用string()方法将数据转换成字符串形式
                //最后声明一个Sting常量保存下来
                final String bingPic = response.body().string();
                //通过PreferenceManager类的getDefaultSharedPreferences()方法得到SharedPreferences对象。
                //getDefaultSharedPreferences()方法接收一个context参数
                //SharedPreferences对象调用edit()方法获取一个SharedPreferences.Editor对象
                SharedPreferences.Editor editor = PreferenceManager.
                        getDefaultSharedPreferences(WeatherActivity.this).edit();
                //SharedPreferences.Editor对象调用putString方法向SharedPreferences.Editor对象中添加数据
                editor.putString("bing_pic",bingPic);
                //SharedPreferences.Editor对象调用apply()方法将添加的数据提交
                editor.apply();
                //调用runOnUiThread()方法将子线程切换成主线程
                //因为接下来有更新UI的操作，不能再子线程更新UI
                runOnUiThread(new Runnable() {
                    @Override
                    //使用Glide加载图片
                    //调用Glide.with()方法并传入一个Context、Activity、Fragment参数，
                    //然后调用load()方法去加载图片，可以传入一个URL地址，也可以是本地路径，
                    //最后调用into()方法将图片设置到具体某一个ImageView中
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
                    }
                });
            }
        });
    }

    //showWeatherInfo()方法将解析的天气Weather实体类数据处理并展示在UI上
    private void showWeatherInfo(Weather weather){
        //用Weather对象获取到Basic实体类中的城市名数据
        String cityName = weather.basic.cityName;
        //用Weather对象获取到Basic实体类中的天气更新时间数据，用split()方法将空格后面的数据显示出来
        String updateTime = weather.basic.update.updateTime.split(" ")[1];
        //用Weather对象获取到Now实体类中的当前气温数据，后面加上温度符号
        String degree = weather.now.temperature + "℃";
        //用Weather对象获取到Now实体类中的天气概况数据
        String weatherIinfo = weather.now.more.info;
        //Weather对象获取到Basic实体类中的城市名数据，调用setText()方法显示在头部布局的显示城市名的控件中
        titleCity.setText(cityName);
        //Weather对象获取到Basic实体类中的天气更新时间数据，调用setText()方法显示在头部布局的显示更新时间的控件中
        titleUpdateTime.setText(updateTime);
        //Weather对象获取到Now实体类中的当前气温数据，调用setText()方法显示在当前天气布局的显示当前气温数据的控件中
        degreeText.setText(degree);
        //Weather对象获取到Now实体类中的当前天气概况数据，
        //调用setText()方法显示在当前天气布局的显示当前天气概况数据的控件中
        weatherInfoText.setText(weatherIinfo);
        //LinearLayout的实例调用removeAllViews()方法把layout容器中views视图都移除掉
        //然后再重新加载控件
        forecastLayout.removeAllViews();
        //写一个for循环来处理未来几天天气预报的每天的天气信息，从weather对象中获取数据
        for (Forecast forecast : weather.forecastList){
            //创建一个View对象，
            //使用LayoutInflater的from()方法构建出一个LayoutInflater对象，然后这个对象调用inflate()方法
            //动态加载一个布局文件，inflate()方法第一个参数传入一个需要加载的布局文件的id，
            //第二个参数给加载好的布局添加一个父布局，第三个参数指定false，表示只让我们在父布局中声明的layout属性生效
            //将加载出来的布局用View对象保存下来，因为inflate()方法返回的是一个View对象
            //R.layout.foreacast_item为未来几天天气信息的子项布局控件
            View view = LayoutInflater.from(this).
                    inflate(R.layout.foreacast_item,forecastLayout,false);
            //构建TextView（天气界面未来几天天气信息的子项布局控件的显示天气预报日期控件）的实例，
            //通过findViewById()方法
            TextView dateText = (TextView)view.findViewById(R.id.date_text);
            //构建TextView（天气界面未来几天天气信息的子项布局控件的显示天气概况控件）的实例，
            //通过findViewById()方法
            TextView infoText = (TextView)view.findViewById(R.id.info_text);
            //构建TextView（天气界面未来几天天气信息的子项布局控件的显示当天最高温度控件）的实例，
            //通过findViewById()方法
            TextView maxText = (TextView)view.findViewById(R.id.max_text);
            //构建TextView（天气界面未来几天天气信息的子项布局控件的显示当天最低温度控件）的实例，
            //通过findViewById()方法
            TextView minText = (TextView)view.findViewById(R.id.min_text);
            //从Forecast对象中获取到天气预报日期数据，用setText()方法显示到用于显示天气预报日期控件上
            dateText.setText(forecast.date);
            //从Forecast对象中获取到天气概况数据，用setText()方法显示到用于显示天气概况控件上
            infoText.setText(forecast.more.info);
            //从Forecast对象中获取到天气当天最高气温数据，用setText()方法显示到用于显示天气当天最高气温控件上
            maxText.setText(forecast.temperature.max);
            //从Forecast对象中获取到天气当天最低气温数据，用setText()方法显示到用于显示天气当天最低气温控件上
            minText.setText(forecast.temperature.min);
            //将未来几天天气信息的子项布局控件添加到天气界面未来几天天气信息的外部控件布局中
            forecastLayout.addView(view);
        }
        //用Weather对象获取到AQI实体类中的数据不为null时，进入代码块
        if (weather.aqi != null){
            //Weather对象获取到AQI实体类中的aqi指数数据，
            //调用setText()方法显示在空气质量信息布局的显示AQI指数的控件中
            aqiText.setText(weather.aqi.city.aqi);
            //Weather对象获取到AQI实体类中的PM2.5指数数据，
            //调用setText()方法显示在空气质量信息布局的显示PM2.5指数的控件中
            pm25Text.setText(weather.aqi.city.pm25);
        }
        //Weather对象获取到Suggestion实体类中的舒适度数据
        String comfort = "舒适度：" + weather.suggestion.comfort.info;
        //Weather对象获取到Suggestion实体类中的洗车指数数据
        String carWash = "洗车指数：" + weather.suggestion.carWash.info;
        //Weather对象获取到Suggestion实体类中的运动建议数据
        String sport = "运动建议：" + weather.suggestion.sport.info;
        //调用setText()方法显示在生活建议信息布局的显示舒适度的控件中
        comfortText.setText(comfort);
        //调用setText()方法显示在生活建议信息布局的显示洗车指数的控件中
        carWashText.setText(carWash);
        //调用setText()方法显示在生活建议信息布局的显示运动建议的控件中
        sportText.setText(sport);
        //调用ScrollView的setVisibility()方法，
        //参数传入VISIBLE表示在服务器上查询参数时让ScrollView控件可见
        weatherLayout.setVisibility(View.VISIBLE);
    }
}
