package com.example.lurenman.photocrop.jokesgallery;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.lurenman.photocrop.BuildConfig;
import com.example.lurenman.photocrop.R;
import com.example.lurenman.photocrop.activity.BaseActivity;
import com.example.lurenman.photocrop.constant.Constants;
import com.example.lurenman.photocrop.constant.MsgConstants;
import com.example.lurenman.photocrop.utils.ImageUtils;
import com.example.lurenman.photocrop.utils.MessageEntity;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * @author: baiyang.
 * Created on 2017/12/11.
 */

public class JokesGalleryActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks {
    private static final String TAG = "JokesGalleryActivity";
    private Context mContext;
    private String mExternalFilesDirPath = "";
    private ImageView iv_back;
    private LinearLayout ll_rotation;
    private ImageView iv_rotation;
    private GridView gv_gridView;
    private Boolean roateFlag = false;
    private Util util;
    private List<String> mPathDatas = new ArrayList<>();//所有图片路径的集合
    private JokesImagesAdapter mJokesImagesAdapter;
    private FrameLayout fl_seleted_content;
    private ListView lv_seleted_listView;
    private JokesSeletedAdapter mJokesSeletedAdapter;
    private List<FileTraversal> locallist;
    private TextView tv_title;
    private int mCropCode = -1;

    @Override
    protected void initViews() {
        setContentView(R.layout.activity_jokes_gallery);
        mContext = this;
        iv_back = (ImageView) findViewById(R.id.iv_back);
        ll_rotation = (LinearLayout) findViewById(R.id.ll_rotation);
        iv_rotation = (ImageView) findViewById(R.id.iv_rotation);
        gv_gridView = (GridView) findViewById(R.id.gv_gridView);
        //隐藏显示的布局
        fl_seleted_content = (FrameLayout) findViewById(R.id.fl_seleted_content);
        lv_seleted_listView = (ListView) findViewById(R.id.lv_seleted_listView);
        tv_title = (TextView) findViewById(R.id.tv_title);
        initPermission();
    }

    @Override
    protected void initVariables() {
        mCropCode = getIntent().getIntExtra("crop", -1);
        mExternalFilesDirPath = getApplicationContext().getExternalFilesDir(null).getAbsolutePath();

    }

