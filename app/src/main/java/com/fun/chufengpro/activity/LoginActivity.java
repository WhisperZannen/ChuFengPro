package com.fun.chufengpro.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.fun.chufengpro.R;
import com.fun.chufengpro.constant.Constant;
import com.fun.chufengpro.pojo.User;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.fun.chufengpro.util.setWindowColor.setWindowStatusBarColor;

/**
 * 安全隐患：
 * 接口未隐藏
 * 返回值JSon数据返回了所有的信息，包括明文密码
 */
public class LoginActivity extends Activity {
    private static final String TAG = "LoginActivity";
    protected EditText et_username;
    protected EditText et_password;
    protected Button btn_login;
    protected Button btn_signIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setWindowStatusBarColor(LoginActivity.this, R.color.Home_bar);
        setContentView(R.layout.activity_login);

        initView();
        initListener();

    }

    private void initView() {
        btn_login = findViewById(R.id.btn_login);
        et_username = findViewById(R.id.et_username);
        et_password = findViewById(R.id.et_password);
        btn_signIn = findViewById(R.id.btn_signin);
    }

    private void initListener() {
        btn_signIn.setOnClickListener(view -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        });
        btn_login.setOnClickListener(view -> {
            if ("".equals(et_username.toString().trim()) || "".equals(et_password.toString().trim())) {
                Toast.makeText(this, "请输入账号或者密码哟", Toast.LENGTH_SHORT).show();
            } else {
                new Thread(new Runnable() {

                    private Response response;

                    @Override
                    public void run() {
                        OkHttpClient okHttpClient = new OkHttpClient();

                        FormBody body = new FormBody.Builder()
                                .add("username", et_username.getText().toString())
                                .add("password", et_password.getText().toString())
                                .build();

                        Request request = new Request.Builder().
                                url(Constant.USER_LOGIN).post(body).build();

                        try {
                            Looper.prepare();

                            response = okHttpClient.newCall(request).execute();
                            User user = JSON.parseObject(response.body().string(), User.class);
                            Log.d(TAG, "返回结果: " + user.toString());
//                            List<User> user = JSONArray.parseArray(response.body().string(), User.class);
                            if (user != null) {
                                Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_LONG).show();
                                SharedPreferences sp = getSharedPreferences("userInfo", MODE_PRIVATE);
                                SharedPreferences.Editor edit = sp.edit();
                                edit.putString("userName", user.getUserName());
                                edit.putInt("userId", user.getUserId());
                                edit.putString("userPhone", user.getUserPhone());
                                edit.putInt("userRole", user.getUserRole());
                                edit.commit();
                                setResult(200);
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                            }
                            Looper.loop();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                    }
                }).start();
            }
        });
    }
}