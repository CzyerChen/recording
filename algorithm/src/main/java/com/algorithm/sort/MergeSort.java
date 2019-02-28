package com.algorithm.sort;

/**
 * Desciption
 *
 * @author Claire.Chen
 * @create_time 2019 -01 - 14 12:51
 */

import java.util.Arrays;

/**
 * 归并排序：分为分-> 合两步，分是数组分到最小的组合，最后将两个组合分别合并起来
 */
public class MergeSort {

    public static void main(String[] args) {
        int[] arr = new int[]{1, 66, 8, 3, 6, 90, 45};
        MergeSort sort = new MergeSort();
        int[] tmp = new int[arr.length];
        sort.mergeSort(arr,tmp,0,arr.length -1);
        System.out.println(Arrays.toString(arr));
    }


    public void mergeSort(int[] a, int[] tmp, int l, int r) {
        if (l < r) {
            int center = (l + r) / 2;
            mergeSort(a, tmp, l, center);
            mergeSort(a, tmp, center+1, r);
            merge(a, tmp, l, r, center);
        }
    }

    public void merge(int[] a, int[] tmp, int l, int r, int center) {
        int i = l;
        int j = center + 1;
        for (int k = l; k <= r; k++) {
            if(i > center){
                tmp[k]= a[j++];
            }
            else  if(j >r){
                tmp[k]  =a[i++] ;
            } else  if(a[i] <= a[j]){
                tmp[k] = a[i++];
            }else {
                tmp[k] = a[j++];
            }
        }
       System.arraycopy(tmp,l,a,l,r-l +1);
        /*for(int t = l ;t<= r ; t++){
            a[t] = tmp[t];
        }*/
    }


}
