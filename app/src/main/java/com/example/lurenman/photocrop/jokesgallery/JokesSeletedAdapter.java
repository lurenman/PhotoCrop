package com.example.lurenman.photocrop.jokesgallery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.lurenman.photocrop.R;
import java.util.HashMap;
import java.util.List;

/**
 * @author: baiyang.
 * Created on 2017/12/11.
 */

public class JokesSeletedAdapter extends BaseAdapter {
    Context context;
    String filecount = "filecount";
    String filename = "filename";
    String imgpath = "imgpath";
    List<HashMap<String, String>> listdata;
    public JokesSeletedAdapter(Context context, List<HashMap<String, String>> listdata) {
        this.context = context;
        this.listdata = listdata;
    }

    @Override
    public int getCount() {
        return listdata.size();
    }

    @Override
    public Object getItem(int position) {
        return listdata.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (convertView == null) {
            holder = new Holder();
            convertView = LayoutInflater.from(context).inflate(R.layout.jokes_seleted_lv_item, null);
            holder.iv_imgview = (ImageView) convertView.findViewById(R.id.iv_imgview);
            holder.tv_fileneme = (TextView) convertView.findViewById(R.id.tv_fileneme);
            holder.tv_filenums = (TextView) convertView.findViewById(R.id.tv_filenums);
            convertView.setTag(holder);

        } else {
            holder = (Holder) convertView.getTag();
        }

        holder.tv_fileneme.setText(listdata.get(position).get(filename));
        holder.tv_filenums.setText("("+listdata.get(position).get(filecount)+")");

        Glide.with(context).load(listdata.get(position).get(imgpath))
                .asBitmap()
                .placeholder(R.mipmap.imgbg)//占位图
                .error(R.mipmap.icon_errorimg)//设置错误图
                .diskCacheStrategy(DiskCacheStrategy.NONE)//不缓存图片
                .into(holder.iv_imgview);
        return convertView;
    }

    private class Holder {
        ImageView iv_imgview;
        TextView tv_fileneme;//文件名称
        TextView tv_filenums;//文件数量
    }
}
