package com.example.novelreading;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;

public class BookDetailsHome extends AppCompatActivity {

    ConcurrentHashMap<String,String> hashMap=new ConcurrentHashMap<>();
    CrawlWebDetils crawlWebDetils=new CrawlWebDetils();
    String url;
    Boolean flag=false;
    URL myFileUrl = null;
    Bitmap bitmap = null;
    TextView bookname,completeness,type,details,author,uptime,newchapter;
    Button btn;
    static  BooklistDBHelper booklistDBHelper;
    SQLiteDatabase sqLiteDatabase;
    ImageView imageView;
    Context context=this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details_home);

        this.setTitle("书籍详情");
        bookname=(TextView)findViewById(R.id.bookname);
        completeness=(TextView)findViewById(R.id.booklabel);
        type=(TextView)findViewById(R.id.novelTypes);
        details=(TextView)findViewById(R.id.bookdetails);
        author=(TextView)findViewById(R.id.author);
        uptime=(TextView)findViewById(R.id.uptime);
        newchapter=(TextView)findViewById(R.id.newchapter);
        btn=(Button)findViewById(R.id.addordel);
        imageView=(ImageView)findViewById(R.id.img1);

        Intent intent=getIntent();
        url=intent.getStringExtra("URL");
        hashMap=crawlWebDetils.getUrl(url);

        bookname.setText(intent.getStringExtra("bookname"));
        completeness.setText(hashMap.get("completeness"));
        author.setText(hashMap.get("author"));
        type.setText(hashMap.get("type"));
        uptime.setText(hashMap.get("uptime"));
        details.setText(hashMap.get("details"));
        newchapter.setText(hashMap.get("newchapter"));
        Bitmap bm =getHttpImg(hashMap.get("src"));
        imageView.setImageBitmap(bm);
        flag=getflage();
        final TextView textView = findViewById(R.id.bookdetails);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取省略的字符数，0表示没有省略
                int ellipsisCount = textView.getLayout().getEllipsisCount(textView.getLineCount() - 1);
                if (ellipsisCount > 0) {
                    textView.setMaxHeight(getResources().getDisplayMetrics().heightPixels);
                } else {
                    textView.setMaxLines(3);
                }
            }
        });
    }

    private Boolean getflage() {
        try {
            sqLiteDatabase = getDBHelper(context).getWritableDatabase();
            Cursor cursor = sqLiteDatabase.query(bookshelfdatabase.BookInfo.TABLE_NAME, null, "bookname=?", new String[]{"" + getIntent().getStringExtra("bookname") + ""}, null, null, null);

            if (cursor != null && cursor.getCount() > 0) {
                if (cursor.moveToNext()) {
                    btn.setText("移出书架");
                    return true;
                }
            }
            btn.setText("加入书架");
        }finally {
            sqLiteDatabase.close();
        }
        return false;
    }

    private Bitmap getHttpImg(String src) {

        try {
            myFileUrl = new URL(src);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        if(myFileUrl!=null&&!myFileUrl.equals("")) {
            Thread t = new Thread() {
                @Override
                public void run() {
                    super.run();
                    try {
                        HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
                        conn.setConnectTimeout(0);
                        conn.setDoInput(true);
                        conn.connect();
                        InputStream is = conn.getInputStream();
                        bitmap = BitmapFactory.decodeStream(is);
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
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

        return bitmap;
    }

    public static byte[] getBytes(InputStream is) throws IOException {
        ByteArrayOutputStream outstream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024]; // 用数据装
        int len = -1;
        while ((len = is.read(buffer)) != -1) {
            Log.i("ll", len+"");
            outstream.write(buffer, 0, len);
        }
        outstream.close();
        // 关闭流一定要记得。
        return outstream.toByteArray();
    }

    public void readText(View view) {

        Intent intent=new Intent(this,ReadNovel.class);
        intent.putExtra("contentUrl",hashMap.get("url"));
        String [] u=url.split("/");
        intent.putExtra("bookid",u[u.length-1]);
        intent.putExtra("bookname",bookname.getText());
        startActivity(intent);
    }

    public void addordelete(View view) {
        Thread t=new Thread(){
            @Override
            public void run() {
                super.run();
                sqLiteDatabase=getDBHelper(context).getWritableDatabase();
                try {
                    if (flag) {
                        sqLiteDatabase.delete(bookshelfdatabase.BookInfo.TABLE_NAME, "bookname=?", new String[]{"" + getIntent().getStringExtra("bookname") + ""});
                    } else {
                        booklistDBHelper.insertData("" + getIntent().getStringExtra("bookname"), "" + hashMap.get("author"), "" + hashMap.get("completeness"), "0", "" + hashMap.get("newchapter"), "1", "" + hashMap.get("src"), "" + hashMap.get("url"), "" + url, sqLiteDatabase);
                    }
                }
                finally {
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
        flag=getflage();
    }

    public void delete(View view) {

        Thread t=new Thread(){
            @Override
            public void run() {
                super.run();
                if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                    File file= new File(Environment.getExternalStorageDirectory().getAbsoluteFile()+ "/Book");
                    File file2= new File(Environment.getExternalStorageDirectory().getAbsoluteFile()+ "/pics");
                    deleteFile(file);
                    deleteFile(file2);
                }
            }
        };
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Toast.makeText(this, "清除缓存成功", Toast.LENGTH_SHORT).show();
    }
    public void deleteFile(File file) {
        if(file.exists()) {
            if(file.isFile()){
                file.delete();
            }else{
                File[] listFiles = file.listFiles();
                for (File file2 : listFiles) {
                    deleteFile(file2);
                }
            }
            file.delete();
        }
    }

    public void getcatalogue(View view) {
        Intent intent=new Intent(BookDetailsHome.this,Catalogue.class);
        String [] u=url.split("/");
        intent.putExtra("bookid",u[u.length-1]);
        intent.putExtra("bookname",bookname.getText());
        startActivity(intent);
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