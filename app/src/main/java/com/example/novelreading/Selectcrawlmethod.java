package com.example.novelreading;

import java.util.HashMap;

public class Selectcrawlmethod {

    HashMap<String,String> hashMap=new HashMap<>();
    public HashMap<String,String> selectmethod(String url)
    {
//        Log.i("surl",url);
        if(url.contains("qidian"))
        {
//            Log.i("1","1");
            CrawlqidianContent crawlqidianContent=new CrawlqidianContent();
            hashMap=crawlqidianContent.gettext(url);
        }
        else if(url.contains("ymoxuan"))
        {
//            Log.i("1","2");
            CrawlymoxuanContent crawlymoxuanContent=new CrawlymoxuanContent();
            hashMap=crawlymoxuanContent.gettext(url);
        }
//        Log.i("1","3");
        return hashMap;
    }
}
