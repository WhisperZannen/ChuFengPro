package com.fun.chufengpro.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.fun.chufengpro.R;
import com.zhouwei.mzbanner.MZBannerView;
import com.zhouwei.mzbanner.holder.MZHolderCreator;
import com.zhouwei.mzbanner.holder.MZViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * 这个是主页
 */
public class MainpageFragment extends Fragment {

    private static final String USERID = "userid";

    private int mUserId;
    private View viewRoot;
    private List<Integer> list = new ArrayList<Integer>();
    private MZBannerView mzBannerView;

    public MainpageFragment() {
    }

    @Override
    public void onResume() {
        super.onResume();
        mzBannerView.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        mzBannerView.pause();
    }

    public static MainpageFragment newInstance(int userId) {
        MainpageFragment fragment = new MainpageFragment();
        Bundle args = new Bundle();
        args.putInt(USERID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUserId = getArguments().getInt(USERID);
        }
        list.add(R.drawable.a);
        list.add(R.drawable.b);
        list.add(R.drawable.c);
        list.add(R.drawable.d);
        list.add(R.drawable.e);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (viewRoot == null){
            viewRoot = inflater.inflate(R.layout.fragment_mainpage,container,false);
        }

        mzBannerView = viewRoot.findViewById(R.id.mainPage_banner);
        mzBannerView.setPages(list, new MZHolderCreator<BannerViewHolder>(){
            @Override
            public BannerViewHolder createViewHolder() {
                return new BannerViewHolder();
            }
        });
        return viewRoot;
    }
    public static class BannerViewHolder implements MZViewHolder<Integer> {
        private ImageView mImageView;
        @Override
        public View createView(Context context) {
            // 返回页面布局
            View view = LayoutInflater.from(context).inflate(R.layout.banner_item,null);
            mImageView = (ImageView) view.findViewById(R.id.banner_image);
            return view;
        }

        @Override
        public void onBind(Context context, int position, Integer data) {
            // 数据绑定
            mImageView.setImageResource(data);
        }
    }
}