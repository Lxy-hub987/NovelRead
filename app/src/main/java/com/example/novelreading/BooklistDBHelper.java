package com.example.novelreading;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BooklistDBHelper extends SQLiteOpenHelper {

    public BooklistDBHelper(Context context) {
        super(context,bookshelfdatabase.DATABASE_NAME, null,bookshelfdatabase.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(bookshelfdatabase.BookInfo.CREATE_TABLE);
    }

    public void insertData(String bookname,String author,String completeness,String uptime,String newchapter,String page_num,String imgsrc,String nowurl,String starturl,SQLiteDatabase db)
    {
        ContentValues contentValues=new ContentValues();
        contentValues.put(bookshelfdatabase.BookInfo.BOOK_NAME,bookname);
        contentValues.put(bookshelfdatabase.BookInfo.AUTHOR,author);
        contentValues.put(bookshelfdatabase.BookInfo.COMPLETENESS,completeness);
        contentValues.put(bookshelfdatabase.BookInfo.UPTIME,uptime);
        contentValues.put(bookshelfdatabase.BookInfo.NEWCHAPTER,newchapter);
        contentValues.put(bookshelfdatabase.BookInfo.PAGE_NUM,page_num);
        contentValues.put(bookshelfdatabase.BookInfo.IMGSRC,imgsrc);
        contentValues.put(bookshelfdatabase.BookInfo.NOWURL,nowurl);
        contentValues.put(bookshelfdatabase.BookInfo.STARTURL,starturl);
        db.insert(bookshelfdatabase.BookInfo.TABLE_NAME,null,contentValues);
    }
    public Cursor selectAll(SQLiteDatabase db)
    {
        Cursor cursor;
        String[] projection={bookshelfdatabase.BookInfo._ID,bookshelfdatabase.BookInfo.BOOK_NAME,bookshelfdatabase.BookInfo.AUTHOR,bookshelfdatabase.BookInfo.COMPLETENESS,bookshelfdatabase.BookInfo.UPTIME,bookshelfdatabase.BookInfo.NEWCHAPTER,bookshelfdatabase.BookInfo.PAGE_NUM,bookshelfdatabase.BookInfo.IMGSRC,bookshelfdatabase.BookInfo.NOWURL,bookshelfdatabase.BookInfo.STARTURL};
        cursor=db.query(bookshelfdatabase.BookInfo.TABLE_NAME,projection,null,null,null,null,null);
        if(cursor!=null)
        {
            cursor.moveToFirst();
        }

        return cursor;
    }
    public void  deleteTable(SQLiteDatabase db)
    {
        db.execSQL("delete from "+bookshelfdatabase.BookInfo.TABLE_NAME);
    }

    public void dropTable(SQLiteDatabase db) {
        db.execSQL("drop table "+bookshelfdatabase.BookInfo.TABLE_NAME);
    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
