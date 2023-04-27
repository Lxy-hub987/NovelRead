package com.example.novelreading;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;

public class QidaintoYmoxuan {

    String url="https://www.ymoxuan.com/search.htm?keyword=";
    String url2="";
    String url3="";
    String text=null;
    Document doc = null;
    HashMap<String,String> hashMap=new HashMap<>();
    public HashMap<String,String> gettext(final String title, final String bookname) {
//        Log.i("1","1");
        Thread t = new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    doc = Jsoup.connect(url+bookname).timeout(50000).userAgent("Mozilla/5.0").get();


                    Elements elements=doc.select("span.n2");
                    for(Element element:elements)
                    {
                        Elements elements1=element.select("a");
                        if(elements1.text().trim().equals(bookname)) {
                            url2="http:"+elements1.attr("href");
                            break;
                        }
                    }

                    getcatalogue(title);


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
        return hashMap;
    }
    public void getcatalogue(final String title)
    {
        Thread t = new Thread() {
            @Override
            public void run() {
                super.run();

                try {
                    doc = Jsoup.connect(url2).timeout(50000).userAgent("Mozilla/5.0").get();
                    Elements elements=doc.select("a.btn.btn-info");
                    for(Element element:elements)
                    {
                        if(element.text().trim().equals("章节目录"))
                        {
                            url3="http:"+element.attr("href");
                        }
                    }
                    geturl(title);
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
    public void geturl(final String title)
    {
        final CrawlymoxuanContent crawlymoxuanContent=new CrawlymoxuanContent();
        Thread t = new Thread() {
            @Override
            public void run() {
                super.run();
                String url4=null;
                try {
                    doc = Jsoup.connect(url3).timeout(50000).userAgent("Mozilla/5.0").get();

                    Elements elements=doc.select("li.col3");
                    for(Element element:elements)
                    {
                        Elements elements1=element.getElementsByTag("a");
                        if(elements1.text().trim().equals(title))
                        {
                            url4="http:"+elements1.attr("href");
                            break;
                        }
                    }
                    if(url4==null)
                    {
                        hashMap=null;
                        return;
                    }
                    hashMap=crawlymoxuanContent.gettext(url4);
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
}
