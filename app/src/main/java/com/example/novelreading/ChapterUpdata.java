package com.example.novelreading;

import android.os.AsyncTask;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ChapterUpdata {

    private Set<LongTask> mTask;
    CrawlWebDetils crawlWebDetils=new CrawlWebDetils();
    LinearLayout linearLayout;
    HashMap<String,info> hashMapUp;
    ListView listView;
    public ChapterUpdata(LinearLayout linearLayout, ListView listView){
        mTask= new HashSet<>();
        hashMapUp=new HashMap<>();
        this.linearLayout=linearLayout;
        this.listView=listView;
    }

    class LongTask extends AsyncTask<String,String,String> {
        TextView tUptime,tChapter,tUpData;
        String murl,chpaters,uptime,newchaper;
        ConcurrentHashMap<String,String> hashMap1=new ConcurrentHashMap<>();
        public LongTask(TextView tUptime,TextView tChapter,TextView tUpData,String c2)
        {
            this.tUptime=tUptime;
            this.tChapter=tChapter;
            this.tUpData=tUpData;
            this.chpaters=c2;
        }
        @Override
        protected String doInBackground(String... strings) {
            murl=strings[0];
            hashMap1=crawlWebDetils.getUrl(murl);
            uptime=hashMap1.get("uptime");
            newchaper=hashMap1.get("newchapter");
            return null;
        }
        @Override
        protected void onPostExecute(String result) {
            info in=new info();
            in.uptime=uptime;
            if(tChapter.getTag().equals(murl)&&tUptime.getTag().equals(murl)) {
                tUptime.setText(uptime);
                if(newchaper!=null&&!newchaper.equals(chpaters))
                {
                    tChapter.setText(newchaper);
                    tUpData.setVisibility(View.VISIBLE);
                    in.up="8";
                    in.chapter=newchaper;
                    hashMapUp.put(murl,in);
                }
                else
                {
                    tChapter.setText(chpaters);
                    tUpData.setVisibility(View.GONE);
                    in.up="0";
                    in.chapter=chpaters;
                    hashMapUp.put(murl,in);
                }
            }
            mTask.remove(this);
            if(mTask.isEmpty())
            {
                linearLayout.setVisibility(View.GONE);
                listView.setEnabled(true);
            }
        }
    }

    public void loadchpater(TextView tup,TextView tchp,TextView upd,String url,String c) {

        upd.setVisibility(View.GONE);
        LongTask longTask = new LongTask(tup, tchp, upd, c);
        longTask.execute(url);
        mTask.add(longTask);
    }
    static class info{
        String up;
        String chapter;
        String uptime;
        public  void set(String up,String uptime,String chapter)
        {
            this.up=up;
            this.uptime=uptime;
            this.chapter=chapter;
        }
    }
}
