package com.fun.chufengpro.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.fun.chufengpro.R;
import com.fun.chufengpro.activity.PostActivity;
import com.fun.chufengpro.adapter.RecyclerVidewAdapter;
import com.fun.chufengpro.listener.EndlessRecyclerOnScrollListener;
import com.fun.chufengpro.pojo.Location;
import com.fun.chufengpro.pojo.MyMedia;
import com.fun.chufengpro.pojo.Post;
import com.fun.chufengpro.pojo.RecyclerViewItem;
import com.fun.chufengpro.pojo.ReturnData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.fun.chufengpro.constant.Constant.PAGE_LIMIT;
import static com.fun.chufengpro.constant.Constant.POST_GETPAGE;
import static com.fun.chufengpro.constant.Constant.SRC_IMGS;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ForumFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ForumFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    static int pageNum = 1;
    Button btn_goPost;

    // 下拉刷新控件
    SwipeRefreshLayout swipeRefreshLayout;

    // 数据展示
    RecyclerView recyclerView;

    public RecyclerVidewAdapter recyclerViewAdapter;
    private ArrayList<RecyclerViewItem> recyclerViewItemList = new ArrayList<>();

    private View viewRoot;
    private Handler handler;

    public ForumFragment() {
        // Required empty public constructor
    }


    public static ForumFragment newInstance(String param1, String param2) {
        ForumFragment fragment = new ForumFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 自定义数据
        loadMyDate(pageNum);
        handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (msg.what == 4) {
                    recyclerViewAdapter.notifyDataSetChanged();
                }
            }
        };
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (viewRoot == null) {
            viewRoot = inflater.inflate(R.layout.fragment_forum, container, false);
        }
        initView();
        btn_goPost = viewRoot.findViewById(R.id.btn_goPost);
        btn_goPost.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), PostActivity.class);
            startActivity(intent);
        });
        return viewRoot;
    }

    private void initView() {
        recyclerView = viewRoot.findViewById(R.id.recyclerView);
        // 布局管理器必须有，否则不显示布局
        // No layout manager attached; skipping layout
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        // RecyclerView适配器
        recyclerViewAdapter = new RecyclerVidewAdapter(this.getContext(), recyclerViewItemList);
        recyclerView.setAdapter(recyclerViewAdapter);

        // 下拉刷新控件
        // 因为该类 implements SwipeRefreshLayout.OnRefreshListener
        // 所以只需要在onCreate里注册一下监听器，具体的响应事件可以写到onCreate方法之外
        swipeRefreshLayout = viewRoot.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

        recyclerView.setOnScrollListener(new EndlessRecyclerOnScrollListener() {
            @Override
            public void onLoadMore() {
                loadMyDate(pageNum);
                handler.sendEmptyMessage(4);
            }
        });
    }
//    获取真实数据

    //    {"postId":"0605cc87-2efd-4084-86f2-3baad800293e","postUserId":0,
//    "postUser":"匿名","postContext":"你好","postImages":"null+20210601115538723.jpg",
//    "postTime":"2021-06-01 11:55:27"}
    private void loadMyDate(int nowPageNum) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                FormBody body = new FormBody.Builder()
                        .add("pageNum", nowPageNum + "")
                        .add("limit", PAGE_LIMIT).build();

                Request request = new Request.Builder()
                        .post(body)
                        .url(POST_GETPAGE).build();

                try {
                    Looper.prepare();
                    Response execute = client.newCall(request).execute();
                    ReturnData returnData = JSON.parseObject(execute.body().string(), ReturnData.class);
                    if (returnData.getCode() != 50001) {
                        List<Post> posts = JSONArray.parseArray(returnData.getData() + "", Post.class);


                        for (int i = 0; i < posts.size(); i++) {
                            ArrayList<MyMedia> list = new ArrayList<>();
                            if (!(posts.get(i).getPostImages() == null || posts.get(i).getPostImages().length() <= 0)) {
                                String postImages = posts.get(i).getPostImages();
                                String[] split = postImages.split("\\+");
                                for (int j = 1; j < split.length; j++) {
                                    MyMedia myMedia = new MyMedia(SRC_IMGS + split[j]);
                                    list.add(myMedia);
                                }
                                recyclerViewItemList.add(new RecyclerViewItem(list,
                                        posts.get(i).getPostContext(), posts.get(i).getPostTime(),
                                        posts.get(i).getPostId(), posts.get(i).getPostUser(),
                                        new Location(), "https://i0.hdslb.com/bfs/album/0b6e13b1028b9a7426990034488b4af04b54c719.png"));
                            }else {
                                recyclerViewItemList.add(new RecyclerViewItem(null,
                                        posts.get(i).getPostContext(), posts.get(i).getPostTime(),
                                        posts.get(i).getPostId(), posts.get(i).getPostUser(),
                                        new Location(), "https://i0.hdslb.com/bfs/album/0b6e13b1028b9a7426990034488b4af04b54c719.png"));
                            }


                        }
                        handler.sendEmptyMessage(4);
                        pageNum++;
                    } else {
                        Toast.makeText(getContext(), "已经到底啦", Toast.LENGTH_SHORT).show();
                    }
                    Looper.loop();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        }).start();
    }

    // 自定义的测试数据（假装这是网络请求并解析后的数据）