    private void initPermission() {
        //这块就申请写权限把报读权限错误 requires android.permission.READ_EXTERNAL_STORAGE
        if (!EasyPermissions.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            EasyPermissions.requestPermissions(this, "需要读写权限", 200, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        } else {
            initSomeing();
        }
    }

    private void initSomeing() {
        util = new Util(mContext);
        mPathDatas = util.listAlldir();// 获取全部图片地址
        mJokesImagesAdapter = new JokesImagesAdapter(mContext, mPathDatas);
        gv_gridView.setAdapter(mJokesImagesAdapter);
        //这块就是读取数据
        locallist = util.LocalImgFileList(mPathDatas);
        List<HashMap<String, String>> listdata = new ArrayList<HashMap<String, String>>();
        //这块把每个条目的第一图片取出来设给listView
        if (locallist != null) {
            //第一行添加所有图片选项
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("filecount", mPathDatas.size() + "");
            map.put("imgpath", locallist.get(0).filecontent.get(0) == null ? null :
                    (locallist.get(0).filecontent.get(0)));
            map.put("filename", "所有图片");
            listdata.add(map);
            for (int i = 0; i < locallist.size(); i++) {
                HashMap<String, String> map1 = new HashMap<String, String>();
                map1.put("filecount", locallist.get(i).filecontent.size() + "");
                map1.put("imgpath", locallist.get(i).filecontent.get(0) == null ? null :
                        (locallist.get(i).filecontent.get(0)));
                map1.put("filename", locallist.get(i).filename);
                listdata.add(map1);
            }
        }
        mJokesSeletedAdapter = new JokesSeletedAdapter(mContext, listdata);
        lv_seleted_listView.setAdapter(mJokesSeletedAdapter);
    }

    @Override
    protected void initEnvent() {
        super.initEnvent();
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ll_rotation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isVisibilityFlcontent();
            }
        });
        lv_seleted_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                isVisibilityFlcontent();
                if (position == 0) {
                    //所有图片选项
                    tv_title.setText("所有图片");
                    mJokesImagesAdapter.setData(mPathDatas);
                } else {
                    String filename = locallist.get(position - 1).filename;
                    tv_title.setText(filename);
                    List<String> pathUrl = locallist.get(position - 1).filecontent;
                    mJokesImagesAdapter.setData(pathUrl);
                }
            }
        });
        gv_gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    case SCROLL_STATE_FLING:
                        Log.i("ScrollListener", "onScrollStateChanged: " + SCROLL_STATE_FLING);
                        Glide.with(mContext).pauseRequests();//停止请求
                        break;
                    case SCROLL_STATE_IDLE:
                        Log.i("ScrollListener", "onScrollStateChanged: " + SCROLL_STATE_IDLE);
                        Glide.with(mContext).resumeRequests();//恢复请求
                        break;
                    case SCROLL_STATE_TOUCH_SCROLL:
                        Log.i("ScrollListener", "SCROLL_STATE_TOUCH_SCROLL: " + SCROLL_STATE_TOUCH_SCROLL);
                        Glide.with(mContext).pauseRequests();
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
        gv_gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                View v_path = (View) view.findViewById(R.id.v_path);
                String path = (String) v_path.getTag();
                Log.e(TAG, "onItemClick: path:" + path);
                if (mCropCode == Constants.PHOTO_CUT_FIRST) {
                    //此时是剪裁需求
                    cropPicture(path, 300);
                } else {

                    Bundle bundle = new Bundle();
                    bundle.putString("path", path);
                    Intent intent = new Intent(mContext, JokesDetialGalleryActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });
    }

    //23以下的可以用这个方法
    private void startPhotoZoom(Uri uri, int size) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", "true");

        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        // outputX,outputY 是剪裁图片的宽高
        intent.putExtra("outputX", size);
        intent.putExtra("outputY", size);
        intent.putExtra("return-data", true);

        startActivityForResult(intent, Constants.PHOTO_CUT_FIRST);
    }

    /**
     * 调用系统剪裁功能7.0的判断
     */
    public void cropPicture(String path, int size) {
        File file = new File(path);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        Uri imageUri;
        Uri outputUri;
        Intent intent = new Intent("com.android.camera.action.CROP");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            //通过FileProvider创建一个content类型的Uri
            imageUri = FileProvider.getUriForFile(mContext, BuildConfig.APPLICATION_ID + ".fileProvider", file);
            outputUri = Uri.fromFile(new File(path));
            //TODO:outputUri不需要ContentUri,否则失败
            //outputUri = FileProvider.getUriForFile(activity, "com.solux.furniture.fileprovider", new File(crop_image));
        } else {
            imageUri = Uri.fromFile(file);
            outputUri = Uri.fromFile(new File(path));
        }
        intent.setDataAndType(imageUri, "image/*");
        intent.putExtra("crop", "true");
        //设置宽高比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        //设置裁剪图片宽高
        intent.putExtra("outputX", size);
        intent.putExtra("outputY", size);
        intent.putExtra("scale", true);
        //发现这个写不写都没问题 看Result的方法
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);//直接输出文件
        intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
        intent.putExtra("noFaceDetection", true);//关闭人脸检测
        intent.putExtra("return-data", true);////是否返回数据
        startActivityForResult(intent, Constants.PHOTO_CUT_FIRST);
    }

    //是否显示那个选中的布局
    private void isVisibilityFlcontent() {
        roateFlag = !roateFlag;
        AnimationUtil.rotateArrow(iv_rotation, roateFlag);
        if (roateFlag) {
            fl_seleted_content.setVisibility(View.VISIBLE);
        } else {
            fl_seleted_content.setVisibility(View.GONE);
        }
    }

    @Override
    protected void loadData() {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        if (requestCode == 200) {
            if (perms.contains(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                initSomeing();
            }
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (requestCode == 200) {
            if (perms.contains(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(getApplicationContext(), "没有读写权限无法使用图库功能", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constants.PHOTO_CUT_FIRST:
                if (data != null) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        Bitmap photo = bundle.getParcelable("data");
                        //ImageUtils.savePhotoToSDCard(photo, mExternalFilesDirPath + "/Picture", System.currentTimeMillis() + "");
                        MessageEntity messageEntity = MessageEntity.obtianMessage();
                        messageEntity.what= MsgConstants.PHOTO_CUT_FIRST;
                        messageEntity.obj=photo;
                        EventBus.getDefault().post(messageEntity);
                        finish();
                    }
                }
                break;
            default:
                break;
        }
    }
}
