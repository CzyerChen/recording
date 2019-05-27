package com.basic.random;

import java.util.HashMap;
import java.util.Random;

/**
 * 从末尾开始依次和前面的位随意交换
 */
public class ShuffleTest {
    private static void swap(char[] chars ,int i,int j){
        char tmp = chars[i];
        chars[i] = chars[j] ;
        chars[j] =tmp;
    }

    public static void shuffle(char[] chars ){
        Random r = new Random();
        int size = chars.length;
        for(int i = size;i>1;i--){
            swap(chars,i-1,r.nextInt(i));
        }
    }
    public static void main(String[] args){

      char[] chars = {'a','c','3','7','k','p','0'};
      ShuffleTest.shuffle(chars);
        String s = chars.toString();
    }
}
