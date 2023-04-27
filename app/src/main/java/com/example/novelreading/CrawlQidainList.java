package com.example.novelreading;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CrawlQidainList {
    List<HashMap<String, String>> list =new ArrayList<HashMap<String, String>>();
    int i=0;
    private String Url=null;

    public List<HashMap<String, String>> getcatalogue(String url)
    {
        this.Url=url;
        list.clear();
        while(list.isEmpty()&&i<3) {
            Thread t = new Thread() {
                @Override
                public void run() {

                    Document doc = null;
                    try {
                        doc = Jsoup.connect(Url).timeout(5000).userAgent("Mozilla/5.0").get();
                        Elements elements = doc.select("ul.all-img-list.cf");
                        Elements elements1 = elements.select("li");
                        for (Element element : elements1) {
                            HashMap<String, String> hashMap = new HashMap<>();
                            //图片
                            Elements elements2 = element.getElementsByTag("img");
                            hashMap.put("imgsrc", "http:" + elements2.attr("src"));

                            Elements elements3 = element.select("div.book-mid-info");
                            // 书名
                            Elements elementstitle = elements3.select("h2");
                            hashMap.put("bookname", elementstitle.text());
                            //链接
                            Elements elements4 = elementstitle.select("a");
                            hashMap.put("url", "http:" + elements4.attr("href"));
                            //作者
                            Elements elementsauthor = elements3.select("a.name");
                            hashMap.put("author", elementsauthor.text().split(" ")[0]);
                            //简介
                            Elements elementsintro = elements3.select("p.intro");
                            hashMap.put("details", elementsintro.text());
                            list.add(hashMap);
                        }


                    } catch (IOException e) {
                        e.printStackTrace();
                        try {
                            i++;
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
        i=0;
        return list;
    }
}
