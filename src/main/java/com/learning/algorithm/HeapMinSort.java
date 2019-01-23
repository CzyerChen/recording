package com.learning.algorithm;

import java.util.Arrays;

/**
 * Desciption
 *
 * @author Claire.Chen
 * @create_time 2019 -01 - 15 12:56
 */
public class HeapMinSort {
    public static void main(String[] args) {
        int[] arr = new int[]{1, 66, 8, 3, 6, 90, 45};
        HeapMinSort sort = new HeapMinSort();
        for (int i = (arr.length - 1) / 2; i >= 0; i--) {
            sort.shiftDown(arr, i, arr.length);
        }

       for(int j = arr.length -1 ; j >=0 ;j-- ){
            sort.swap(arr,0,j);
            sort.shiftDown(arr,0,j);
        }
        System.out.println(Arrays.toString(arr));
    }

    public void shiftDown(int[] a, int i, int len) {
        int j = i * 2 + 1;
        while (j < len) {
            if (j + 1 < len && a[j + 1] < a[j]) {
                j++;
            }
            if (a[i] < a[j]) {
                break;
            }
           /* a[i] = a[j];*/
            swap(a,i,j);
            i = j;
            j = 2 * i + 1;
        }
    }
    public void swap(int []arr,int a ,int b){
        int temp=arr[a];
        arr[a] = arr[b];
        arr[b] = temp;
    }

}
