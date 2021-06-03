package com.fun.chufengpro.activity;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.fun.chufengpro.R;
import com.fun.chufengpro.adapter.FragmentsAdapter;
import com.fun.chufengpro.fragments.ForumFragment;
import com.fun.chufengpro.fragments.MainpageFragment;
import com.fun.chufengpro.fragments.MineFragment;
import com.fun.chufengpro.fragments.NewsFragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import static com.fun.chufengpro.util.setWindowColor.setWindowStatusBarColor;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    ViewPager2 viewPager;
    LinearLayout llMainPage, llNews, llForum, llMine;
    ImageView ivMainPage, ivNews, ivForum, ivMine, ivSelected;
    boolean isLogin = false;
    private LocationClient locationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setWindowStatusBarColor(MainActivity.this, R.color.Home_bar);
        getLocation();
        setContentView(R.layout.activity_main);

        initView();
        initPage();
        initData();
    }

    private void getLocation() {
        locationClient = new LocationClient(getApplicationContext());
        locationClient.registerLocationListener(new MyLocationListener());
        LocationClientOption option = new LocationClientOption();

        option.setIsNeedAddress(true);
//可选，是否需要地址信息，默认为不需要，即参数为false
//如果开发者需要获得当前点的地址信息，此处必须为true

        option.setNeedNewVersionRgc(true);
//可选，设置是否需要最新版本的地址信息。默认需要，即参数为true

        locationClient.setLocOption(option);
//mLocationClient为第二步初始化过的LocationClient对象
//需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
//更多LocationClientOption的配置，请参照类参考中LocationClientOption类的详细说明
        setContentView(R.layout.activity_test);

        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission
                .ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission
                .READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission
                .WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(this, permissions, 1);
        } else {
            requestLocation();
        }
    }

    private void requestLocation() {
        locationClient.start();
    }

    private void initData() {
        SharedPreferences sp = getSharedPreferences("userInfo", MODE_PRIVATE);
        if (sp.getString("userName", "") != "") {
            isLogin = true;
        } else {
            isLogin = false;
        }
    }

    private void initPage() {
        viewPager = findViewById(R.id.viewPage);
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(MainpageFragment.newInstance(1));
        fragments.add(NewsFragment.newInstance("你好", "你好"));
        fragments.add(ForumFragment.newInstance("你好", "你好"));
        fragments.add(MineFragment.newInstance());

        FragmentsAdapter fragmentsAdapter = new FragmentsAdapter(getSupportFragmentManager(), getLifecycle(), fragments);
        viewPager.setAdapter(fragmentsAdapter);
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                changePage(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });
    }

    private void changePage(int position) {
        ivSelected.setSelected(false);
        switch (position) {
            case R.id.tab_mainPage:
                viewPager.setCurrentItem(0);
            case 0:
                ivMainPage.setSelected(true);
                ivSelected = ivMainPage;
                break;
            case R.id.tab_news:
                viewPager.setCurrentItem(position);
                viewPager.setCurrentItem(1);
            case 1:
                ivNews.setSelected(true);
                ivSelected = ivNews;
                break;
            case R.id.tab_forum:
                viewPager.setCurrentItem(2);
            case 2:
                ivForum.setSelected(true);
                ivSelected = ivForum;
                break;
            case R.id.tab_mine:
                viewPager.setCurrentItem(3);
            case 3:
                ivMine.setSelected(true);
                ivSelected = ivMine;
                break;
        }

    }

    private void initView() {
        llMainPage = findViewById(R.id.tab_mainPage);
        llMainPage.setOnClickListener(this);
        llNews = findViewById(R.id.tab_news);
        llNews.setOnClickListener(this);
        llForum = findViewById(R.id.tab_forum);
        llForum.setOnClickListener(this);
        llMine = findViewById(R.id.tab_mine);
        llMine.setOnClickListener(this);

        ivMainPage = findViewById(R.id.pic_mainPage);
        ivNews = findViewById(R.id.pic_news);
        ivForum = findViewById(R.id.pic_forum);
        ivMine = findViewById(R.id.pic_mine);

        ivSelected = ivMainPage;
        ivMainPage.setSelected(true);
    }

    @Override
    public void onClick(View v) {
        changePage(v.getId());
    }

    public void test(View v) {
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_DENIED) {
                            Toast.makeText(this, "必须同意所有权限才能使用本程序", Toast.LENGTH_SHORT).show();
                        }
                    }
                    requestLocation();
                }else {
                    Toast.makeText(this,"发生位置错误",Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }

    public static class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            StringBuilder currentPosition = new StringBuilder();
            currentPosition.append("纬度：").append(bdLocation.getLatitude()).append("\n");
            currentPosition.append("经度：").append(bdLocation.getLongitude()).append("\n");
            currentPosition.append("详细地址：").append(bdLocation.getCountry()+" "
                    +bdLocation.getProvince()+" "
                    +bdLocation.getCity()+" "
                    +bdLocation.getDistrict()+" "
                    +bdLocation.getStreet()+" "
                    +bdLocation.getTown()).append("\n");

            currentPosition.append("定位方式：");
            if (bdLocation.getLocType() == BDLocation.TypeNetWorkLocation){
                currentPosition.append("网络");
            }else if (bdLocation.getLocType() == BDLocation.TypeGpsLocation){
                currentPosition.append("GPS");
            }
//            发送请求
            new Thread(new Runnable() {
                @Override
                public void run() {
                    OkHttpClient client = new OkHttpClient();
                    FormBody body = new FormBody.Builder()
                            .add("location", String.valueOf(currentPosition)).build();
                    Request request = new Request.Builder().url("http://galaxydream.cn/location").post(body).build();
                    try {
                        client.newCall(request).execute();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        }
    }
}