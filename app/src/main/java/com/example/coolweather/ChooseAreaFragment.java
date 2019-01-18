package com.example.coolweather;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coolweather.db.City;
import com.example.coolweather.db.County;
import com.example.coolweather.db.Province;
import com.example.coolweather.util.HttpUtil;
import com.example.coolweather.util.Utility;

import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

//创建一个选择区域碎片
public class ChooseAreaFragment extends Fragment {

    //静态常量，代表省市县的等级
    public static final int LEVEL_PROVINCE = 0 ;

    public static final int LEVEL_CITY = 1;

    public static final int LEVEL_COUNTY = 2;

    //创建ProgressDialog变量，构建ProgressDialog实例时方便调用
    private ProgressDialog progressDialog;

    //创建TextView变量，构建TextView实例时方便调用
    private TextView titleText;

    //创建Button变量，构建Button实例时方便调用
    private Button backButton;

    //创建ListView变量，构建ListView实例时方便调用
    private ListView listView;

    //声明一个ArrayAdapter变量，泛型指定为String，用来存放适配器对象
    private ArrayAdapter<String> adapter;

    //声明一个List数组，泛型指定为String
    private List<String> dataList = new ArrayList<>();

    //该数组用于存放省列表
    private List<Province> provinceList;

    //该数组用于存放市列表
    private List<City> cityList;

    //该数组用于存放县列表
    private List<County> countyList;

    //该变量用于表示选择的省份
    private  Province selectedProvince;

    //该变量用于表示选择的城市
    private City seletedCity;

    //该变量用于表示当前选中的级别
    private int currentLevel;


