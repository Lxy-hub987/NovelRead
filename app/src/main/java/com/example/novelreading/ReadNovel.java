package com.example.novelreading;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ReadNovel extends AppCompatActivity {

    TextView t1,t2,t3,t4,t5;
    LinearLayout l1,l2;
    CheckBox checkBox;
    String m="",m1="",m2="";
    int flag=1;
    int lastornext=3;
    int isExist=0;
    int Maxpage=0,lines=0,Maxpage2=0;
    int id,index=0,maxchpater=0;
    Boolean scflag=false,listflag=false;
    int spline=0;
    float startspace,stopspace;
    int page_num=1;
    String nowurl;
    String t1text=null;
    String bookid=null;
    SharedPreferences sharedPreferences;
    String contentUrl=null,bookname;
    Dialog dialog;
    ListView listView;
    Set<cacheText> cachett;
    Badapter b=new Badapter();
    SeekBar seekBar,seek;
    HashMap<String,String> hashMap=new HashMap<>();
    List<HashMap<String,String>> list=new ArrayList<>();
    Selectcrawlmethod selectcrawlmethod=new Selectcrawlmethod();

    ReadAndWriteBook readAndWriteBook;
    SQLiteDatabase sqLiteDatabase;
    static BooklistDBHelper booklistDBHelper;

    @Override
    protected void onStart() {
        super.onStart();
        int x=sharedPreferences.getInt("light",-2);
        boolean s=sharedPreferences.getBoolean("sys",true);
        int y=sharedPreferences.getInt("textcolor",-2);
        int z=sharedPreferences.getInt("textsize",0);
        float j=sharedPreferences.getFloat("spaceline",0);
        String nd=sharedPreferences.getString("nd","");
        if(x!=-2&&!s) {
            changelight(x);
        }
        if(y!=-2)
        {
            if(nd!="") {
                if (nd.equals("夜间")) {
                    changecolors(y);
                } else {
                    changecolors(0);
                }
            }
        }
        if(z!=0) {
            t1.setTextSize(z);
            t2.setTextSize(z);
            if(j!=0)
            {
                t1.setLineSpacing(j,1.1f);
                t2.setLineSpacing(j,1.1f);
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_read_novel);
        t1= findViewById(R.id.textView1);
        t2= findViewById(R.id.txt);
        t3= findViewById(R.id.chapter);
        t4= findViewById(R.id.chapternum);
        t5= findViewById(R.id.pagenum);
        l1= findViewById(R.id.l1);
        l2= findViewById(R.id.ll_progress);
        sharedPreferences=getSharedPreferences("light", Context.MODE_PRIVATE);
        l2.setVisibility(View.VISIBLE);
        cachett=new HashSet<>();
        list.clear();
        showText();
        l1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        startspace=motionEvent.getX();
                        break;
                    case MotionEvent.ACTION_UP:
                        stopspace=motionEvent.getX();
                        jug();
                        break;
                    default:
                        break;
                }

                return true;
            }
        });
    }
    public void jug()
    {
        if(startspace-stopspace>20)
        {
            page_num++;
            readtext(1);
        }
        else if(startspace-stopspace<-20)
        {
            page_num--;
            readtext(0);
        }
        else if(startspace-stopspace>-20&&startspace-stopspace<20)
        {
            if(startspace<400)
            {
                page_num--;
                readtext(0);
            }
            else if(startspace>610)
            {
                page_num++;
                readtext(1);
            }
            else
            {
                showdialog();
            }
        }
    }
    public void  readtext(int lnflag)
    {
        if(scflag)
        {
            int page1=((spline)%lines>0 ? (spline)/lines+1:(spline)/lines);
            if(page_num<=Maxpage2&&page_num>0)
            {
                if((page_num*lines)-1<spline)
                {
                    m1 = m.substring(t2.getLayout().getLineStart((page_num - 1) * lines), t2.getLayout().getLineEnd((page_num * lines) - 1));
                }
                else if((page_num-1)*lines<spline&&(page_num*lines)-1>=spline)
                {
                    m1=m.substring(t2.getLayout().getLineStart((page_num - 1) * lines),t2.getLayout().getLineEnd(spline-1));
                }
                else
                {
                    if(((page_num-page1)*lines)-1>Maxpage-spline)
                    {
                        m1=m.substring(t2.getLayout().getLineStart((page_num-page1-1)*lines+spline),t2.getLayout().getLineEnd(Maxpage-1));
                    }
                    else
                    {
                        m1=m.substring(t2.getLayout().getLineStart((page_num-page1-1)*lines+spline),t2.getLayout().getLineEnd(((page_num-page1) * lines) - 1+spline));
                    }
                }
                t1.setText(m1);
                t5.setText(page_num+"/"+Maxpage2+"页");
            }
            else
            {
                flag=2;
                if(lnflag == 1)
                {
                    page_num--;
                    if(index+1<list.size()) {
                        index++;
                    }
                }
                else
                {
                    page_num=1;
                    if(index-1>=0) {
                        index--;
                    }
                }
                lastornext=lnflag;
                scflag=false;
                showText();
            }
            if(isExist==1)
            {
                sqLiteDatabase=getDBHelper(ReadNovel.this).getWritableDatabase();
                if((page_num-1)*lines>=spline)
                {
                    sqLiteDatabase.execSQL("update "+bookshelfdatabase.BookInfo.TABLE_NAME+" set page_num=? where _id=? ",new Object[]{""+(page_num-1),id});

                }
                else
                {
                    sqLiteDatabase.execSQL("update "+bookshelfdatabase.BookInfo.TABLE_NAME+" set page_num=? where _id=? ",new Object[]{""+page_num,id});

                }
                sqLiteDatabase.close();
            }
        }
        else
        {
            if(page_num<=Maxpage2&&page_num>0)
            {
                if((page_num*lines)-1>Maxpage-1)
                {
                    m1=m.substring(t2.getLayout().getLineStart((page_num-1)*lines),t2.getLayout().getLineEnd(Maxpage-1));
                }
                else {
                    m1 = m.substring(t2.getLayout().getLineStart((page_num - 1) * lines), t2.getLayout().getLineEnd((page_num * lines) - 1));
                }
                t1.setText(m1);
                t5.setText(page_num+"/"+Maxpage2+"页");
            }
            else
            {
                flag=2;
                if(lnflag == 1)
                {
                    page_num--;
                    index++;
                }
                else
                {
                    page_num=1;
                    index--;
                }
                lastornext=lnflag;
                showText();
            }
            if(isExist==1)
            {

                sqLiteDatabase=getDBHelper(ReadNovel.this).getWritableDatabase();
                sqLiteDatabase.execSQL("update "+bookshelfdatabase.BookInfo.TABLE_NAME+" set page_num=? where _id=? ",new Object[]{""+page_num,id});
                sqLiteDatabase.close();
            }
        }
    }

    public void setting(View view) {
        dialog.dismiss();
        dialog = new Dialog(this, R.style.dialog1);
        dialog.setContentView(R.layout.setting);

        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.BOTTOM);
        dialogWindow.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        lp.width = WindowManager.LayoutParams.FILL_PARENT;
        dialogWindow.setAttributes(lp);
        dialog.show();
        checkBox=dialog.findViewById(R.id.systemlight);
        seekBar= dialog.findViewById(R.id.seekbar);
        boolean s=sharedPreferences.getBoolean("sys",true);
        checkBox.setChecked(s);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor=sharedPreferences.edit();
                if(!checkBox.isChecked())
                {
                    editor.putBoolean("sys",false);
                    editor.commit();
                    int x=sharedPreferences.getInt("light",-2);
                    changelight(x);
                }
                else  {
                    editor.putBoolean("sys",true);
                    editor.commit();
                    changelight(-1);
                }
            }
        });
        try {
            int x=sharedPreferences.getInt("light",-2);
            boolean ss=sharedPreferences.getBoolean("sys",true);
            if(x==-2||ss) {
                seekBar.setProgress(Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS));
            }
            else
            {
                seekBar.setProgress(x);
            }
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                checkBox.setChecked(false);
                changelight(i);
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putInt("light",i);
                editor.putBoolean("sys",false);
                editor.commit();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        TextView minus= dialog.findViewById(R.id.minus);
        TextView plus= dialog.findViewById(R.id.plus);
        final TextView data= dialog.findViewById(R.id.data);
        int z=sharedPreferences.getInt("textsize",0);
        final int height=sharedPreferences.getInt("height",0);
        if(z!=0)
        {
            data.setText(String.valueOf(z));
        }
        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i=Integer.valueOf(data.getText().toString()).intValue();
                i--;
                if(i>=20) {
                    data.setText(String.valueOf(i));
                    int line2=(page_num - 1) * lines;
                    int splits=0;
                    if(line2>=spline&&spline!=0)
                    {
                        line2=spline+((line2-spline)/lines)*lines;
                        splits = t2.getLayout().getLineStart(line2)-1;
                    }
                    else {
                        splits = t2.getLayout().getLineStart(line2);
                    }
                    t1.setTextSize(i);
                    float y=i*0.8f;
                    t1.setLineSpacing(y,1.1f);
                    t2.setTextSize(i);
                    t2.setLineSpacing(y,1.1f);
                    textsizechanged(splits);
                    scflag=true;
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("textsize", i);
                    editor.putFloat("spaceline",y);
                    editor.commit();
                }
            }
        });
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i=Integer.valueOf(data.getText().toString()).intValue();
                i++;
                if(i<=35) {
                    data.setText(String.valueOf(i));
                    int line2=(page_num - 1) * lines;
                    int splits=0;
                    if(line2>=spline&&spline!=0)
                    {
                        line2=spline+((line2-spline)/lines)*lines;
                        splits = t2.getLayout().getLineStart(line2)-1;
                    }
                    else {
                        splits = t2.getLayout().getLineStart(line2);
                    }
                    t1.setTextSize(i);
                    float y=i*0.8f;
                    t1.setLineSpacing(y,1.1f);
                    t2.setTextSize(i);
                    t2.setLineSpacing(y,1.1f);
                    textsizechanged(splits);
                    scflag=true;
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("textsize", i);
                    editor.putFloat("spaceline",y);
                    editor.commit();
                }
            }
        });
    }
    public void textsizechanged(final int splits)
    {
        m=m2;
        final int height=sharedPreferences.getInt("height",0);
        if(splits>0) {
            String mm1 = m.substring(0, splits);
            String mm2 = m.substring(splits);
            m=mm1+"\n"+mm2;
        }
        t1.post(new Runnable() {
            @Override
            public void run() {
                t1text=m1.substring(t1.getLayout().getLineStart(0),t1.getLayout().getLineEnd(0));
            }
        });
        t2.setText(m);
        t2.post(new Runnable() {
            @Override
            public void run() {
                Maxpage=t2.getLineCount();
                lines = (height / t2.getLineHeight());
//                Log.i("lines",""+lines+"  "+height+" "+t2.getLineHeight());
                for(int i=0;i<t2.getLineCount();i++)
                {
                    String t2text=m.substring(t2.getLayout().getLineStart(i),t2.getLayout().getLineEnd(i));
//                    Log.i("t1text",""+t2text);
                    if(t1text.trim().equals(t2text.trim()))
                    {
                        spline=i;
                        if(spline+lines-1>Maxpage-1)
                        {
                            m1=m.substring(t2.getLayout().getLineStart(spline),t2.getLayout().getLineEnd(Maxpage-1));
                        }
                        else
                        {
                            m1=m.substring(t2.getLayout().getLineStart(spline),t2.getLayout().getLineEnd(spline+lines-1));
                        }

                        int page2=((spline)%lines>0 ? (spline)/lines+1:(spline)/lines);
                        Maxpage2=page2+((Maxpage-spline+1)%lines>0?(Maxpage-spline+1)/lines+1:(Maxpage-spline+1)/lines);
                        page_num=page2+1;
                        t1.setText(m1);
                        t5.setText(page_num+"/"+Maxpage2+"页");
                        break;
                    }
                }
            }
        });
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
    public void  changecolors(int y)
    {
        switch (y)
        {
            case 0:
                t1.setTextColor(Color.WHITE);
                t1.setBackgroundColor(Color.BLACK);
                t3.setTextColor(Color.WHITE);
                t4.setTextColor(Color.WHITE);
                t5.setTextColor(Color.WHITE);
                t3.setBackgroundColor(Color.BLACK);
                break;
            case 1:
                t1.setTextColor(getResources().getColor(R.color.imgcolor1));
                t1.setBackgroundColor(getResources().getColor(R.color.imgcolor12));
                t3.setTextColor(getResources().getColor(R.color.imgcolor1));
                t4.setTextColor(getResources().getColor(R.color.imgcolor1));
                t5.setTextColor(getResources().getColor(R.color.imgcolor1));
                t3.setBackgroundColor(getResources().getColor(R.color.imgcolor12));
                break;
            case 2:
                t1.setTextColor(getResources().getColor(R.color.imgcolor2));
                t1.setBackgroundColor(getResources().getColor(R.color.imgcolor22));
                t3.setTextColor(getResources().getColor(R.color.imgcolor2));
                t4.setTextColor(getResources().getColor(R.color.imgcolor2));
                t5.setTextColor(getResources().getColor(R.color.imgcolor2));
                t3.setBackgroundColor(getResources().getColor(R.color.imgcolor22));
                break;
            case 3:
                t1.setTextColor(getResources().getColor(R.color.imgcolor3));
                t1.setBackgroundColor(getResources().getColor(R.color.imgcolor32));
                t3.setTextColor(getResources().getColor(R.color.imgcolor3));
                t4.setTextColor(getResources().getColor(R.color.imgcolor3));
                t5.setTextColor(getResources().getColor(R.color.imgcolor3));
                t3.setBackgroundColor(getResources().getColor(R.color.imgcolor32));
                break;
            case 4:
                t1.setTextColor(getResources().getColor(R.color.imgcolor4));
                t1.setBackgroundColor(getResources().getColor(R.color.imgcolor42));
                t3.setTextColor(getResources().getColor(R.color.imgcolor4));
                t4.setTextColor(getResources().getColor(R.color.imgcolor4));
                t5.setTextColor(getResources().getColor(R.color.imgcolor4));
                t3.setBackgroundColor(getResources().getColor(R.color.imgcolor42));
                break;
            case 5:
                t1.setTextColor(getResources().getColor(R.color.imgcolor5));
                t1.setBackgroundColor(getResources().getColor(R.color.imgcolor52));
                t3.setTextColor(getResources().getColor(R.color.imgcolor5));
                t4.setTextColor(getResources().getColor(R.color.imgcolor5));
                t5.setTextColor(getResources().getColor(R.color.imgcolor5));
                t3.setBackgroundColor(getResources().getColor(R.color.imgcolor52));
                break;
            case 6:
                t1.setTextColor(getResources().getColor(R.color.imgcolor6));
                t1.setBackgroundColor(getResources().getColor(R.color.imgcolor62));
                t3.setTextColor(getResources().getColor(R.color.imgcolor6));
                t4.setTextColor(getResources().getColor(R.color.imgcolor6));
                t5.setTextColor(getResources().getColor(R.color.imgcolor6));
                t3.setBackgroundColor(getResources().getColor(R.color.imgcolor62));
                break;
            case 7:
                t1.setTextColor(getResources().getColor(R.color.imgcolor7));
                t1.setBackgroundColor(getResources().getColor(R.color.imgcolor72));
                t3.setTextColor(getResources().getColor(R.color.imgcolor7));
                t4.setTextColor(getResources().getColor(R.color.imgcolor7));
                t5.setTextColor(getResources().getColor(R.color.imgcolor7));
                t3.setBackgroundColor(getResources().getColor(R.color.imgcolor72));
                break;
            case 8:
                t1.setTextColor(getResources().getColor(R.color.imgcolor8));
                t1.setBackgroundColor(getResources().getColor(R.color.imgcolor82));
                t3.setTextColor(getResources().getColor(R.color.imgcolor8));
                t4.setTextColor(getResources().getColor(R.color.imgcolor8));
                t5.setTextColor(getResources().getColor(R.color.imgcolor8));
                t3.setBackgroundColor(getResources().getColor(R.color.imgcolor82));
                break;
            case 9:
                t1.setTextColor(getResources().getColor(R.color.imgcolor9));
                t1.setBackgroundColor(getResources().getColor(R.color.imgcolor92));
                t3.setTextColor(getResources().getColor(R.color.imgcolor9));
                t4.setTextColor(getResources().getColor(R.color.imgcolor9));
                t5.setTextColor(getResources().getColor(R.color.imgcolor9));
                t3.setBackgroundColor(getResources().getColor(R.color.imgcolor92));
                break;
            case 10:
                t1.setTextColor(getResources().getColor(R.color.imgcolor10));
                t1.setBackgroundColor(getResources().getColor(R.color.imgcolor102));
                t3.setTextColor(getResources().getColor(R.color.imgcolor10));
                t4.setTextColor(getResources().getColor(R.color.imgcolor10));
                t5.setTextColor(getResources().getColor(R.color.imgcolor10));
                t3.setBackgroundColor(getResources().getColor(R.color.imgcolor102));
                break;
        }
    }
    public void changecolor(View view) {
        SharedPreferences.Editor editor=sharedPreferences.edit();
        int id =  view.getId();
        if(id == R.id.img1) {
            t1.setTextColor(getResources().getColor(R.color.imgcolor1));
            t1.setBackgroundColor(getResources().getColor(R.color.imgcolor12));
            t3.setTextColor(getResources().getColor(R.color.imgcolor1));
            t4.setTextColor(getResources().getColor(R.color.imgcolor1));
            t5.setTextColor(getResources().getColor(R.color.imgcolor1));
            t3.setBackgroundColor(getResources().getColor(R.color.imgcolor12));
            editor.putInt("textcolor", 1);
            editor.putString("nd", "夜间");
            editor.apply();
        }
         else if(id == R.id.img2){
                t1.setTextColor(getResources().getColor(R.color.imgcolor2));
                t1.setBackgroundColor(getResources().getColor(R.color.imgcolor22));
                t3.setTextColor(getResources().getColor(R.color.imgcolor2));
                t4.setTextColor(getResources().getColor(R.color.imgcolor2));
                t5.setTextColor(getResources().getColor(R.color.imgcolor2));
                t3.setBackgroundColor(getResources().getColor(R.color.imgcolor22));
                editor.putInt("textcolor",2);
                editor.putString("nd","夜间");
                editor.apply();
         }
           else if(id == R.id.img3){
                t1.setTextColor(getResources().getColor(R.color.imgcolor3));
                t1.setBackgroundColor(getResources().getColor(R.color.imgcolor32));
                t3.setTextColor(getResources().getColor(R.color.imgcolor3));
                t4.setTextColor(getResources().getColor(R.color.imgcolor3));
                t5.setTextColor(getResources().getColor(R.color.imgcolor3));
                t3.setBackgroundColor(getResources().getColor(R.color.imgcolor32));
                editor.putInt("textcolor",3);
                editor.putString("nd","夜间");
                editor.apply();
           }
           else if(id == R.id.img4){
                t1.setTextColor(getResources().getColor(R.color.imgcolor4));
                t1.setBackgroundColor(getResources().getColor(R.color.imgcolor42));
                t3.setTextColor(getResources().getColor(R.color.imgcolor4));
                t4.setTextColor(getResources().getColor(R.color.imgcolor4));
                t5.setTextColor(getResources().getColor(R.color.imgcolor4));
                t3.setBackgroundColor(getResources().getColor(R.color.imgcolor42));
                editor.putInt("textcolor",4);
                editor.putString("nd","夜间");
                editor.apply();
                }
           else if(id == R.id.img5){
                t1.setTextColor(getResources().getColor(R.color.imgcolor5));
                t1.setBackgroundColor(getResources().getColor(R.color.imgcolor52));
                t3.setTextColor(getResources().getColor(R.color.imgcolor5));
                t4.setTextColor(getResources().getColor(R.color.imgcolor5));
                t5.setTextColor(getResources().getColor(R.color.imgcolor5));
                t3.setBackgroundColor(getResources().getColor(R.color.imgcolor52));
                editor.putInt("textcolor",5);
                editor.putString("nd","夜间");
                editor.apply();
                }
           else if(id == R.id.img6){
                t1.setTextColor(getResources().getColor(R.color.imgcolor6));
                t1.setBackgroundColor(getResources().getColor(R.color.imgcolor62));
                t3.setTextColor(getResources().getColor(R.color.imgcolor6));
                t4.setTextColor(getResources().getColor(R.color.imgcolor6));
                t5.setTextColor(getResources().getColor(R.color.imgcolor6));
                t3.setBackgroundColor(getResources().getColor(R.color.imgcolor62));
                editor.putInt("textcolor",6);
                editor.putString("nd","夜间");
                editor.apply();
                }
           else if(id == R.id.img7){
                t1.setTextColor(getResources().getColor(R.color.imgcolor7));
                t1.setBackgroundColor(getResources().getColor(R.color.imgcolor72));
                t3.setTextColor(getResources().getColor(R.color.imgcolor7));
                t4.setTextColor(getResources().getColor(R.color.imgcolor7));
                t5.setTextColor(getResources().getColor(R.color.imgcolor7));
                t3.setBackgroundColor(getResources().getColor(R.color.imgcolor72));
                editor.putInt("textcolor",7);
                editor.putString("nd","夜间");
                editor.apply();
                }
           else if(id == R.id.img8){
                t1.setTextColor(getResources().getColor(R.color.imgcolor8));
                t1.setBackgroundColor(getResources().getColor(R.color.imgcolor82));
                t3.setTextColor(getResources().getColor(R.color.imgcolor8));
                t4.setTextColor(getResources().getColor(R.color.imgcolor8));
                t5.setTextColor(getResources().getColor(R.color.imgcolor8));
                t3.setBackgroundColor(getResources().getColor(R.color.imgcolor82));
                editor.putInt("textcolor",8);
                editor.putString("nd","夜间");
                editor.apply();
                }
           else if(id == R.id.img9){
                t1.setTextColor(getResources().getColor(R.color.imgcolor9));
                t1.setBackgroundColor(getResources().getColor(R.color.imgcolor92));
                t3.setTextColor(getResources().getColor(R.color.imgcolor9));
                t4.setTextColor(getResources().getColor(R.color.imgcolor9));
                t5.setTextColor(getResources().getColor(R.color.imgcolor9));
                t3.setBackgroundColor(getResources().getColor(R.color.imgcolor92));
                editor.putInt("textcolor",9);
                editor.putString("nd","夜间");
                editor.apply();
                }
           else if(id == R.id.img10){
                t1.setTextColor(getResources().getColor(R.color.imgcolor10));
                t1.setBackgroundColor(getResources().getColor(R.color.imgcolor102));
                t3.setTextColor(getResources().getColor(R.color.imgcolor10));
                t4.setTextColor(getResources().getColor(R.color.imgcolor10));
                t5.setTextColor(getResources().getColor(R.color.imgcolor10));
                t3.setBackgroundColor(getResources().getColor(R.color.imgcolor102));
                editor.putInt("textcolor",10);
                editor.putString("nd","夜间");
                editor.apply();
                }
           }
    public void showcatalogue(View view) {
        dialog.dismiss();
        dialog=new Dialog(this,R.style.dialog1);
        dialog.setContentView(R.layout.catalogues);
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.LEFT);
        lp.height=WindowManager.LayoutParams.FILL_PARENT;
        lp.width=getWindowManager().getDefaultDisplay().getWidth()*7/10;
        dialogWindow.setAttributes(lp);
        dialog.show();
        listView= dialog.findViewById(R.id.listv);
        b.setSelectItem(index);
        listView.setAdapter(b);

        listView.setSelection(index);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView cataurl=(TextView) view.findViewById(R.id.cataurl);
                hashMap.put("nexturl",cataurl.getText().toString());
                lastornext=1;
                scflag=false;
                flag=2;
                index=i;
                cachett.clear();
                cacheText c = new cacheText();
                c.f=1;
                c.flag2=true;
                cachett.add(c);
                c.start();
                showText();
                dialog.dismiss();
            }
        });
    }
    class Badapter extends BaseAdapter {
        private int selectItem = -1;
        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }
        public void setSelectItem(int selectItem) {
            this.selectItem = selectItem;
        }
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View v;
            if (view == null) {
                //创建新的view 对象
                v = View.inflate(ReadNovel.this, R.layout.cataloguelayout, null);
            }else{
                v = view;

            }
            TextView cata = (TextView) v.findViewById(R.id.cata);
            TextView cataurl=(TextView)v.findViewById(R.id.cataurl);
            HashMap<String,String> hashMap1=list.get(i);
            cata.setText(hashMap1.get("catalogue"));
            cataurl.setText(hashMap1.get("catalogueurl"));
            if(selectItem==i)
            {
                cata.setTextColor(Color.parseColor("#4050B5"));
            }
            else
            {
                cata.setTextColor(Color.parseColor("#000000"));
            }

            return v;
        }
    }
    class LongTask extends AsyncTask<String,String,String> {
        @Override
        protected void onPreExecute() {
            // 显示进度条
            l1.setEnabled(false);
        }
        @Override
        protected String doInBackground(String... strings) {
            hashMap = selectcrawlmethod.selectmethod(strings[0]);
            m = hashMap.get("text");
            if (index == 0) {
                maxchpater = list.size();
                for (int i = 0; i < maxchpater; i++) {
                    HashMap<String, String> h = new HashMap<>();
                    h = list.get(i);
                    if (h.get("catalogue").trim().equals(hashMap.get("chpater"))) {
                        index = i;
                        break;
                    }
                }
            }
            if(m!=null) {
                readAndWriteBook.writeBook(m, String.valueOf(index));
            }
            if(flag==2)
            {
                if(m!=null) {
                    if (isExist == 1) {
                        sqLiteDatabase = getDBHelper(ReadNovel.this).getWritableDatabase();
                        sqLiteDatabase.execSQL("update " + bookshelfdatabase.BookInfo.TABLE_NAME + " set uptime=? where _id=? ", new Object[]{"" + index, id});
                        sqLiteDatabase.close();
                    }
                }
            }
            return null;
        }
        @Override
        protected void onPostExecute(String result) {
            // 执行完毕后，则更新UI
            readText2();
        }
    }
    public String adjust(String t)
    {
        String text=t;
        String text2="";
        String [] tt=text.trim().split("\n");
        for(int i=0;i<tt.length-1;i++)
        {
            if(tt[i]!=null) {
                text2 += tt[i].trim() + "\n";
            }
        }
        if(tt[tt.length-1]!=null) {
            text2 += tt[tt.length - 1].trim();
        }
        return text2;
    }
    public void  readText2()
    {
        if(lines==0) {
            if(true)
            {
                SpannableString spannableString = new SpannableString("h \n h \n h \n h \n h \n h \n h \n h \n h \n h \n h \n h \n h \n h \n h \n h \n h \n h \n h \n h \n h \n h \n h \n h \n h \n ");
                spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#00FF0000")), 0,spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                t1.setText(spannableString);
                t1.post(new Runnable() {
                    @Override
                    public void run() {

                        int line=t1.getMeasuredHeight();
                        SharedPreferences.Editor editor=sharedPreferences.edit();
                        editor.putInt("height",line-69);
                        editor.apply();
                    }
                });
            }
        }
        if (m != null) {
            m = adjust(m);
            m=list.get(index).get("catalogue")+"\n\n\n"+m;
            m2=m;
            t2.setText(m);
            t3.setText(list.get(index).get("catalogue"));
            t2.post(new Runnable() {
                @Override
                public void run() {
                    if(lines==0)
                    {
                        int height=sharedPreferences.getInt("height",0);
                        lines=height/t2.getLineHeight();
                    }
                    Maxpage = t2.getLineCount();
                    Maxpage2=(Maxpage % lines > 0 ? Maxpage / lines + 1 : Maxpage / lines);
                    if (lastornext == 0) {
                        page_num = Maxpage2;
                    } else if(flag!=1&&lastornext!=0){
                        page_num = 1;
                    }
                    if ((page_num * lines) - 1 > Maxpage - 1) {
                        m1 = m.substring(t2.getLayout().getLineStart((page_num - 1) * lines), t2.getLayout().getLineEnd(Maxpage - 1));
                    } else {
                        m1 = m.substring(t2.getLayout().getLineStart((page_num - 1) * lines), t2.getLayout().getLineEnd((page_num * lines) - 1));
                    }
                    l2.setVisibility(View.GONE);
                    t1.setText(m1);
                    t4.setText((index+1)+"/"+maxchpater+"章");
                    t5.setText(page_num+"/"+Maxpage2+"页");
                    l1.setEnabled(true);
                    if(isExist==1)
                    {
                        sqLiteDatabase=getDBHelper(ReadNovel.this).getWritableDatabase();
                        sqLiteDatabase.execSQL("update "+bookshelfdatabase.BookInfo.TABLE_NAME+" set page_num=? where _id=? ",new Object[]{""+page_num,id});
                        sqLiteDatabase.close();
                    }
                }
            });
        }
        else {
            Toast.makeText(ReadNovel.this, "已经没有更多的了！", Toast.LENGTH_SHORT).show();
            m=t2.getText()+"";
            l1.setEnabled(true);
        }

    }

    public void showText()
    {
        if(flag==1) {

            Intent intent=getIntent();
            contentUrl=intent.getStringExtra("contentUrl");
            bookname=intent.getStringExtra("bookname");
            readAndWriteBook=new ReadAndWriteBook(bookname);
            sqLiteDatabase=getDBHelper(ReadNovel.this).getWritableDatabase();
            Cursor cursor=sqLiteDatabase.query(bookshelfdatabase.BookInfo.TABLE_NAME,new String[]{"_id","page_num","uptime"},"starturl=?",new String[]{""+contentUrl+""},null,null,null);
            if(cursor!=null&&cursor.getCount()>0)
            {
                if(cursor.moveToNext())
                {
                    id=Integer.parseInt(cursor.getString(0));
                    page_num=Integer.parseInt(cursor.getString(1));
                    nowurl=cursor.getString(2);
                    index=Integer.valueOf(nowurl).intValue();
                }
                String [] u=contentUrl.split("/");
                bookid=u[u.length-1];
                isExist=1;

            }
            else
            {
                isExist=0;
                bookid=intent.getStringExtra("bookid");
                index=intent.getIntExtra("index",0);
            }
            sqLiteDatabase.close();
            if(list.size()<=0||list==null) {
                CrawlWebCatalogue crawlWebCatalogue=new CrawlWebCatalogue();
                list = crawlWebCatalogue.getcatalogue(bookid);
                if(list.size()>0)
                {
                    listflag=true;
                    maxchpater=list.size();
                }
            }
            if((m=readAndWriteBook.readBook(String.valueOf(index)))==null) {
                new LongTask().execute(list.get(Integer.valueOf(index)).get("catalogueurl"));
                if(cachett.isEmpty()) {
                    cacheText c = new cacheText();
                    cachett.add(c);
                    c.start();
                }
                return;
            }
            else {
                if(cachett.isEmpty()) {
                    cacheText c = new cacheText();
                    cachett.add(c);
                    c.start();
                }
            }
            readText2();
        }
        else
        {
            if(index<0)
            {
                m=null;
                index++;
                readText2();
                return;
            }
            if(index>=maxchpater)
            {
                m=null;
                index--;
                readText2();
                return;
            }
            if((m=readAndWriteBook.readBook(""+index))!=null)
            {
                if (isExist == 1) {
                    sqLiteDatabase = getDBHelper(ReadNovel.this).getWritableDatabase();
                    sqLiteDatabase.execSQL("update " + bookshelfdatabase.BookInfo.TABLE_NAME + " set uptime=? where _id=? ", new Object[]{"" + index, id});
                    sqLiteDatabase.close();
                }
                if(cachett.isEmpty()) {
                    cacheText c = new cacheText();
                    cachett.add(c);
                    c.start();
                }
                readText2();
                return;
            }
            new LongTask().execute(list.get(Integer.valueOf(index)).get("catalogueurl"));
            if(cachett.isEmpty()) {
                cacheText c = new cacheText();
                cachett.add(c);
                c.start();
            }
            if(m!=null) {
                if (isExist == 1) {
                    sqLiteDatabase = getDBHelper(ReadNovel.this).getWritableDatabase();
                    sqLiteDatabase.execSQL("update " + bookshelfdatabase.BookInfo.TABLE_NAME + " set uptime=? where _id=? ", new Object[]{"" + index, id});
                    sqLiteDatabase.close();
                }
            }
        }
    }


    public void showdialog()
    {
        dialog=new Dialog(this,R.style.dialog1);
        dialog.setContentView(R.layout.buttomcomponent);

        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.BOTTOM);
        dialogWindow.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        lp.width=WindowManager.LayoutParams.FILL_PARENT;
        dialogWindow.setAttributes(lp);
        dialog.show();

        seek = dialog.findViewById(R.id.seekbar);
        seek.setMax(maxchpater - 1);
        seek.setProgress(index);
        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                index = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(listflag) {
                    HashMap<String, String> hh = list.get(index);
                    hashMap.put("nexturl", hh.get("catalogueurl"));
                    lastornext = 1;
                    scflag = false;
                    flag = 2;
                    cachett.clear();
                    cacheText c = new cacheText();
                    c.f=1;
                    c.flag2=true;
                    cachett.add(c);
                    c.start();
                    showText();
                }
                else {
                    Toast.makeText(ReadNovel.this, "无法获取目录", Toast.LENGTH_SHORT).show();
                }
            }
        });
        final Button last = dialog.findViewById(R.id.lastchapter);
        last.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listflag) {
                    if (index - 1 >= 0) {
                        index--;
                        seek.setProgress(index);
                        lastornext = 3;
                        scflag = false;
                        flag = 2;
                        showText();
                    }
                }
                else {
                    Toast.makeText(ReadNovel.this, "无法获取目录", Toast.LENGTH_SHORT).show();
                }
            }
        });
        final Button next = dialog.findViewById(R.id.nextchapter);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listflag) {
                    if (index + 1 < maxchpater) {
                        index++;
                        seek.setProgress(index);
                        lastornext = 3;
                        scflag = false;
                        flag = 2;
                        showText();

                    }
                }
                else {
                    Toast.makeText(ReadNovel.this, "无法获取目录", Toast.LENGTH_SHORT).show();
                }
            }
        });
        final Button button=dialog.findViewById(R.id.night);
        String nd=sharedPreferences.getString("nd","");
        if(nd!="")
        {
            button.setText(nd);
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor=sharedPreferences.edit();
                if(button.getText().equals("夜间"))
                {
                    t1.setBackgroundColor(Color.BLACK);
                    t1.setTextColor(Color.WHITE);
                    t3.setBackgroundColor(Color.BLACK);
                    t3.setTextColor(Color.WHITE);
                    button.setText("白天");
                    editor.putString("nd","白天");
                    editor.apply();
                }
                else
                {
                    int y=sharedPreferences.getInt("textcolor",-2);
                    if(y!=-2)
                    {
                        changecolors(y);
                    }
                    else
                    {
                        t1.setBackgroundColor(Color.WHITE);
                        t1.setTextColor(Color.BLACK);
                        t3.setBackgroundColor(Color.WHITE);
                        t3.setTextColor(Color.BLACK);
                    }
                    button.setText("夜间");
                    editor.putString("nd","夜间");
                    editor.apply();
                }
            }
        });
    }
    class cacheText extends Thread{
        HashMap<String, String> h=new HashMap<>();
        int f=1;
        boolean flag2=true;
        @Override
        public void run() {
            super.run();
            boolean flag=true;
            int y=index;
            File file = null,dir;
            if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                flag=true;
                file= new File(Environment.getExternalStorageDirectory().getAbsoluteFile()+ "/Book/"+bookname);
            }
            else {
                flag=false;
            }
            if(y>=0&&flag)
            {
                for(int i=y+1;i<y+8&&i<list.size();i++)
                {
                    dir= new File(file, i + ".txt");
                    if(!dir.exists()&&flag2)
                    {
                        if (f==1) {
                            h = selectcrawlmethod.selectmethod(list.get(i).get("catalogueurl"));
                            if(h.isEmpty())
                            {
                                flag2=false;
                                return;
                            }
                            if(h.get("text")!=null) {
                                readAndWriteBook.writeBook(h.get("text"), String.valueOf(i));
                                f=2;
                            }
                        }
                        else {
                            if(h.get("nexturl")!=null) {
                                h = selectcrawlmethod.selectmethod(h.get("nexturl"));
                                if(h.get("text")!=null) {
                                    readAndWriteBook.writeBook(h.get("text"), String.valueOf(i));
                                }
                            }
                        }
                    }
                }
            }
            cachett.remove(this);
        }
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