package com.example.novelreading;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Spac1 extends AppCompatActivity {

    MyAdapter []myAdapter=new MyAdapter[3];
    private ListView listView;
    RadioButton r1,r2,r3;
    RadioGroup rg;
    int rgflag=1;
    Crawlrecommend crawlrecommend=new Crawlrecommend();
    Context context=this;
    private LinearLayout llProgress;

    List<HashMap< String, String>> list1 = new ArrayList< HashMap< String, String> >();
    List< HashMap< String, String> > list2 = new ArrayList< HashMap< String, String> >();
    List< HashMap< String, String> > list3 = new ArrayList< HashMap< String, String> >();

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences sharedPreferences=getSharedPreferences("light",Context.MODE_PRIVATE);
        int x=sharedPreferences.getInt("light",-2);
        if(x!=-2) {
            changelight(x);
        }

    }
    public  void changelight(int brightness)
    {
        Window window = this.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        if (brightness == -1) {
            lp.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;
        } else {
            lp.screenBrightness = (brightness <= 0 ? 1 : brightness) / 255f;
        }
        window.setAttributes(lp);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spac1);
        setCustomActionBar();
        TextView tt=(TextView)getSupportActionBar().getCustomView().findViewById(android.R.id.title);
        tt.setText("推荐");
        listView=(ListView) findViewById(R.id.userListItem);
        llProgress = (LinearLayout) findViewById(R.id.ll_progress);
        r1=(RadioButton)findViewById(R.id.r1);
        r2=(RadioButton)findViewById(R.id.r2);
        r3=(RadioButton)findViewById(R.id.r3);
        rg=(RadioGroup)findViewById(R.id.rg);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                checked();
            }
        });
        checked();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView turl=(TextView) view.findViewById(R.id.starturl);
                TextView tbook=(TextView) view.findViewById(R.id.bookname);
                Intent intent=new Intent(Spac1.this,BookDetailsHome.class);
                intent.putExtra("URL",turl.getText());
                intent.putExtra("bookname",tbook.getText());
                startActivity(intent);
            }
        });
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

    private void setCustomActionBar() {
        ActionBar.LayoutParams lp =new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT, Gravity.CENTER);
        View mActionBarView = LayoutInflater.from(this).inflate(R.layout.actionbar_layout, null);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(mActionBarView, lp);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
    }
    public void checked() {
        if(r1.isChecked())
        {
            r1.setBackgroundColor(Color.parseColor("#FFFFFF"));
            r1.setTextColor(Color.parseColor("#0B85E3"));
            rgflag=1;
            new LongTask().execute();
        }
        else
        {
            r1.setBackgroundColor(Color.parseColor("#D4D5D7"));
            r1.setTextColor(Color.BLACK);
        }
        if(r2.isChecked())
        {
            rgflag=2;
            r2.setBackgroundColor(Color.parseColor("#FFFFFF"));
            r2.setTextColor(Color.parseColor("#0B85E3"));
            new LongTask().execute();
        }
        else
        {
            r2.setBackgroundColor(Color.parseColor("#D4D5D7"));
            r2.setTextColor(Color.BLACK);
        }
        if(r3.isChecked())
        {
            rgflag=3;
            r3.setBackgroundColor(Color.parseColor("#FFFFFF"));
            r3.setTextColor(Color.parseColor("#0B85E3"));
            new LongTask().execute();
        }
        else
        {
            r3.setBackgroundColor(Color.parseColor("#D4D5D7"));
            r3.setTextColor(Color.BLACK);
        }
    }

    protected class LongTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            // 显示进度条
            llProgress.setVisibility(View.VISIBLE);
        }
        @Override
        protected String doInBackground(String... strings) {
            if(rgflag==1) {
                if(list1.size()<=0) {
                    list1.addAll(crawlrecommend.getcatalogue("https://www.qidian.com/rank/yuepiao?style=1&page="));
                }
            }
            if(rgflag==2) {
                if(list2.size()<=0) {
                    list2.addAll(crawlrecommend.getcatalogue("https://www.qidian.com/rank/click?style=1&page="));
                }
            }
            if(rgflag==3) {
                if(list3.size()<=0) {
                    list3.addAll(crawlrecommend.getcatalogue("https://www.qidian.com/rank/recom?style=1&page="));
                }
            }
            return null;
        }
        @Override
        protected void onPostExecute(String s) {
            llProgress.setVisibility(View.GONE);
            List< HashMap< String, String> > list = new ArrayList< HashMap< String, String> >();
            if(rgflag==1) {
                list=list1;
                if(myAdapter[0]==null) {
                    myAdapter[0] = new MyAdapter(context, list1, getFileRoot());
                    listView.setAdapter(myAdapter[0]);
                }
                else {
                    listView.setAdapter(myAdapter[0]);
                }
            }
            else if(rgflag==2) {
//                list.clear();
                list=list2;
                if(myAdapter[1]==null) {
                    myAdapter[1] = new MyAdapter(context, list2, getFileRoot());
                    listView.setAdapter(myAdapter[1]);
                }
                else {
                    listView.setAdapter(myAdapter[1]);
                }
            }
            else if(rgflag==3) {
                list=list3;
                if(myAdapter[2]==null) {
                    myAdapter[2] = new MyAdapter(context, list3, getFileRoot());
                    listView.setAdapter(myAdapter[2]);
                }
                else {
                    listView.setAdapter(myAdapter[2]);
                }
            }
        }
    }
    public void search(View view) {
        startActivity(new Intent(this,SearchActivity.class));
    }

}