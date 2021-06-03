package com.fun.chufengpro.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSON;
import com.fun.chufengpro.R;
import com.fun.chufengpro.constant.Constant;
import com.fun.chufengpro.pojo.ReturnData;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.fun.chufengpro.util.setWindowColor.setWindowStatusBarColor;

public class RegisterActivity extends Activity {
    private static final String TAG = "RegisterActivity";
    EditText et_username2;
    EditText et_password2;
    EditText et_config_pwd2;
    EditText et_phone2;
    Button btn_signIn2;
    Button btn_cancel2;
    private static Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setWindowStatusBarColor(this, R.color.Home_bar);
        setContentView(R.layout.activity_register);

        initView();
        initListener();
        handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                Log.d(TAG, "handleMessage: " + msg.obj);
                if (msg.what == 2){
                    if (msg.obj.toString().equals("true")){
                        Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                        finish();
                    }else {
                        Toast.makeText(RegisterActivity.this, "注册失败", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };
    }


    private void initListener() {
        btn_signIn2.setOnClickListener(view -> {
            if (et_username2.getText().toString().trim().length() < 1 || et_username2.getText().toString().trim().length() > 10 ||
                    et_password2.getText().toString().trim().length() < 6 || et_password2.getText().toString().trim().length() > 20 ||
                    et_phone2.getText().toString().trim().length() != 11||
                    !et_password2.getText().toString().trim().equals( et_config_pwd2.getText().toString().trim())) {
                Log.d(TAG, "initListener: 用户名长度判断：" + et_username2.getText().toString().trim().length());
                Log.d(TAG, "initListener: 密码长度判断：" + et_password2.getText().toString().trim().length());
                Log.d(TAG, "initListener: 手机号长度判断：" + et_phone2.getText().toString().trim().length());
                Log.d(TAG, "initListener: 相通性判断"+ et_password2.getText().toString().trim().equals( et_config_pwd2.getText().toString().trim()));
                Toast.makeText(this, "请输入正确内容 用户名1-10位，密码6-20位，手机号11位", Toast.LENGTH_LONG).show();
            } else {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        OkHttpClient client = new OkHttpClient();
                        FormBody body = new FormBody.Builder()
                                .add("userName", et_username2.getText().toString().trim())
                                .add("userPassword", et_password2.getText().toString().trim())
                                .add("userPhone", et_phone2.getText().toString().trim())
                                .build();
                        Request request = new Request.Builder().url(Constant.USER_SIGNUP).post(body).build();

                        try {
                            Looper.prepare();
                            Response response = client.newCall(request).execute();
//                             User user = JSON.parseObject(response.body().string(), User.class);
                            ReturnData r = JSON.parseObject(response.body().string(), ReturnData.class);
                            Log.d(TAG, "返回结果: " + r.toString() + "以及" + r);
                            if (("true").equals(r.getData().toString())) {
                                Log.d(TAG, "注册成功 ");
                                Message message = new Message();
                                message.what = 2;
                                message.obj = true;
                                handler.sendMessage(message);
                            } else {
                                Log.d(TAG, "注册失败 ");
                                Message message = new Message();
                                message.what = 2;
                                message.obj = false;
                                handler.sendMessage(message);
                            }
                            Looper.loop();
                        } catch (IOException e) {
                            Log.d(TAG, "run: 出现问题");
                            e.printStackTrace();
                        }
                    }
                }).start();

            }
        });
        btn_cancel2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                finish();
            }
        });
    }

    private void initView() {
        et_username2 = findViewById(R.id.et_username2);
        et_config_pwd2 = findViewById(R.id.et_config_pwd2);
        et_password2 = findViewById(R.id.et_password2);
        et_phone2 = findViewById(R.id.et_phone2);
        btn_signIn2 = findViewById(R.id.btn_register2);
        btn_cancel2 = findViewById(R.id.btn_cancel2);
    }
}