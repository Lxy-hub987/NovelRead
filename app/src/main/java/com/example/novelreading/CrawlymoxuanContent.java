package com.example.novelreading;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;

public class CrawlymoxuanContent {

    Document doc = null;
    String url="";
    String text;
    int i=0;
    public HashMap<String,String> gettext(final String url2) {
        this.url=url2;
//        Log.i("u",url2);
        final HashMap<String,String> hashMap=new HashMap<>();

        Thread t = new Thread() {
            @Override
            public void run() {
                super.run();
                while(hashMap.isEmpty()&&i<3) {
                    try {
                        doc = Jsoup.connect(url).timeout(50000).userAgent("Mozilla/5.0").get();

                        Elements elements = doc.select("div.content");
                        String text1 = elements.toString();
                        Document d = Jsoup.parse(text1);
//                        text = Jsoup.clean(d.toString(), "", Whitelist.none(), new Document.OutputSettings().prettyPrint(false));
                        text = d.toString();
//                    text=elements.text();
                        hashMap.put("text", text);
                        Elements eetitle = doc.select("header.clearfix");
                        Elements tiltle = eetitle.select("h1");
                        hashMap.put("chpater", tiltle.text());
                        Elements ele = doc.select("div.operate");
                        Elements e2 = ele.select("li");
                        for (Element e : e2) {
                            Elements ee = e.select("a");
                            if (ee.text().trim().equals("上一章")) {
                                String lasturl = null;
                                lasturl = "https:" + ee.attr("href");
                                if (!lasturl.matches("^.*(ymoxuan).*+$") && !lasturl.contains("index")) {
                                    lasturl = null;
                                }
                                hashMap.put("lasturl", lasturl);
                            }
                            if (ee.text().trim().equals("下一章")) {
                                String nexturl = null;
                                nexturl = "https:" + ee.attr("href");
                                if (!nexturl.matches("^.*(book).*+$")) {
                                    nexturl = null;
                                }
                                hashMap.put("nexturl", nexturl);
                            }
                        }
                        hashMap.put("nowurl", url2);

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
            }
        };

        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        i=0;
        return hashMap;
    }
}
