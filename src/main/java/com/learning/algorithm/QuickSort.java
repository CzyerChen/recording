package com.learning.algorithm;

/**
 * Desciption
 *
 * @author Claire.Chen
 * @create_time 2019 -01 - 14 11:22
 */

import java.util.ArrayList;
import java.util.Arrays;

/**
 * 它的基本思想是：选择一个基准数，通过一趟排序将要排序的数据分割成独立的两部分；其中一部分的所有数据都比另外一部分的所有数据都要小。
 * 然后，再按此方法对这两部分数据分别进行快速排序，整个排序过程可以递归进行，以此达到整个数据变成有序序列。
 */

/**
 * 快速排序是由东尼·霍尔所发展的一种排序算法。在平均状况下，排序 n 个项目要Ο(n log n)次比较。在最坏状况下则需要Ο(n2)次比较，但这种状况并不常见。
 * 事实上，快速排序通常明显比其他Ο(n log n) 算法更快，因为它的内部循环（inner loop）可以在大部分的架构上很有效率地被实现出来。
 */
public class QuickSort {
    public static void main(String[] args) {
      /*  int[] arr = new int[]{1, 66, 8, 3, 6, 90, 45};
        QuickSort sort  = new QuickSort();
        sort.quickSort(arr,0,arr.length-1);
        System.out.println(Arrays.toString(arr));*/
      String a= "aaa.bbb";
      String[] h = a.split("\\.");
      String[] k = new String[10];
      System.arraycopy(h,0,k,0,h.length);
      System.out.println(Arrays.toString(k));

    }

    void quickSort(int[] a, int left, int right) {
        if (left < right) {
            int i = left;
            int j = right;
            int x = a[i];
            while(i<j){
                while (i < j && a[j] > x) {
                    j--;
                }
                if (i < j) {
                    a[i++] = a[j];
                }
                while(i <j && a[i] < x){
                    i ++;
                }
                if(i<j){
                    a[j--] = a[i];
                }
            }

            a[i] = x;
            quickSort(a,left,i-1);
            quickSort(a,i+1,right);
        }
    }
}
