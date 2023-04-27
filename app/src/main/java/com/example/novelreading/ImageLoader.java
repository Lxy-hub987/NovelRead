package com.example.novelreading;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;

public class ImageLoader {

    URL myFileUrl;
    Bitmap bitmap;
    boolean media_avaliable,media_writable;
    private final String path;
    //创建缓存
    private LruCache<String, Bitmap> mCaches;
    private Set<NewsAsyncTask> mTask;
    public ImageLoader(String path1)
    {
        path = path1+"/pic";
        mTask = new HashSet<>();
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 4;
        mCaches = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                //在每次存入缓存的时候调用
                return value.getByteCount();
            }
        };
    }

    public void savefile(String url,Bitmap bitmap)
    {

        String state=Environment.getExternalStorageState();

        if(Environment.MEDIA_MOUNTED.equals(state))
        {
            media_avaliable=media_writable=true;
        }
        else if(Environment.MEDIA_MOUNTED_READ_ONLY.equals(state))
        {
            media_avaliable=true;
            media_writable=false;
        }
        else
        {
            media_writable=media_avaliable=false;
        }
        if(media_writable&&media_avaliable) {


            String[] url2 = url.split("/");
            final String fileNa = url2[url2.length - 2].toLowerCase();

            File file = new File(path+ "/image/"+fileNa);
            boolean sdCardExist = Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
            if (sdCardExist) {
                File ad = new File(path + "/image");
//                 如果文件夹不存在
                if (!ad.exists()) {
//                     按照指定的路径创建文件夹
                    Log.i("创建文件夹", "开始创建文件夹");
                    boolean f = ad.mkdirs();
                    if(f){
                        Log.i("创建文件夹", "创建成功");
                    }else {
                        Log.i("创建文件夹", "创建失败");
                    }
                }
//                 检查图片是否存在
                if (!file.exists()) {
                    try {
                        Log.i("创建文件", "开始创建文件");
                        file.createNewFile();
                        Log.i("创建文件", "创建成功");
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.i("创建文件", "创建失败");
                    }
                }
            }

            BufferedOutputStream bos = null;
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    bos = new BufferedOutputStream(Files.newOutputStream(file.toPath()));
                }

                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                assert bos != null;
                bos.flush();
                bos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public Bitmap useTheImage(String imageUrl) {
        Bitmap bmpDefaultPic = null;
        String[] url2 = imageUrl.split("/");
        final String fileNa = url2[url2.length - 2];
        // 获得文件路径
        String imageSDCardPath = path
                + "/image/"
                + fileNa.toLowerCase();
        File file = new File(imageSDCardPath);
        // 检查图片是否存在
        if (!file.exists()) {
            return null;
        }
        bmpDefaultPic = BitmapFactory.decodeFile(imageSDCardPath, null);

        if (bmpDefaultPic != null || bmpDefaultPic.toString().length() > 3) {
            return bmpDefaultPic;
        } else {
            return null;
        }
    }
    public void addBitmapToCache(String url, Bitmap bitmap) {
        if (getBitmapFromCache(url) == null) {
            mCaches.put(url, bitmap);
        }
    }
    public Bitmap getBitmapFromCache(String url) {
        return mCaches.get(url);
    }

    private Bitmap getHttpImg(String src) {
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
    private  class  NewsAsyncTask extends AsyncTask<String, Void, Bitmap> {

        private String murl2;
        ImageView imageView;
        public NewsAsyncTask(ImageView imageView)
        {
            this.imageView=imageView;
        }
        @Override
        protected Bitmap doInBackground(String... strings) {
            murl2=strings[0];
            Bitmap bitmap2=getHttpImg(murl2);
            if (bitmap2 != null) {
                addBitmapToCache(murl2, bitmap2);
                savefile(murl2,bitmap2);
            }
            return bitmap2;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap2) {
            super.onPostExecute(bitmap2);
            if (imageView!=null&&bitmap2!=null&&imageView.getTag().equals(murl2)){
                imageView.setImageBitmap(bitmap2);
            }
            mTask.remove(this);
        }
    }
    public void loadImages2(ImageView imageView,String url) {
        //先从缓存中获取图片
        Bitmap bitmap = getBitmapFromCache(url);
        if (bitmap == null) {
            bitmap=useTheImage(url);
            if(bitmap==null) {
                imageView.setImageResource(0);
                NewsAsyncTask task = new NewsAsyncTask(imageView);
                task.execute(url);
                mTask.add(task);
            }else {
                imageView.setImageBitmap(bitmap);
                addBitmapToCache(url,bitmap);
            }
        } else {
            imageView.setImageBitmap(bitmap);
        }
    }
}