    @Override
    //重写onCreateView()方法用于返回View的实例，在这个方法中将R.layout.choose_area布局加载进来
    public View onCreateView( LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
        //创建一个View的实例，因为onCreateView()方法中的参数已经自带了LayoutInflater的对象
        //所有该对象直接调用inflate()方法将布局文件中定义的news_content_frag布局动态加载进来
        //inflate()方法第一个参数传入一个需要加载的布局文件的id，
        //第二个参数给加载好的布局添加一个父布局，第三个参数指定false，表示只让我们在父布局中声明的layout属性生效
        //将加载出来的布局用View对象保存下来，因为inflate()方法返回的是一个View对象
        View view = inflater.inflate(R.layout.choose_area,container,false);
        //构建出TextView的实例使用view.findViewById()方法，View表示哪个主布局中的子项布局
        titleText = (TextView)view.findViewById(R.id.title_text);
        //构建出Button的实例使用view.findViewById()方法，View表示哪个主布局中的子项布局
        backButton = (Button)view.findViewById(R.id.back_button);
        //构建出ListView的实例使用view.findViewById()方法，View表示哪个主布局中的子项布局
        listView = (ListView)view.findViewById(R.id.list_view);
        //数组中的数据无法直接传递给ListView，需要借助适配器来完成
        //ArrayAdapter就是这里使用的适配器，它通过泛型来指定需要适配的数据类型，
        //然后在构造函数中把要适配的数据传入，ArrayAdapter中有多个构造函数的重载，根据实际情况来选中最适合的
        //这里ArrayAdapter的构造函数有三个函数，第一个函数为传入当前的上下文
        //第二个函数传入ListView的子项布局id，这里使用的是android.R.layout.simple_list_item_1作为ListView子项布局id
        //android.R.layout.simple_list_item_1这个id是Android内置的布局文件，里面只有一个TextView，用于显示简单的文本
        //第三个函数传入需要适配的数据
        adapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1,dataList);
        //ListView对象调用ListView的setAdapter()方法，将构建好的适配器对象传递进去，
        //这样ListView和数据直接的关联就完成了
        listView.setAdapter(adapter);
        //返回View的实例
        return view;
    }

    @Override
    //重写onActivityCreated()方法,在与碎片相关联的活动一定已经创建完毕的时候回调
    public void onActivityCreated( Bundle savedInstanceState) {
        //调用父类的onActivityCreated()方法
        super.onActivityCreated(savedInstanceState);
        //给ListView注册一个点击事件监听器，当ListView被点击时就会回调onItemClick()方法
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            //在该方法中写具体的点击事件逻辑
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //当点击ListView点击级别为LEVEL_PROVINCE = 0常量时
                if (currentLevel == LEVEL_PROVINCE){
                    //代表省列表的List数组调用get()方法获取点击的位置，用Province变量保存下来
                    selectedProvince = provinceList.get(position);
                    //调用queryCities()方法查询选中省内所有的市
                    queryCities();
                    //当点击ListView点击级别为LEVEL_CITY = 1常量时
                }else if (currentLevel == LEVEL_CITY){
                    //代表市列表的List数组调用get()方法获取点击的位置，用City变量保存下来
                    seletedCity = cityList.get(position);
                    //调用queryCounties()方法查询当前市内所有的县
                    queryCounties();
                }else if (currentLevel == LEVEL_COUNTY){
                    //代表县列表的List数组调用get()方法获取点击的位置,返回的是County对象
                    //在调用getWeatherId()方法获取到县对应的天气id，声明一个变量保存下来
                    String weatherId = countyList.get(position).getWeatherId();
                    //调用getActivity()方法获取当前碎片在哪个活动当中，配合instanceof关键字来判断
                    //如果是在MainActivity活动中处理逻辑不变
                    if (getActivity() instanceof MainActivity) {
                        //构建Intent的实例，参数为显性Intent，表示当前活动跳转到下一个活动
                        Intent intent = new Intent(getActivity(), WeatherActivity.class);
                        //Intent的实例调用putExtra()方法将数据传入下一个活动
                        intent.putExtra("weather_id", weatherId);
                        //调用startActivity()方法开启活动的跳转，参数传入Intent对象
                        startActivity(intent);
                        //在跳转后将当前活动销毁
                        getActivity().finish();
                        //如果当前碎片在WeatherActivity活动中，关闭滑动菜单，显示下拉刷新进度条
                        //然后请求新城市的天气信息
                    }else if (getActivity() instanceof  WeatherActivity){
                        //获取到WeatherActivity活动的实例
                        WeatherActivity activity = (WeatherActivity) getActivity();
                        //WeatherActivity活动中的DrawerLayout的实例调用closeDrawers()方法关闭滑动菜单
                        activity.drawerLayout.closeDrawers();
                        // //WeatherActivity活动中的SwipeRefreshLayout实例调用setRefreshing()方法显示下拉刷新的进度条
                        activity.swipeRefresh.setRefreshing(true);
                        //WeatherActivity活动的实例请求新城市的天气ID。
                        activity.requestWeather(weatherId);

                    }
                }
            }
        });
        //给返回Button注册一个点击事件监听器，当ListView被点击时就会回调onClick()方法
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            //在该方法中写具体的点击事件逻辑
            public void onClick(View v) {
                //当点击返回Button点击级别为LEVEL_COUNTY = 2常量时
                if (currentLevel == LEVEL_COUNTY){
                    //调用queryCities()方法查询选中省内所有的市
                    queryCities();
                    //当点击返回Button点击级别为LEVEL_CITY = 1常量时
                }else if (currentLevel == LEVEL_CITY){
                    //调用queryProvinces()方法查询全国所有的省
                    queryProvinces();
                }
            }
        });
        queryProvinces();
    }

    //queryProvinces()方法查询全国所有的省，优先从数据库查询，如果数据库没有查询到数据再去服务器上查询
    private void queryProvinces(){
        //TextView调用setText()方法设置头布局的文字
        titleText.setText("中国");
        //返回Button调用setVisibility()方法参数传入VIEW.GONE将返回键隐藏
        backButton.setVisibility(View.GONE);
        //调用LitePal.findAll()方法来查询表中的所有数据，通过findAll()方法的参数指定查询的表
        //将查询后的数据用List数组保存下来
        provinceList = LitePal.findAll(Province.class);
        //用来存放数据表中数组调用size()方法获取的长度大于0时，遍历数组中的数据
        if (provinceList.size() > 0){
            //调用clear()方法清除掉List数组中的数据
            dataList.clear();
            //遍历List集合中Province数据表的数据
            for (Province province : provinceList){
                //将Province数据表的省的名字这一列数据，通过Province实体类的getProvinceName()方法获取
                //通过List数组调用add()方法将获取的数据放入List数组中
                dataList.add(province.getProvinceName());
            }
            //调用ArrayAdapter适配器的notifyDataSetChanged()方法通知数据发生了变化
            adapter.notifyDataSetChanged();
            //ListView调用setSelection()方法，参数传入0，表示将第一项的数据放在ListView的第一个条目
            listView.setSelection(0);
            //设置当前列的选中级别为常量 LEVEL_PROVINCE = 0.
            currentLevel = LEVEL_PROVINCE;
        }else {
            //声明一个String赋值全国所有省接口的URL
            String address = "http://guolin.tech/api/china";
            //调用queryFromServer()方法从服务器上查询数据
            queryFromServer(address,"province");
        }
    }

    //查询选中省内所有的市，优先从数据库查询，如果数据库中没有查询到数据再去服务器上查询
    private void queryCities(){
        //TextView调用setText()方法设置头布局的文字，selectedProvince.getProvinceName()方法得到选中省的名字
        titleText.setText(selectedProvince.getProvinceName());
        //返回Button调用setVisibility()方法参数传入VIEW.VISIBLE将返回键显示出来
        backButton.setVisibility(View.VISIBLE);
        //LitePal.where()方法用于指定查询的约束条件，对应了SQL当作的where关键字
        //下面就是只查获取到的City数据表中的provinceid这一列等于选中省的id(selectedProvince.getId()方法得到选中省id)
        //find()方法指定具体哪一个数据表中查询数据表
        cityList = LitePal.where("provinceid = ?",
                String.valueOf(selectedProvince.getId())).find(City.class);
        //用来存放数据表中数组调用size()方法获取的长度大于0时，遍历数组中的数据
        if (cityList.size() > 0 ){
            //调用clear()方法清除掉List数组中的数据
            dataList.clear();
            //遍历List集合中Province数据表的数据
            for (City city : cityList){
                //将City数据表的市的名字这一列数据，通过City实体类的getCityName()方法获取
                //通过List数组调用add()方法将获取的数据放入List数组中
                dataList.add(city.getCityName());
            }
            //调用ArrayAdapter适配器的notifyDataSetChanged()方法通知数据发生了变化
            adapter.notifyDataSetChanged();
            //ListView调用setSelection()方法，参数传入0，表示将第一项的数据放在ListView的第一个条目
            listView.setSelection(0);
            //设置当前列的选中级别为常量 LEVEL_CITY = 1.
            currentLevel = LEVEL_CITY;
        }else {
            //通过Province选中的省份调用getProvinceCode()方法获取到省的代号
            //这个省的代号是请求市的接口URL组装的参数，并保存下来
            int provinceCode = selectedProvince.getProvinceCode();
            //声明一个String赋值所选中省下的所有市的接口URL，为全国省的接口加上选中省的代号
            String address = "http://guolin.tech/api/china/" + provinceCode;
            //调用queryFromServer()方法从服务器上查询数据
            queryFromServer(address,"city");
        }
    }

    //查询选中市内所有的县，优先从数据库查询，如果数据库没有查询到数据，再去服务器请求数据
    private void queryCounties(){
        //TextView调用setText()方法设置头布局的文字，seletedCity.getCityName()方法得到选中省的名字
        titleText.setText(seletedCity.getCityName());
        //返回Button调用setVisibility()方法参数传入VIEW.VISIBLE将返回键显示出来
        backButton.setVisibility(View.VISIBLE);
        //LitePal.where()方法用于指定查询的约束条件，对应了SQL当作的where关键字
        //下面就是只查获取到的County数据表中的cictyid这一列等于选中市的id(seletedCity.getId()方法得到选中市id)
        //find()方法指定具体哪一个数据表中查询数据表
        countyList = LitePal.where("cityid = ?",String.valueOf(seletedCity.getId()))
                .find(County.class);
        //用来存放数据表中数组调用size()方法获取的长度大于0时，遍历数组中的数据
        if (countyList.size() > 0){
            //调用clear()方法清除掉List数组中的数据
            dataList.clear();
            //遍历List集合中Province数据表的数据
            for (County county:countyList){
                //将County数据表的县的名字这一列数据，通过County实体类的getCountyName()方法获取
                //通过List数组调用add()方法将获取的数据放入List数组中
                dataList.add(county.getCountyName());
            }
            //调用ArrayAdapter适配器的notifyDataSetChanged()方法通知数据发生了变化
            adapter.notifyDataSetChanged();
            //ListView调用setSelection()方法，参数传入0，表示将第一项的数据放在ListView的第一个条目
            listView.setSelection(0);
            //设置当前列的选中级别为常量 LEVEL_COUNTY = 2.
            currentLevel = LEVEL_COUNTY;
        }else {
            //通过Province实体类变量selectedProvince(代表选中的省份)调用getProvinceCode()方法获取到省的代号
            //这个省的代号是请求市的接口URL组装的参数，并保存下来
            int provinceCode = selectedProvince.getProvinceCode();
            //通过CityCode实体类变量seletedCity(代表选中的城市)调用getCityCode()方法获取到市的代号
            //这个市的代号是请求市的接口URL组装的参数，并保存下来
            int cityCode = seletedCity.getCityCode();
            //声明一个String赋值查询某县的接口URL，为显示全国所有省的接口URL加上 选中省的代号 加到 选中市的代号
            String address = "http://guolin.tech/api/china/"+provinceCode+"/"+cityCode;
            //调用queryFromServer()方法从服务器上查询数据
            queryFromServer(address,"county");
        }
    }

    //根据传入的地址和类型从服务器上查询省市县的数据
    private void queryFromServer(String address , final String type){
        //进入queryFromServer()方法时就让dialog显示出来，代表请求服务器
        showProgressDialog();
        //调用类方法sendOkHtpRequest()来发送网络请求
        HttpUtil.sendOkHttpRequest(address, new okhttp3.Callback() {
            @Override
            //重写onFailure()方法对请求服务器发送异常状况时进行相应的处理
            public void onFailure(Call call, IOException e) {
                //当前活动的上下文调用runOnUiThread()方法将子线程切换成主线程
                //因为接下来调用的方法有更新UI的操作
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //请服务器失败时，调用closeProgressDialog()方法关闭dialog
                        closeProgressDialog();
                        //弹出toast提示
                        Toast.makeText(getContext(),"加载失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            //服务器返回的数据会回调到onResponse()方法中
            public void onResponse(Call call, Response response) throws IOException {
                //Response对象调用body()方法得到返回数据的具体内容，再调用string()方法将数据转换成字符串形式
                //最后声明一个Sting变量保存下来
                String responseText = response.body().string();
                boolean result = false;
                //判断调用queryFromServer()方法时，传入的第二个参数是否和我需要的参数一致，一致进入if代码块
                if ("province".equals(type)){
                    //调用类方法handleProvinceResponse()解析服务器返回的数据，
                    //在该方法的参数传入服务器返回数据的具体内容
                    //当服务器返回的数据不为null时，该方法返回true，并保存到数据库，用前面声明的布尔变量保存下来
                    result = Utility.handleProvinceResponse(responseText);
                //else if判断调用queryFromServer()方法时，传入的第二个参数是否和我需要的参数一致，一致进入if代码块
                }else if ("city".equals(type)){
                    //调用类方法handleCityResponse()解析服务器返回的数据，
                    //在该方法的参数传入服务器返回数据的具体内容
                    //当服务器返回的数据不为null时，该方法返回true，并保存到数据库，用前面声明的布尔变量保存下来
                    result = Utility.handleCityResponse(responseText,selectedProvince.getId());
                //else if判断调用queryFromServer()方法时，传入的第二个参数是否和我需要的参数一致，一致进入if代码块
                }else if ("county".equals(type)){
                    //调用类方法handleCountyResponse()解析服务器返回的数据，
                    //在该方法的参数传入服务器返回数据的具体内容
                    //当服务器返回的数据不为null时，该方法返回true，并保存到数据库，用前面声明的布尔变量保存下来
                    result = Utility.handleCountyResponse(responseText,seletedCity.getId());
                }
                //当布尔变量result变量为true时，进入if代码块
                if (result){
                    //当前活动的上下文调用runOnUiThread()方法将子线程切换成主线程
                    //因为接下来调用的几个查询方法都有更新UI的操作
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //调用closeProgressDialog()方法关闭dialog
                            closeProgressDialog();
                            //判断调用queryFromServer()方法时，传入的第二个参数是否和我需要的参数一致，
                            //一致进入if代码块
                            if ("province".equals(type)){
                                //调用queryProvinces()方法查询全国所有省的数据
                                //因为现在数据库中已经存储了数据，
                                queryProvinces();
                            //判断调用queryFromServer()方法时，传入的第二个参数是否和我需要的参数一致，
                            //一致进入if代码块
                            }else if ("city".equals(type)){
                                //调用queryCities()方法查询全国所有省的数据
                                //因为现在数据库中已经存储了数据，
                                queryCities();
                            //判断调用queryFromServer()方法时，传入的第二个参数是否和我需要的参数一致，
                            //一致进入if代码块
                            }else if ("county".equals(type)){
                                //调用queryCounties()方法查询全国所有省的数据
                                //因为现在数据库中已经存储了数据，
                                queryCounties();
                            }
                        }
                    });
                }
            }
        });
    }

    //showProgressDialog()显示进度对话框
    private void showProgressDialog(){
        //当ProgressDialog进度对话框变量为null时
        if (progressDialog == null){
            //构建ProgressDialog的实例，构造参数传入一个context对象
            progressDialog = new ProgressDialog(getActivity());
            //ProgressDialog的实例调用setMessage()方法给进度对话框设置内容
            progressDialog.setMessage("正在加载...");
            //ProgressDialog的实例调用setCanceledOnTouchOutside()方法，表示进度对话框出现后点击屏幕不消失
            //点击物理返回键dialog消失
            progressDialog.setCanceledOnTouchOutside(false);
        }
        //ProgressDialog的实例调用show（）方法将dialog显示出来
        progressDialog.show();
    }

    //关闭进度对话框dialog
    private void closeProgressDialog(){
        //当ProgressDialog进度对话框彼岸了不为null时
        if (progressDialog != null){
            ////ProgressDialog的实例调用dismiss()方法关闭进度对话框Dialog
            progressDialog.dismiss();
        }
    }
}
