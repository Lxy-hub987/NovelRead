package com.example.novelreading;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class CrawlWebDetils {
    String text;
    int i=0;
    ConcurrentHashMap<String,String> hashMap=new ConcurrentHashMap<>();
    String url3 = "";
    public ConcurrentHashMap<String,String> getUrl(String urltest){
        url3=urltest;
        hashMap.clear();

        Thread t = new Thread() {
            @Override
            public void run() {

                Document doc = null;
                while (hashMap.isEmpty()&&i<3) {
                    try {

                        doc = Jsoup.connect(url3).timeout(5000).userAgent("Mozilla/5.0").get();

                        Elements elements = doc.select("div.book-information.cf");
                        //作者
                        Elements elementsbookname = elements.select("div.book-info");
                        Elements elements1 = elementsbookname.select("a.writer");
                        String author = elements1.text();
                        hashMap.put("author", author);

                        //图片
                        Elements elementsimg = elements.select("div.book-img");
                        String src = null;
                        for (Element eimg : elementsimg) {
                            Elements elementsimg2 = eimg.getElementsByTag("img");
                            src = "https:" + elementsimg2.attr("src");
                            break;
                        }
                        hashMap.put("src", src);
                        //小说链接
                        Elements elementsurl = elements.select("a.red-btn.J-getJumpUrl");
                        hashMap.put("url", "https:" + elementsurl.attr("href"));

                        //完成情况，类型
                        Elements elementsdetails1 = elementsbookname.select("span.blue");
                        Elements elementtype = elementsbookname.select("a.red");
                        String completeness = null, type = null;
                        for (Element d1 : elementsdetails1) {
                            completeness = d1.text();
                            break;
                        }
                        for (Element d2 : elementtype) {
                            type = d2.text();
                            break;
                        }
                        hashMap.put("type", type);
                        hashMap.put("completeness", completeness);


                        //内容介绍
                        Elements elementsdetails2 = doc.select("div.book-intro");
                        String details = null;
                        for (Element ed : elementsdetails2) {
                            details = ed.text();
                            break;
                        }
                        hashMap.put("details", details);

                        //最新章节
                        Elements elementsnew1 = doc.select("li.update");
                        Elements elementsnew = elementsnew1.select("p.cf");
                        for(Element element: elementsnew){
                            Elements elementsnewchapter = element.select("a");
                            String newchapter = elementsnewchapter.text();
                            hashMap.put("newchapter", newchapter);

                            //距离上一次更新时间
                            Elements elementsuptime = element.select("em.time");
                            String time = elementsuptime.text();
                            hashMap.put("uptime", time + "更新");
                            break;
                        }


                    } catch (IOException e) {
                        e.printStackTrace();
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                        i++;
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
        i=0;
        return  hashMap;
    }
}
