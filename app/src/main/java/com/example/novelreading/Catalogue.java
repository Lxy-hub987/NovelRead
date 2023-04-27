package com.example.novelreading;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Catalogue extends AppCompatActivity {

    private LinearLayout llProgress;
    ListView listView;
    SimpleAdapter adapter;
    String bookid;
    CrawlWebCatalogue crawlWebCatalogue=new CrawlWebCatalogue();
    Context context=this;
    List<HashMap< String, String>> list = new ArrayList< HashMap< String, String> >();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalogue);
        listView=(ListView)findViewById(R.id.cataloguelist);
        llProgress = (LinearLayout) findViewById(R.id.ll_progress);
        new LongTask().execute();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView textView=(TextView)view.findViewById(R.id.cataurl);
                Intent intent=new Intent(Catalogue.this,ReadNovel.class);
                intent.putExtra("contentUrl",textView.getText());
                intent.putExtra("bookid",bookid);
                intent.putExtra("bookname",getIntent().getStringExtra("bookname"));
                intent.putExtra("index",i);
                startActivity(intent);
            }
        });
    }
    protected class LongTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            // 显示进度条
            llProgress.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... strings) {
            Intent intent=getIntent();
            bookid=intent.getStringExtra("bookid");
            list=crawlWebCatalogue.getcatalogue(bookid);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {//此函数任务在线程池中执行结束了，回调给UI主线程的结果 比如下载结果
            llProgress.setVisibility(View.GONE);
            adapter=new SimpleAdapter(Catalogue.this,
                    list,
                    R.layout.cataloguelayout,
                    new String[]{"catalogue", "catalogueurl"},
                    new int[]{R.id.cata, R.id.cataurl});
            listView.setAdapter(adapter);
        }
    }
}