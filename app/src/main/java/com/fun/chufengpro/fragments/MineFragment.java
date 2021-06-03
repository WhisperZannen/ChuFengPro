package com.fun.chufengpro.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.fun.chufengpro.R;
import com.fun.chufengpro.activity.LoginActivity;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * 这是“我的”主要页面
 */
public class MineFragment extends Fragment {

    private static final int LOGIN_RESULT = 1;

    private static final String ISLOGIN = "isLogin";
    private static final String USERNAME = "userName";
    private static final String USERID = "userId";

    // TODO: Rename and change types of parameters
    private boolean mIsLogin;
    private String mUserName;
    private int mUserId;
    private View viewRoot;
    private TextView loginTextView;
    private Button btn_login;
    private LinearLayout ll_logout;
    private SharedPreferences sp;
    private Handler handler;
    private LinearLayout lo_login;
    private LinearLayout lo_setting;

    public MineFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static MineFragment newInstance() {
        MineFragment fragment = new MineFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sp = getContext().getSharedPreferences("userInfo",Context.MODE_PRIVATE);
        if (sp.getString("userName","") != ""){
            mIsLogin = true;
            mUserName = sp.getString("userName","");
//            Toast.makeText(this.getContext(),"2.已登录" + mUserName,Toast.LENGTH_SHORT).show();
        }else {
            mIsLogin = false;
//            Toast.makeText(this.getContext(),"2.未登录",Toast.LENGTH_SHORT).show();

        }
        handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if (msg.what == 911){
                    mIsLogin = false;
                    changeTextView();
                }
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        viewRoot = inflater.inflate(R.layout.fragment_mine,container,false);
        loginTextView = viewRoot.findViewById(R.id.textView);
        btn_login = viewRoot.findViewById(R.id.button2);
        ll_logout = viewRoot.findViewById(R.id.lo_logout);
        lo_login = viewRoot.findViewById(R.id.lo_login);
        lo_setting = viewRoot.findViewById(R.id.lo_setting);

        changeTextView();

        ll_logout.setOnClickListener(view->{
            AlertDialog alertDialog = new AlertDialog.Builder(this.getContext())
                    .setTitle("退出")
                    .setMessage("亲，是否要退出吗？")
                    .setIcon(R.mipmap.pic)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            sp.edit().clear().commit();
                            Message message = new Message();
                            message.what = 911;
                            handler.sendMessage(message);
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .create();
                    alertDialog.show();
        });
        lo_setting.setOnClickListener(view->{
            Toast.makeText(getContext(),"开发中",Toast.LENGTH_SHORT).show();
//            Intent intent = new Intent();
//            intent.setClass(getContext(), TestActivity.class);
//            startActivity(intent);
        });

        return viewRoot;
    }

    private void changeTextView(){
        //        如果登录的话
        if (mIsLogin){
            loginTextView.setText(mUserName+"，您好");
            btn_login.setVisibility(GONE);
        }else {
            loginTextView.setText("还未登录，请登录");
            btn_login.setVisibility(VISIBLE);
            btn_login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    startActivityForResult(intent,LOGIN_RESULT);

                }
            });

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        if (requestCode == LOGIN_RESULT){
            if (resultCode == 200){
                SharedPreferences sp = getActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                btn_login.setVisibility(GONE);
                loginTextView.setText(sp.getString("userName","")+"，您好");
            }
        }
    }
}