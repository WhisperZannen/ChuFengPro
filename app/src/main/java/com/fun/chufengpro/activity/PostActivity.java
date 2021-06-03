package com.fun.chufengpro.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.fun.chufengpro.R;
import com.fun.chufengpro.activity.basic.BaseActivity;
import com.fun.chufengpro.adapter.NineGridAdapter;
import com.fun.chufengpro.listener.OnAddPicturesListener;
import com.fun.chufengpro.pojo.ReturnData;
import com.giftedcat.picture.lib.selector.MultiImageSelector;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static com.fun.chufengpro.constant.Constant.UPLOAD_CONTEXT;
import static com.fun.chufengpro.constant.Constant.UPLOAD_IMG;
import static com.fun.chufengpro.util.XchClientUploadUtils.upload;


public class PostActivity extends BaseActivity {
    private static final String TAG = PostActivity.class.getName();

    private static final int REQUEST_IMAGE = 2;
    private int maxNum = 9;

    Unbinder unbinder;

    @BindView(R.id.rv_images)
    RecyclerView rvImages;

    NineGridAdapter adapter;

    Boolean isPost = false;

    //    上传的图片List列表
    List<String> mSelectList;

    @BindView(R.id.btn_post)
    Button btn_post;

//    朋友圈内容内容
    @BindView(R.id.et_post)
    EditText et_post;
    private Handler handler;
    private UUID uuid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        unbinder = ButterKnife.bind(this);

        mSelectList = new ArrayList<>();
        initView();
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 3){
                    postImgs();
                }
            }
        };
    }

    private void postImgs() {
            //            上传文件的
            Log.d(TAG, "initView: 图片开始上传");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Looper.prepare();
                        Toast.makeText(context,"请稍后，文件上传中",Toast.LENGTH_LONG).show();
                        for (int i = 0; i < mSelectList.size() - 1; i++) {
                            File file = new File(mSelectList.get(i));
                            Log.d(TAG, "initView: 文件：" + file);
                            ResponseBody upload = upload(UPLOAD_IMG, file + "", UUID.randomUUID() + ".jpg",uuid);
                            Log.d(TAG, "上传文件结果 " + upload);
                        }
                        Toast.makeText(context,"发布成功",Toast.LENGTH_SHORT).show();
                        finish();
                        Looper.loop();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //                    QuietOkHttp.postFile(UPLOAD_IMG)
                    //                            .uploadFile("File",file)
                    //                            .execute(new StringCallBack() {
                    //                                @Override
                    //                                protected void onSuccess(Call call, String response) {
                    //
                    //                                }
                    //
                    //                                @Override
                    //                                public void onFailure(Call call, Exception e) {
                    //
                    //                                }
                    //                            });

                }
            }).start();

    }

    private void initView() {
        rvImages.setLayoutManager(new GridLayoutManager(this, 3));
        adapter = new NineGridAdapter(PostActivity.this, mSelectList, rvImages);
        adapter.setMaxSize(maxNum);
        rvImages.setAdapter(adapter);
        adapter.setOnAddPicturesListener(new OnAddPicturesListener() {
            @Override
            public void onAdd() {
                pickImage();
            }
        });
        btn_post.setOnClickListener(view -> {
//            if (mSelectList.size() > 1 || !et_post.getText().toString().trim().equals("") ){
            if (mSelectList.size() > 1){
                Log.d(TAG, "initView: mSelectList.size()" + mSelectList.size() + et_post.getText().toString().trim().equals("") );
                uuid = UUID.randomUUID();
                Log.d(TAG, "initView: 生成一个UUID" + uuid);
                SharedPreferences preferences = getSharedPreferences("userInfo",MODE_PRIVATE);
                String userName = preferences.getString("userName", "匿名");
                int userId = preferences.getInt("userId", 0);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        OkHttpClient client = new OkHttpClient();
                        FormBody formBody = new FormBody.Builder()
                                .add("uuid", String.valueOf(uuid))
                                .add("userName",userName)
                                .add("userId",userId+"")
                                .add("postContext",et_post.getText().toString()).build();
                        Request request = new Request.Builder().url(UPLOAD_CONTEXT)
                                .post(formBody).build();

                        try {
                            Looper.prepare();
                            Response execute = client.newCall(request).execute();
                            ReturnData returnData = JSON.parseObject(execute.body().string(), ReturnData.class);
                            Log.d(TAG, "run: " + returnData + "结果：" + returnData.getData().toString().equals("true"));
                            if (returnData.getData().toString().equals("true")){
                                isPost = true;
                                handler.sendEmptyMessage(3);
                            }else {
                                Toast.makeText(context,returnData.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                            Looper.loop();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
        }else {
                Toast.makeText(context,"请至少上传一张图片哟",Toast.LENGTH_SHORT).show();
            }
                }
        );
    }

    /**
     * 选择需添加的图片
     */
    private void pickImage() {
        MultiImageSelector selector = MultiImageSelector.create(context);
        selector.showCamera(false);
        selector.count(maxNum);
        selector.multi();
        selector.origin(mSelectList);
        selector.start(instans, REQUEST_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == RESULT_OK) {
                List<String> select = data.getStringArrayListExtra(MultiImageSelector.EXTRA_RESULT);
                mSelectList.clear();
                mSelectList.addAll(select);
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unbinder.unbind();
    }
}
