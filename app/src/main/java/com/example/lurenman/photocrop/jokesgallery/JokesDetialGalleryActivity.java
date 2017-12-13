package com.example.lurenman.photocrop.jokesgallery;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.lurenman.photocrop.R;
import com.example.lurenman.photocrop.activity.BaseActivity;
import com.github.chrisbanes.photoview.OnPhotoTapListener;
import com.github.chrisbanes.photoview.PhotoView;

/**
 * @author: baiyang.
 * Created on 2017/12/12.
 */

public class JokesDetialGalleryActivity extends BaseActivity {
    private PhotoView mIvPic;
    private String mSdPath = "";

    @Override
    protected void initViews() {
        setContentView(R.layout.activity_jokesdetial_gallery);
        mIvPic = (PhotoView) findViewById(R.id.pv_pic);
    }

    @Override
    protected void initVariables() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            mSdPath = bundle.getString("path", "");
            Glide.with(mContext).load(mSdPath)
                    .asBitmap()
                    .placeholder(R.mipmap.imgbg)//占位图
                    .error(R.mipmap.icon_errorimg)//设置错误图
                    //.crossFade()//动画效果
                    .diskCacheStrategy(DiskCacheStrategy.NONE)//不缓存图片
                    .into(mIvPic);
        }
    }

    @Override
    protected void initEnvent() {
        super.initEnvent();
        mIvPic.setOnPhotoTapListener(new OnPhotoTapListener() {
            @Override
            public void onPhotoTap(ImageView view, float x, float y) {
                JokesDetialGalleryActivity.this.finish();
            }
        });
    }

    @Override
    protected void loadData() {

    }
}
