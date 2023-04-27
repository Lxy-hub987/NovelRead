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

public class Crawlrecommend {

    List<HashMap<String, String>> list =new ArrayList<HashMap<String, String>>();
    private String Url=null;
    private String baseurl=null;
    int page=1;

    public List<HashMap<String, String>> getcatalogue(String url) {
        list.clear();
        this.baseurl=url;
        this.Url = url + page;
        gettext();
        return list;
    }
    public void gettext(){
        while (true) {
            Thread t = new Thread() {
                @Override
                public void run() {

                    Document doc = null;
                    try {
                        doc = Jsoup.connect(Url).timeout(5000).userAgent("Mozilla/5.0").get();
                        Elements elements = doc.select("div.book-img-text");
                        Elements elements1 = elements.select("li");
                        for (Element element : elements1) {
                            HashMap<String, String> hashMap = new HashMap<>();
                            //图片
                            Elements elements2 = element.getElementsByTag("img");
                            hashMap.put("imgsrc", "http:" + elements2.attr("src"));

                            Elements elements3 = element.select("div.book-mid-info");
                            // 书名
                            Elements elementstitle = elements3.select("h4");
                            hashMap.put("bookname", elementstitle.text());
                            //链接
                            Elements elements4 = elementstitle.select("a");
                            hashMap.put("url", "http:" + elements4.attr("href"));
                            //作者
                            Elements elementsauthor = elements3.select("a.name");
                            hashMap.put("author", elementsauthor.text());
                            //类型
                            Elements elementtype = elements3.select("p.author");
                            Elements eee=elementtype.select("a");
                            int i = 0;
                            for (Element e : eee) {
                                i++;
                                if (i == 2) {
                                    hashMap.put("type", e.text());
                                    break;
                                }
                            }

                            //简介
                            Elements elementsintro = elements3.select("p.intro");
                            hashMap.put("details", elementsintro.text());

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
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                continue;
            }
            if (page < 6) {
                page++;
                Url=baseurl+page;
            }
            else {
                page = 1;
                return;
            }
        }
    }
}
