package com.example.novelreading;

import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

public class ReadAndWriteBook {
    File dir;
    boolean flag=true;
    public ReadAndWriteBook(String name)
    {
        if(isSDCARDMounted()){
            File root = Environment.getExternalStorageDirectory();
            dir = new File(root.getPath() + "/Book/"+name);
            boolean b = false;
            if (!dir.exists()) {
                b = dir.mkdirs();
            }
            if(b){
                flag = true;
            }else {
                flag=false;
            }
        }
        else {
            flag=false;
        }

    }

    private boolean isSDCARDMounted() {
        String status = Environment.getExternalStorageState();

        if (status.equals(Environment.MEDIA_MOUNTED))
            return true;
        return false;
    }
    public void writeBook(String text,String num)
    {
        if(flag) {
            File file = new File(dir, num + ".txt");

            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(text.toString().getBytes());
                fileOutputStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public String readBook(String num)
    {
        if(flag) {
            File file = new File(dir, num + ".txt");
            if (file.exists()) {
                FileInputStream fileInputStream = null;
                try {
                    fileInputStream = new FileInputStream(file);
                    BufferedReader bufferedReader=new BufferedReader(new FileReader(file));
                    String s="";
                    StringBuilder sb = new StringBuilder();
                    while((s=bufferedReader.readLine())!=null)
                    {
                        sb.append(s+"\n");
                    }
                    fileInputStream.close();
                    return sb.toString();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
        return null;
    }
}
