package com.example.novelreading;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;

public class CrawlqidianContent {

    String text=null;
    String url;
    Document doc = null;
    String nexturl=null;
    String lasturl=null;
    HashMap<String,String> hashMap=new HashMap<>();
    public HashMap<String,String> gettext(final String url2)
    {
        url=url2;
        Thread t=new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    doc= Jsoup.connect(url).timeout(5000).userAgent("Mozilla/5.0").get();
                    Elements elements1=doc.select("div.vip-limit-wrap");
                    if(elements1.text().trim().length()!=0)
                    {
                        QidaintoYmoxuan qidaintoYmoxuan=new QidaintoYmoxuan();
                        Elements elements=doc.select("div.text-head");
                        Elements elements3=doc.select("a.act");
                        Elements elements2=elements.select("h3");
                        hashMap=qidaintoYmoxuan.gettext(elements2.text(),elements3.text());
                    }
                    else
                    {
                        Elements e=doc.select("h3.j_chapterName");
                        hashMap.put("chpater",e.text());
                        Elements elements=doc.select("div.read-content.j_readContent");
                        String text1 = elements.toString();
                        Document d = Jsoup.parse(text1);
//                        text = Jsoup.clean(d.toString(), "", Whitelist.none(), new Document.OutputSettings().prettyPrint(false));
                        text = d.toString();
                        hashMap.put("text",text);
                        Elements elements3=doc.select("div.chapter-control.dib-wrap");
                        Elements elements2=elements3.select("a");
                        for(Element element:elements2)
                        {
                            if(element.text().trim().equals("上一章"))
                            {
                                String lasturl=null;
                                lasturl="https:"+element.attr("href");
                                if(!lasturl.matches("^.*(qidian).*+$"))
                                {
                                    lasturl=null;
                                }
                                hashMap.put("lasturl",lasturl);
                            }
                            if(element.text().trim().equals("下一章"))
                            {
                                String nexturl=null;
                                nexturl="https:"+element.attr("href");
                                if(!nexturl.matches("^.*(qidian).*+$"))
                                {
                                    nexturl=null;
                                }
                                hashMap.put("nexturl",nexturl);
                                break;
                            }
                        }
                        hashMap.put("nowurl",url2);
                    }

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
}
