package com.algorithm.sort;

/**
 * Desciption
 *
 * @author Claire.Chen
 * @create_time 2019 -01 - 14 11:06
 */

import java.util.Arrays;

/**
 * 选择排序：在开始序列中查找最小的，放在第一个，然后再在剩下的序列当中查找最小的，放在第二个
 */
public class ChoiceSort {
    public  static  void main(String[] args){
        Integer[] arr = new Integer[]{1,66,8,3,6,90,45};
        for(int i = 0 ; i < arr.length ; i ++ ){
            int min = i;
            for( int j = i+1; j< arr.length;j++){
              if(arr[j] < arr[min]){
                 min = j;
              }
            }
            if(min != i){
                int tmp = arr[min];
                arr[min] = arr[i];
                arr[i] = tmp;
            }
        }

        System.out.println(Arrays.toString(arr));
    }
}
