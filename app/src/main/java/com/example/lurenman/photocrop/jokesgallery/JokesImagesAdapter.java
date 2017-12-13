package com.example.lurenman.photocrop.jokesgallery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.lurenman.photocrop.R;
import java.util.List;

/**
 * @author: baiyang.
 * Created on 2017/12/11.
 */

public class JokesImagesAdapter extends BaseAdapter {
    private Context mContext;
    private List<String> mDatas;

    public JokesImagesAdapter(Context context, List<String> data) {
        this.mContext = context;
        this.mDatas = data;
    }

    @Override
    public int getCount() {
        return null==mDatas?0:mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(this.mContext).inflate(R.layout.jokes_grid_item, parent, false);
            holder.iv_img=(ImageView) convertView.findViewById(R.id.iv_img);
            holder.v_path=(View) convertView.findViewById(R.id.v_path);

            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.v_path.setTag(mDatas.get(position));
        Glide.with(mContext).load(mDatas.get(position))
                .asBitmap()
                .placeholder(R.mipmap.imgbg)//占位图
                .error(R.mipmap.icon_errorimg)//设置错误图
                .diskCacheStrategy(DiskCacheStrategy.NONE)//不缓存图片
                .into(holder.iv_img);
        return convertView;
    }
    public void setData(List<String> data) {
        this.mDatas = data;
        this.notifyDataSetChanged();
    }
    private class ViewHolder {
        private ImageView iv_img;
        private View v_path;//设置tag用的
    }
}
