package com.example.coolweather;

import android.app.ProgressDialog;
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

import com.example.coolweather.db.City;
import com.example.coolweather.db.County;
import com.example.coolweather.db.Province;

import java.util.ArrayList;
import java.util.List;

//创建一个选择区域碎片
public class ChooseAreaFragment extends Fragment {

    //静态常量
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
                //当点击ListView点击级别为LEVEL_PROVINCE常量时
                if (currentLevel == LEVEL_PROVINCE){
                    //代表省列表的List数组调用get()方法获取点击的位置，用Province变量保存下来
                    selectedProvince = provinceList.get(position);
                    //调用queryCities()方法查询选中省内所有的省
                    queryCities();
                }else if (currentLevel == LEVEL_CITY){
                    seletedCity = cityList.get(position);
                    queryCounties();
                }
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLevel == LEVEL_COUNTY){
                    queryCities();
                }else if (currentLevel == LEVEL_CITY){
                    queryProvinces();
                }
            }
        });
        queryProvinces();
    }

    private void queryProvinces(){

    }

    private void queryCities(){

    }

    private void queryCounties(){

    }
}
