package com.example.novelreading;

public class bookshelfdatabase {

    private bookshelfdatabase(){
    }
    public static final String DATABASE_NAME="bookdatabase.db";
    public static final int DATABASE_VERSION=1;

    public static abstract class BookInfo
    {
        public static final String TABLE_NAME="BookInfo";
        //created a cloumn
        public static final String _ID="_id";
        public static final String BOOK_NAME="bookname";
        public static final String AUTHOR="author";
        public static final String COMPLETENESS="completeness";
        public static final String UPTIME="uptime";
        public static final String NEWCHAPTER="newchapter";
        public static final String PAGE_NUM="page_num";
        public static final String NOWURL="nowurl";
        public static final String STARTURL="starturl";
        public static final String IMGSRC="imgsrc";

        public static final String CREATE_TABLE="create table " + TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                BOOK_NAME + " TEXT," +
                AUTHOR + " TEXT," +
                COMPLETENESS + " TEXT," +
                UPTIME + " TEXT," +
                NEWCHAPTER + " TEXT," +
                PAGE_NUM + " TEXT," +
                IMGSRC  + " TEXT," +
                NOWURL + " TEXT," +
                STARTURL + " TEXT)";
    }
}
