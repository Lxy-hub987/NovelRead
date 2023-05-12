package com.example.novelreading;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CrawlSearch {

    List<HashMap<String, String>> list =new ArrayList<HashMap<String, String>>();
    String baseurl1="https://www.qidian.com/so/";
    String baseurl2=".html";
    private String Url=null;
    public List<HashMap<String, String>> getsearch(String searchinfo,int page)
    {
        this.Url=baseurl1+searchinfo+baseurl2;
        final Thread t= new Thread() {
            @Override
            public void run() {

                Document doc = null;
                try {
//                    Log.i("Url","4");
                    doc = Jsoup.connect(Url).timeout(5000).userAgent("Mozilla/5.0").get();
//                    Log.i("Url","4");
                    Elements elements=doc.select("div.book-img-text");
                    Elements elements1=elements.select("li");
                    for(Element element:elements1)
                    {
                        HashMap<String,String> hashMap=new HashMap<>();
                        //图片
                        Elements elements2=element.getElementsByTag("img");
                        hashMap.put("imgsrc","https:"+elements2.attr("src"));

                        Elements elements3=element.select("div.book-mid-info");
                        // 书名
                        Elements elementstitle=elements3.select("h3");
                        hashMap.put("bookname",elementstitle.text());
                        //链接
                        Elements elements4=elementstitle.select("a");
                        hashMap.put("url","http:"+elements4.attr("href"));
                        //作者
                        Elements elementsauthor=elements3.select("p.author");
                        Elements elements5=elementsauthor.select("a");
                        String author=null;
                        String type=null;
                        for(Element e:elements5)
                        {
                            if(author==null)
                            {
                                author=e.text();
                                hashMap.put("author",author);
                            }
                            else
                            {
                                type=e.text();
                                hashMap.put("type",type);
                                break;
                            }
                        }
                        //简介
                        Elements elementsintro=elements3.select("p.intro");
                        hashMap.put("details",elementsintro.text());
                        list.add(hashMap);
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                    Log.i("message", "连接失败");
                }
            }
        };
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        return list;
    }
}
