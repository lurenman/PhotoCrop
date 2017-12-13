package com.example.lurenman.photocrop;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.lurenman.photocrop.constant.MsgConstants;
import com.example.lurenman.photocrop.jokesgallery.JokesGalleryActivity;
import com.example.lurenman.photocrop.constant.Constants;
import com.example.lurenman.photocrop.utils.MessageEntity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MainActivity extends AppCompatActivity {
    private Context mContext;
    private Button bt_crop_first;
    private ImageView iv_imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext=this;
        EventBus.getDefault().register(this);
        initViews();
        initEvents();
    }

    private void initViews() {
        bt_crop_first = (Button) findViewById(R.id.bt_crop_first);
        iv_imageView = (ImageView) findViewById(R.id.iv_imageView);
    }

    private void initEvents() {
        bt_crop_first.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, JokesGalleryActivity.class);
                intent.putExtra("crop", Constants.PHOTO_CUT_FIRST);
                startActivity(intent);
            }
        });
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void upDateImageView(MessageEntity entity)
    {
        if (entity.what== MsgConstants.PHOTO_CUT_FIRST)
        {
            Bitmap photo = (Bitmap) entity.obj;
            if (null!=photo)
            {
              iv_imageView.setImageBitmap(photo);
            }
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
