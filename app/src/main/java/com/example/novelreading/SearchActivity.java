package com.example.novelreading;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    EditText editText;
    ListView listView;
    String searchinfo="";
    private LinearLayout llProgress;
    Context context=this;
    CrawlSearch crawlSearch=new CrawlSearch();
    MyAdapter myAdapter;
    int flag=0;
    int page=1;
    List<HashMap< String, String>> list = new ArrayList< HashMap< String, String> >();
    List< HashMap< String, String> > list1 = new ArrayList< HashMap< String, String> >();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setCustomActionBar();
//        Log.i("title",""+this.getTitle().toString());
        this.setTitle("搜索");
        editText=(EditText)findViewById(R.id.edittTxt);
        listView=(ListView)findViewById(R.id.listview);
        llProgress = (LinearLayout) findViewById(R.id.ll_progress);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView turl=(TextView) view.findViewById(R.id.starturl);
                TextView tbook=(TextView) view.findViewById(R.id.bookname);
//                    ImageView img_circle3=(ImageView)view.findViewById(R.id.img2);
//                    Log.i("imgwidth",img_circle3.getMeasuredHeight()+"");
                Intent intent=new Intent(SearchActivity.this,BookDetailsHome.class);
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
                            Toast.makeText(SearchActivity.this, "已经到底了", Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });
    }
    private void setCustomActionBar() {
        ActionBar.LayoutParams lp =new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT, Gravity.CENTER);
        View mActionBarView = LayoutInflater.from(this).inflate(R.layout.actionbar2, null);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(mActionBarView, lp);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
    }
    public void search(View view) {
        list.clear();
        list1.clear();
        InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(editText.getWindowToken(),0);
        searchinfo=editText.getText().toString();
        new LongTask().execute();
        listView.setSelection(0);
        page=1;
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
            list1=crawlSearch.getsearch(searchinfo,page);
            if(list1.size()<10)
            {
                flag=1;
            }
//            Log.i("list",list1.size()+"");
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