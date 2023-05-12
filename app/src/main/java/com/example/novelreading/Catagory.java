package com.example.novelreading;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Catagory extends AppCompatActivity {

    ImageView[] imageView=new ImageView[14];
    URL myFileUrl = null;
    Bitmap bitmap = null;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catagory);
        setCustomActionBar();
        TextView tt=(TextView)getSupportActionBar().getCustomView().findViewById(android.R.id.title);
        tt.setText("分类");
        imageView[0]=(ImageView)findViewById(R.id.img1);
        imageView[1]=(ImageView)findViewById(R.id.img2);
        imageView[2]=(ImageView)findViewById(R.id.img3);
        imageView[3]=(ImageView)findViewById(R.id.img4);
        imageView[4]=(ImageView)findViewById(R.id.img5);
        imageView[5]=(ImageView)findViewById(R.id.img6);
        imageView[6]=(ImageView)findViewById(R.id.img7);
        imageView[7]=(ImageView)findViewById(R.id.img8);
        imageView[8]=(ImageView)findViewById(R.id.img9);
        imageView[9]=(ImageView)findViewById(R.id.img10);
        imageView[10]=(ImageView)findViewById(R.id.img11);
        imageView[11]=(ImageView)findViewById(R.id.img12);
        imageView[12]=(ImageView)findViewById(R.id.img13);
        imageView[13]=(ImageView)findViewById(R.id.img14);

        String []imgsrc={"https://bookcover.yuewen.com/qdbimg/349573/1004608738/150",
                "https://bookcover.yuewen.com/qdbimg/349573/1011408726/150",
                "https://bookcover.yuewen.com/qdbimg/349573/1001827159/150",
                "https://bookcover.yuewen.com/qdbimg/349573/1010327039/150",
                "https://bookcover.yuewen.com/qdbimg/349573/3602691/150",
                "https://bookcover.yuewen.com/qdbimg/349573/1004593977/150",
                "https://bookcover.yuewen.com/qdbimg/349573/1010539872/150",
                "https://bookcover.yuewen.com/qdbimg/349573/1010136878/150",
                "https://bookcover.yuewen.com/qdbimg/349573/1009480992/150",
                "https://bookcover.yuewen.com/qdbimg/349573/1012223036/150",
                "https://bookcover.yuewen.com/qdbimg/349573/1011449273/150",
                "https://bookcover.yuewen.com/qdbimg/349573/1011335417/150",
                "https://bookcover.yuewen.com/qdbimg/349573/1009817672/150",
                "https://bookcover.yuewen.com/qdbimg/349573/1013365677/150"};

        Bitmap [] bm=new Bitmap[imgsrc.length];
        for(int i=0;i<imgsrc.length;i++)
        {
            bm[i] =getHttpImg(imgsrc[i]);
            imageView[i].setImageBitmap(bm[i]);
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
    private Bitmap getHttpImg(String src) {
//          Log.i("httpurl",src);
        try {
            myFileUrl = new URL(src);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Thread t=new Thread(){
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

        return bitmap;
    }
    public void showsomebook(View view)
    {
        TextView textView1;
        int id = view.getId();
        if(id == R.id.l1) {
            textView1 = (TextView) view.findViewById(R.id.t1);
            intent = new Intent(Catagory.this, CatagoryBook.class);
            intent.putExtra("url", "https://www.qidian.com/all?chanId=21&orderId=&style=1&pageSize=20&siteid=1&pubflag=0&hiddenField=0&page=");
            intent.putExtra("catagory", textView1.getText().toString());
            startActivity(intent);
        }
        else if(id == R.id.l2) {
            textView1 = (TextView) view.findViewById(R.id.t2);
            intent = new Intent(Catagory.this, CatagoryBook.class);
            intent.putExtra("url", "https://www.qidian.com/all?chanId=1&orderId=&style=1&pageSize=20&siteid=1&pubflag=0&hiddenField=0&page=");
            intent.putExtra("catagory", textView1.getText().toString());
            startActivity(intent);
        }
        else if(id == R.id.l3) {
            textView1=(TextView)view.findViewById(R.id.t3);
            intent=new Intent(Catagory.this,CatagoryBook.class);
            intent.putExtra("url","https://www.qidian.com/all?chanId=2&orderId=&style=1&pageSize=20&siteid=1&pubflag=0&hiddenField=0&page=");
            intent.putExtra("catagory",textView1.getText().toString());
            startActivity(intent);
       }
        else if(id == R.id.l4) {
            textView1=(TextView)view.findViewById(R.id.t4);
            intent=new Intent(Catagory.this,CatagoryBook.class);
            intent.putExtra("url","https://www.qidian.com/all?chanId=22&orderId=&style=1&pageSize=20&siteid=1&pubflag=0&hiddenField=0&page=");
            intent.putExtra("catagory",textView1.getText().toString());
            startActivity(intent);
       }
        else if(id == R.id.l5) {
            textView1=(TextView)view.findViewById(R.id.t5);
            intent=new Intent(Catagory.this,CatagoryBook.class);
            intent.putExtra("url","https://www.qidian.com/all?chanId=4&orderId=&style=1&pageSize=20&siteid=1&pubflag=0&hiddenField=0&page=");
            intent.putExtra("catagory",textView1.getText().toString());
            startActivity(intent);
       }
        else if(id == R.id.l6) {
            textView1=(TextView)view.findViewById(R.id.t6);
            intent=new Intent(Catagory.this,CatagoryBook.class);
            intent.putExtra("url","https://www.qidian.com/all?chanId=15&orderId=&style=1&pageSize=20&siteid=1&pubflag=0&hiddenField=0&page=");
            intent.putExtra("catagory",textView1.getText().toString());
            startActivity(intent);
       }
        else if(id == R.id.l7) {
            textView1=(TextView)view.findViewById(R.id.t7);
            intent=new Intent(Catagory.this,CatagoryBook.class);
            intent.putExtra("url","https://www.qidian.com/all?chanId=6&orderId=&style=1&pageSize=20&siteid=1&pubflag=0&hiddenField=0&page=");
            intent.putExtra("catagory",textView1.getText().toString());
            startActivity(intent);
       }
        else if(id == R.id.l8) {
            textView1=(TextView)view.findViewById(R.id.t8);
            intent=new Intent(Catagory.this,CatagoryBook.class);
            intent.putExtra("url","https://www.qidian.com/all?chanId=5&orderId=&style=1&pageSize=20&siteid=1&pubflag=0&hiddenField=0&page=");
            intent.putExtra("catagory",textView1.getText().toString());
            startActivity(intent);
       }
        else if(id == R.id.l9) {
            textView1=(TextView)view.findViewById(R.id.t9);
            intent=new Intent(Catagory.this,CatagoryBook.class);
            intent.putExtra("url","https://www.qidian.com/all?chanId=7&orderId=&style=1&pageSize=20&siteid=1&pubflag=0&hiddenField=0&page=");
            intent.putExtra("catagory",textView1.getText().toString());
            startActivity(intent);
       }
        else if(id == R.id.l10) {
            textView1=(TextView)view.findViewById(R.id.t10);
            intent=new Intent(Catagory.this,CatagoryBook.class);
            intent.putExtra("url","https://www.qidian.com/all?chanId=8&orderId=&style=1&pageSize=20&siteid=1&pubflag=0&hiddenField=0&page=");
            intent.putExtra("catagory",textView1.getText().toString());
            startActivity(intent);
       }
        else if(id == R.id.l11) {
            textView1=(TextView)view.findViewById(R.id.t11);
            intent=new Intent(Catagory.this,CatagoryBook.class);
            intent.putExtra("url","https://www.qidian.com/all?chanId=9&orderId=&style=1&pageSize=20&siteid=1&pubflag=0&hiddenField=0&page=");
            intent.putExtra("catagory",textView1.getText().toString());
            startActivity(intent);
       }
        else if(id == R.id.l12) {
            textView1=(TextView)view.findViewById(R.id.t12);
            intent=new Intent(Catagory.this,CatagoryBook.class);
            intent.putExtra("url","https://www.qidian.com/all?chanId=10&orderId=&style=1&pageSize=20&siteid=1&pubflag=0&hiddenField=0&page=");
            intent.putExtra("catagory",textView1.getText().toString());
            startActivity(intent);
       }
        else if(id == R.id.l13) {
            textView1=(TextView)view.findViewById(R.id.t13);
            intent=new Intent(Catagory.this,CatagoryBook.class);
            intent.putExtra("url","https://www.qidian.com/all?chanId=12&orderId=&style=1&pageSize=20&siteid=1&pubflag=0&hiddenField=0&page=");
            intent.putExtra("catagory",textView1.getText().toString());
            startActivity(intent);
       }
        else if(id == R.id.l14) {
            textView1=(TextView)view.findViewById(R.id.t14);
            intent=new Intent(Catagory.this,CatagoryBook.class);
            intent.putExtra("url","https://www.qidian.com/all?chanId=20076&orderId=&style=1&pageSize=20&siteid=1&pubflag=0&hiddenField=0&page=");
            intent.putExtra("catagory",textView1.getText().toString());
            startActivity(intent);
       }
    }
    public void search(View view) {
        startActivity(new Intent(this,SearchActivity.class));
    }
}