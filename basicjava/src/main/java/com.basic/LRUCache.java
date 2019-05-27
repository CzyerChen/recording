package com.basic;

import java.util.LinkedHashMap;
import java.util.Map;

public class LRUCache<K,V> extends LinkedHashMap<K,V> {
    private int maxSize;

    LRUCache(int maxSize){
        super(16,0.75f,true);
        this.maxSize = maxSize;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K,V> eldest) {
        return size()>maxSize;
    }


    public  static void main(String[] args){
        LRUCache<String,Object> lruCache = new LRUCache<>(3);
        lruCache.put("2222",9888);
        lruCache.put("1",99);
        lruCache.put("6","sss");
        lruCache.get("1");
        lruCache.replace("1",99,"9999");

        lruCache.put("010","010");
        System.out.print(lruCache);
    }
}