//    ?pageNum=1&limit=10
    private void loadMyTestDate() {
        // 先构造MyMedia
        String imgUrl1 = "https://i0.hdslb.com/bfs/album/0b6e13b1028b9a7426990034488b4af04b54c719.png";
        String imgUrl2 = "https://i0.hdslb.com/bfs/album/7db905515628e6c18d8a61f4369a505f1ab0dec2.jpg";
//        String imgUrl2 = "http://192.168.137.57:8005/img/20210531172014644.jpg";
        String imgUrl3 = "https://i0.hdslb.com/bfs/album/f26eba49f3a8c8fc394f629aba27c7e1da812698.png";
        // 视频内容：敲架子鼓
        String videoUrl1 = "http://jzvd.nathen.cn/c6e3dc12a1154626b3476d9bf3bd7266/6b56c5f0dc31428083757a45764763b0-5287d2089db37e62345123a1be272f8b.mp4";
        // 视频内容：感受到鸭力
        String videoUrl2 = "http://gslb.miaopai.com/stream/w95S1LIlrb4Hi4zGbAtC4TYx0ta4BVKr-PXjuw__.mp4?vend=miaopai&ssig=8f20ca2d86ec365f0f777b769184f8aa&time_stamp=1574944581588&mpflag=32&unique_id=1574940981591448";
        // 视频内容：狗崽子
        String videoUrl4 = "http://gslb.miaopai.com/stream/7-5Q7kCzeec9tu~9XvZAxNizNAL1TJC7KtJCuw__.mp4?vend=miaopai&ssig=82b42debfc2a51569bafe6ac7a993d89&time_stamp=1574944868488&mpflag=32&unique_id=1574940981591448";
        String videoUrl3 = videoUrl4;

        MyMedia myMedia1 = new MyMedia(imgUrl1, videoUrl1);
        MyMedia myMedia2 = new MyMedia(imgUrl2);
        MyMedia myMedia3 = new MyMedia(imgUrl3, videoUrl2);
        MyMedia myMedia4 = new MyMedia(imgUrl1, videoUrl3);
        MyMedia myMedia5 = new MyMedia(imgUrl3, videoUrl4);
        // 再构造mediaList
        // 1张图片
        ArrayList<MyMedia> mediaList1 = new ArrayList<>();
        mediaList1.add(myMedia2);
        // 2张图片
        ArrayList<MyMedia> mediaList2 = new ArrayList<>();
        mediaList2.add(myMedia1);
        mediaList2.add(myMedia2);
        // 4张图片
        ArrayList<MyMedia> mediaList4 = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            mediaList4.add(myMedia1);
            mediaList4.add(myMedia2);
        }
        // 10张图片
        ArrayList<MyMedia> mediaList10 = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            mediaList10.add(myMedia1);
            mediaList10.add(myMedia2);
            mediaList10.add(myMedia3);
            mediaList10.add(myMedia4);
            mediaList10.add(myMedia5);
        }

        Location location = new Location();
        location.setAddress("Test Address");
        // 最后构造EvaluationItem
        final RecyclerViewItem recyclerViewItem1 = new RecyclerViewItem(mediaList1, "河北经贸大学自强社是在校学生处指导、学生资助管理中心主办下，于2008年4月15日注册成立的，一个以在校学生为主体的学生公益社团。历经十年的发展，在学生处、学生资助管理中心的大力支持下，在每一届自强人的团结努力下，自强社已经由成... ", "2019-11-02",
                "10080", "自强社", location, imgUrl1);
        final RecyclerViewItem recyclerViewItem2 = new RecyclerViewItem(mediaList2, "河北经贸大学信息技术学院成立于1996年，由原计算机系/经济信息系合并组建而成，是我校建设的第一批学院。", "2019-11-02",
                "10080", "信息技术学院", location, imgUrl2);
        final RecyclerViewItem recyclerViewItem4 = new RecyclerViewItem(mediaList4, "河北经贸大学信息技术学院成立于1996年，由原计算机系/经济信息系合并组建而成，是我校建设的第一批学院。", "2019-11-02",
                "10080", "信息技术学院", location, imgUrl2);
        final RecyclerViewItem recyclerViewItem10 = new RecyclerViewItem(mediaList10, "河北经贸大学雷雨话剧社是河北经贸大学唯一以话剧为主，兼小品，相声等多种表演艺术形式，由一批热爱表演，热爱话剧，热爱中国传统艺术与当代流行艺术结合的同学共同组成的文艺类大型社团。雷雨话剧社坚持以追求话剧“更新颖”、“更大型”、“更专业”为奋斗目标，坚持在继承传统文化和前辈的演出经验... ", "2019-11-02",
                "10080", "雷雨话剧社", location, imgUrl3);
        recyclerViewItemList.add(recyclerViewItem1);
        recyclerViewItemList.add(recyclerViewItem2);
        recyclerViewItemList.add(recyclerViewItem4);
        recyclerViewItemList.add(recyclerViewItem10);
    }

    @Override
    public void onRefresh() {
//        // 加载数据（先清空原来的数据）
//        recyclerViewItemList.clear();
//        // loadBackendData(url);
//        loadMyTestDate();
//        // 打乱顺序（为了确认确实是刷新了）
//        Collections.shuffle(recyclerViewItemList);
//        // 通知适配器数据已经改变
//        recyclerViewAdapter.notifyDataSetChanged();
//        // 下拉刷新完成
//        if (swipeRefreshLayout.isRefreshing()) {
//            swipeRefreshLayout.setRefreshing(false);
//        }

        recyclerViewItemList.clear();
        pageNum = 1;
        loadMyDate(pageNum);
        handler.sendEmptyMessage(4);
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }
}