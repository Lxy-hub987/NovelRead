package com.example.novelreading;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CrawlWebCatalogue {

    List<HashMap<String, String>> list =new ArrayList<HashMap<String, String>>();

    private String Url="https://read.qidian.com/ajax/book/category?_csrfToken=gVwIGc53NS73hJlFMrjH1p7tZiXwrulDfFxO82Fx&bookId=";
    Boolean flag=true;

    public List<HashMap<String, String>> getcatalogue(final String url) {
        this.Url += url;
        int i=0;
        list.clear();
        while (i < 3&&list.isEmpty()) {
            Thread t = new Thread() {
                @Override
                public void run() {

                    Document doc = null;
                    try {
                        doc = Jsoup.connect(Url).timeout(5000).userAgent("Mozilla/5.0").get();

                        String[] ss = doc.text().trim().split("uuid");
                        for (int i = 1; i < ss.length - 1; i++) {
                            HashMap<String, String> hashMap = new HashMap<>();
                            String[] ss2 = ss[i].split("\"");
                            hashMap.put("catalogue", ss2[4]);
                            char[] c = ss2[ss2.length - 1].toCharArray();
                            if (c[1] == '1' && flag) {
                                hashMap.put("catalogueurl", "https://read.qidian.com/chapter/" + ss2[14]);
                            } else {
                                flag = false;
                                hashMap.put("catalogueurl", "https://vipreader.qidian.com/chapter/" + url + "/" + ss2[17].substring(1, ss2[17].length() - 1));
                            }
                            list.add(hashMap);
                        }
                        HashMap<String, String> hashMap = new HashMap<>();
                        String[] ss2 = ss[ss.length - 1].split("\"");
                        hashMap.put("catalogue", ss2[4]);
                        if (flag) {
                            hashMap.put("catalogueurl", "https://read.qidian.com/chapter/" + ss2[14]);
                        } else {
                            hashMap.put("catalogueurl", "https://vipreader.qidian.com/chapter/" + url + "/" + ss2[17].substring(1, ss2[17].length() - 1));
                        }
                        list.add(hashMap);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.i("message", "连接失败");
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
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

        return list;
    }
}
