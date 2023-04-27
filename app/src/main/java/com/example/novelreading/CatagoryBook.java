package com.example.novelreading;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CatagoryBook extends AppCompatActivity {

    ListView listView;
    String url="";
    Intent intent;
    Context context=this;
    int page=1;
    private LinearLayout llProgress;
    CrawlQidainList crawlQidainList=new CrawlQidainList();
    MyAdapter myAdapter;
    int flag=0;
    List< HashMap< String, String> > list = new ArrayList< HashMap< String, String> >();
    List< HashMap< String, String> > list1 = new ArrayList< HashMap< String, String> >();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catagory_book);

        intent=getIntent();
        this.setTitle(intent.getStringExtra("catagory"));
        llProgress = (LinearLayout) findViewById(R.id.ll_progress);

        url=intent.getStringExtra("url");
        listView=(ListView)findViewById(R.id.catagorybooklist);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView turl=(TextView) view.findViewById(R.id.starturl);
                TextView tbook=(TextView) view.findViewById(R.id.bookname);
                Intent intent=new Intent(CatagoryBook.this,BookDetailsHome.class);
                intent.putExtra("URL",turl.getText());
                intent.putExtra("bookname",tbook.getText());
                startActivity(intent);
            }
        });
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                switch(i)
                {
                    case SCROLL_STATE_IDLE:
                        int position = listView.getLastVisiblePosition();
                        if (position == list.size() - 1 &&flag==0) {
                            // 加载下一批数据
                            page++;
                            new LongTask().execute();
                        } else if (position == list.size() - 1 &&flag==1) {
                            Toast.makeText(CatagoryBook.this, "已经到底了", Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
            }
        });
        new LongTask().execute();
    }
    protected class LongTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            // 显示进度条
            llProgress.setVisibility(View.VISIBLE);
        }
        @Override
        protected String doInBackground(String... strings) {
            list1.clear();
            list1=crawlQidainList.getcatalogue(url+""+page);
            if(list1.size()<20)
            {
                flag=1;
            }
            return null;
        }
        @Override
        protected void onPostExecute(String s) {//此函数任务在线程池中执行结束了，回调给UI主线程的结果 比如下载结果
            llProgress.setVisibility(View.GONE);
            list.addAll(list1);
            if (myAdapter == null) {
                myAdapter=new MyAdapter(context,list, getFileRoot());
                listView.setAdapter(myAdapter);
            } else {
                myAdapter.URLS=new String[list.size()];
                for(int i=0;i<list.size();i++){
                    myAdapter.URLS[i] = list.get(i).get("imgsrc");
                }
                myAdapter.notifyDataSetChanged();
            }


        }
    }

    private String getFileRoot() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            File external = this.getExternalFilesDir(null);
            if (external != null) {
                return external.getAbsolutePath();
            }
        }
        return this.getFilesDir().getAbsolutePath();
    }
}