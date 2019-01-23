package com.learning.algorithm;

/**
 * Desciption
 *
 * @author Claire.Chen
 * @create_time 2019 -01 - 14 10:55
 */

import java.util.Arrays;

/**
 * 冒泡排序的思想：每次都是相邻的两个比较大小，根据递增递减自己调整比较的形式
 */
public class BubbleSort {

    public  static  void main(String[] args){
        int[] arr = new int[]{1,66,8,3,6,90,45};

        for(int i  = arr.length -1 ; i > 0 ; i--){
            boolean flag = true;
             for(int j = 0; j < i ; j ++){

                 if(arr[j] > arr[j+1]){
                     int tmp =arr[j];
                     arr[j]=arr[j+1];
                     arr[j+1]= tmp;
                     flag = false;
                 }
             }
           if(flag){
                break;
            }

        }

        System.out.println(Arrays.toString(arr));
    }


}
