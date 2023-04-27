package com.example.novelreading;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

public class MyAdapter extends BaseAdapter {

    Context context;
    ImageLoader imageLoader;
    private LayoutInflater mInflater;
    List<HashMap<String,String>> list;
    public static String[] URLS;
    public MyAdapter(Context context, List<HashMap<String,String>> lists, String p)
    {
        this.context=context;
        mInflater = LayoutInflater.from(this.context);
        this.list=lists;
        imageLoader=new ImageLoader(p);
        URLS = new String[list.size()];
        for(int i=0;i<list.size();i++){
            URLS[i] = list.get(i).get("imgsrc");
        }
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            //创建新的view 对象
            viewHolder=new ViewHolder();
            convertView = mInflater.inflate(R.layout.book, null);
            viewHolder.imageView=(ImageView)convertView.findViewById(R.id.img2) ;
            viewHolder.booknames = (TextView) convertView.findViewById(R.id.bookname);
            viewHolder.completeness = (TextView) convertView.findViewById(R.id.completeness);
            viewHolder.details=(TextView)convertView.findViewById(R.id.details);
            viewHolder.author = (TextView) convertView.findViewById(R.id.author);
            viewHolder.type = (TextView) convertView.findViewById(R.id.type);
            viewHolder.starturl = (TextView) convertView.findViewById(R.id.starturl);
            convertView.setTag(viewHolder);
        }else{
            viewHolder=(ViewHolder)convertView.getTag();
        }
        //如何显示数据
        HashMap<String,String> hashMap=list.get(position);
        String url=list.get(position).get("imgsrc");
        viewHolder.imageView.setTag(url);
        imageLoader.loadImages2(viewHolder.imageView, url);
        viewHolder.booknames.setText(hashMap.get("bookname"));
        viewHolder.author.setText(hashMap.get("author"));
        viewHolder.completeness.setText(hashMap.get("completeness"));
        viewHolder.details.setText(hashMap.get("details"));
        viewHolder.starturl.setText(hashMap.get("url"));
        viewHolder.type.setText(hashMap.get("type"));

        return convertView;
    }

    class ViewHolder{
        ImageView imageView;
        TextView booknames;
        TextView completeness;
        TextView details;
        TextView author;
        TextView type;
        TextView starturl;
    }
}
