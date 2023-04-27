package com.example.novelreading;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class BookShelf extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback{

    ListView listView;
    SQLiteDatabase sqLiteDatabase;
    Cursor cursor;
    List<BookDetailsClass> list;
    PopupWindow popupWindow;
    float y1,y2;
    ChapterUpdata chapterUpdata;
    LinearLayout linearLayout;
    ImageLoader imageLoader;
    List<String> integerList=new ArrayList<>();
    Boolean readflag=true,wifiFlag=true,refreshflag=true,downflag=false;
    static BooklistDBHelper booklistDBHelper;
    MyAdapter myAdapter;

    @Override
    protected void onStart() {
        super.onStart();
        updata();
        SharedPreferences sharedPreferences=getSharedPreferences("light", Context.MODE_PRIVATE);
        int x=sharedPreferences.getInt("light",-2);
        if(x!=-2) {
            changelight(x);
        }
        wifiFlag=isWifi(BookShelf.this);
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
        this.getWindow().setAttributes(lp);
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_shelf);
        setCustomActionBar();
        listView = (ListView) findViewById(R.id.bookshelflist);
        linearLayout=(LinearLayout)findViewById(R.id.prograss);
        list=new ArrayList<BookDetailsClass>();
        chapterUpdata=new ChapterUpdata(linearLayout,listView);
        imageLoader=new ImageLoader(getFileRoot());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(readflag) {
                    final TextView tt = (TextView) view.findViewById(R.id.starturl);
                    final TextView updatas=(TextView)view.findViewById(R.id.updata);
                    final TextView bookname=(TextView)view.findViewById(R.id.bookname);
                    final TextView chapter=(TextView)view.findViewById(R.id.chapter);
                    final TextView uptime=(TextView)view.findViewById(R.id.uptime);
                    if(updatas.getVisibility()==View.VISIBLE)
                    {
                        updatas.setVisibility(View.GONE);
                        ChapterUpdata.info in=new ChapterUpdata.info();
                        in.set("0",uptime.getText().toString(),chapter.getText().toString());
                        chapterUpdata.hashMapUp.put(tt.getText().toString(),in);
                        Thread t=new Thread(){
                            @Override
                            public void run() {
                                super.run();
                                try {
                                    sqLiteDatabase=new BooklistDBHelper(BookShelf.this).getWritableDatabase();
                                    sqLiteDatabase.execSQL("update "+bookshelfdatabase.BookInfo.TABLE_NAME+" set newchapter=?,uptime=? where starturl=? ",new Object[]{chapter.getText().toString(),uptime.getText().toString(),tt.getText().toString()});
                                }finally {
                                    sqLiteDatabase.close();
                                }
                            }
                        };
                        t.start();
                        try {
                            t.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    Intent intent = new Intent(BookShelf.this, ReadNovel.class);
                    intent.putExtra("contentUrl", "" + tt.getText());
                    intent.putExtra("bookname", "" + bookname.getText());
                    startActivity(intent);
                }
                else {
                    CheckBox checkBox=(CheckBox)view.findViewById(R.id.checkbox);
                    TextView tid=(TextView)view.findViewById(R.id.id);
                    String i1=tid.getText().toString();
                    if(checkBox.isChecked())
                    {
                        checkBox.setChecked(false);
                        myAdapter.removechecked(i1);
                    }
                    else {
                        checkBox.setChecked(true);
                        myAdapter.setchecked(i1);
                    }
                }
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                readflag=false;
                refreshflag=false;
                myAdapter.notifyDataSetChanged();
                View popupWindowView = getLayoutInflater().inflate(R.layout.delete, null);
                popupWindow=new PopupWindow(popupWindowView,WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT,true);
                popupWindow.setFocusable(false);
                popupWindow.setOutsideTouchable(false);
                popupWindow.showAtLocation(getLayoutInflater().inflate(R.layout.activity_book_shelf, null), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

                Button btn=(Button)popupWindowView.findViewById(R.id.btn);
                Button btn1=(Button)popupWindowView.findViewById(R.id.btn1);
                btn1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        popupWindow.dismiss();
                        readflag=true;
                        integerList.clear();
                        refreshflag=true;
                        myAdapter.notifyDataSetChanged();
                    }
                });
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Thread t=new Thread(new Myrun());
                        t.start();
                        try {
                            t.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        popupWindow.dismiss();
                        readflag=true;
                        refreshflag=true;
                        integerList.clear();
                        updata();
                    }
                });
                return false;
            }
        });
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                switch(i)
                {
                    case SCROLL_STATE_IDLE:
                        if(y1+200f<=y2&&downflag&&refreshflag&&linearLayout.getVisibility()==View.GONE)
                        {
                            chapterUpdata.hashMapUp.clear();
                            updata();
                            linearLayout.setVisibility(View.VISIBLE);
                            listView.setEnabled(false);
                        }
                        break;
                    case SCROLL_STATE_TOUCH_SCROLL:
                        if(listView.getFirstVisiblePosition()==0)
                        {
                            downflag=true;
                        }
                        else {
                            downflag=false;
                        }
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });
        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        y1=motionEvent.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        y2=motionEvent.getRawY();
                        break;
                }
                return false;
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
    public class Myrun extends Thread{
        @Override
        public void run() {
            super.run();
            int size=integerList.size();
            try {
                sqLiteDatabase = getDBHelper(BookShelf.this).getWritableDatabase();

                for (int j = 0; j < size; j++) {
                    sqLiteDatabase.delete(bookshelfdatabase.BookInfo.TABLE_NAME, "_id=?", new String[]{integerList.get(j)});
                }
            }finally {
                sqLiteDatabase.close();
            }
        }
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
    public void updata()
    {
        try {
            sqLiteDatabase = getDBHelper(BookShelf.this).getReadableDatabase();
            cursor = booklistDBHelper.selectAll(sqLiteDatabase);
            list.clear();
            if (cursor != null && cursor.getCount() > 0) {

                do {
                    String id = cursor.getString(0);
                    String bookname = cursor.getString(1);
                    String completeness = cursor.getString(3);
                    String newchapter = cursor.getString(5);
                    String imgsrc = cursor.getString(7);
                    String starturl = cursor.getString(9);

                    BookDetailsClass bookDetailsClass = new BookDetailsClass();
                    bookDetailsClass.setId(id);
                    bookDetailsClass.setBookname(bookname);
                    bookDetailsClass.setCompleteness(completeness);
                    bookDetailsClass.setNewchapter(newchapter);
                    bookDetailsClass.setImgsrc(imgsrc);
                    bookDetailsClass.setStarturl(starturl);
                    list.add(bookDetailsClass);
                } while (cursor.moveToNext());
            }
        }finally {
            sqLiteDatabase.close();
        }
        if(myAdapter==null) {
            myAdapter = new MyAdapter(list);
            listView.setAdapter(myAdapter);
        }
        else {
            myAdapter.notifyDataSetChanged();
        }
    }

    private class MyAdapter extends BaseAdapter {

        URL myFileUrl = null;
        Bitmap bitmap = null;
        List<BookDetailsClass> list2;
        public MyAdapter(List<BookDetailsClass> list){
            list2=list;
        }
        public void setchecked(String i)
        {
            integerList.add(i);
        }
        public void removechecked(String i)
        {
            integerList.remove(i);
        }
        @Override
        public int getCount() {

            return list2.size();
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
            View view;
            if (convertView == null) {
                //创建新的view 对象
                view = View.inflate(getApplicationContext(), R.layout.book, null);

            }else{
                view = convertView;
            }
            //[找到控件用来显示数据]
            ImageView imageView=(ImageView)view.findViewById(R.id.img2) ;
            ImageView imgp=(ImageView)view.findViewById(R.id.imgp) ;
            TextView bookname = (TextView) view.findViewById(R.id.bookname);
            TextView id = (TextView) view.findViewById(R.id.id);
            TextView updata=(TextView)view.findViewById(R.id.updata);
            TextView chapter = (TextView) view.findViewById(R.id.chapter);
            TextView completeness = (TextView) view.findViewById(R.id.completeness);
            TextView uptime = (TextView) view.findViewById(R.id.uptime);
            TextView starturl = (TextView) view.findViewById(R.id.starturl);
            CheckBox checkBox=(CheckBox)view.findViewById(R.id.checkbox);
            //如何显示数据
            BookDetailsClass bookDetailsClass=list2.get(position);
            imageView.setTag(bookDetailsClass.getImgsrc());
            imageLoader.loadImages2(imageView,bookDetailsClass.getImgsrc());
            imgp.setVisibility(View.GONE);
            id.setText(bookDetailsClass.getId());
            String url=bookDetailsClass.getStarturl();
            chapter.setTag(url);
            uptime.setTag(url);
            bookname.setText(bookDetailsClass.getBookname());
            completeness.setText(bookDetailsClass.getCompleteness());
            starturl.setText(url);
            if(chapterUpdata.hashMapUp.get(url)==null)
            {
                chapterUpdata.loadchpater(uptime,chapter,updata,url,bookDetailsClass.getNewchapter());
            }
            else {
                int x=Integer.valueOf(chapterUpdata.hashMapUp.get(url).up).intValue();
                if(x==8)
                {
                    chapter.setText(chapterUpdata.hashMapUp.get(url).chapter);
                    uptime.setText(chapterUpdata.hashMapUp.get(url).uptime);
                    updata.setVisibility(View.VISIBLE);
                }
                else {
                    chapter.setText(chapterUpdata.hashMapUp.get(url).chapter);
                    uptime.setText(chapterUpdata.hashMapUp.get(url).uptime);
                    updata.setVisibility(View.GONE);
                }
            }
            if(integerList.contains(id.getText().toString()))
            {
                checkBox.setChecked(true);
            }
            else {
                checkBox.setChecked(false);
            }
            if(!readflag)
            {
                checkBox.setVisibility(View.VISIBLE);
            }
            else {
                checkBox.setVisibility(View.GONE);
            }
            return view;
        }

    }
    public void search(View view) {
        startActivity(new Intent(this,SearchActivity.class));
    }

    private static boolean isWifi(Context mContext) {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null
                && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }
    public synchronized static BooklistDBHelper getDBHelper(Context context)
    {
        if(booklistDBHelper==null)
        {
            booklistDBHelper=new BooklistDBHelper(context);
            return booklistDBHelper;
        }
        return booklistDBHelper;
    }
}