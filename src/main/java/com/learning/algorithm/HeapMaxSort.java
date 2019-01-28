package com.learning.algorithm;

/**
 * Desciption
 *
 * @author Claire.Chen
 * @create_time 2019 -01 - 14 16:20
 */

import java.util.Arrays;

/**
 * 最大堆堆排
 */

/**
 * 先将数组元素逐个插入建立最大堆，然后依次从最小堆拿出最小的元素，然后调整最小堆，直到拿完，分为建堆和删除最小元素两步
 */
public class HeapMaxSort {

    public static void main(String[] args){
        int[] arr = new int[]{1,66,8,3,6,90,45};
        HeapMaxSort sort = new HeapMaxSort();
        sort.buildHeap(arr,arr.length);
        System.out.println(Arrays.toString(arr));
        sort.deleteMin(arr);
        System.out.println(Arrays.toString(arr));
    }

    public void buildHeap(int[] arr,int len){
        for(int i = len /2 -1; i>=0 ; i--){
            sink(arr,i,arr.length);
        }
    }

    public void deleteMin(int[] arr){
        for( int i = arr.length -1 ;i>=0 ;i--){
            swap(arr,0,i);
            sink(arr,0,i);
        }



    }


    void shiftdown(int[] arr ,int i,int len) { //传入一个需要向下调整的结点编号i
        int t, flag = 0; //flag用来标记是否需要继续向下调整
        while (i * 2 < len && flag == 0) {
            //首先判断它和左儿子的关系，并用t记录值较小的节点编号
            if (arr[i] > arr[i*2]) {
                t = i*2;
            } else {
                t = i;
            }

            //如果它有右儿子，再对右儿子进行讨论
            if (i*2 + 1 < len) {
                //如果它的右儿子的值更小，更新较小的结点编号
                if (arr[t] < arr[i*2 + 1]){
                    t = i * 2 + 1;
                }
            }

            //如果发现最小的编号不是自己，说明子结点中有比父节点更小的
            if (t != i) {
                swap(arr,t, i);
                i = t;
            } else {
                flag = 1;
            }
        }
    }


    public void sink(int[] arr,int i,int len ){
        int tmp = arr[i];
        for(int k = i*2+1;k<len;k = k*2+1){
            if(k+1 < len && arr[k] < arr[k+1]){
                k++;
            }
            if(arr[k] > tmp){
                arr[i] = arr[k];
                i = k;
            }else {
                break;
            }
        }
        arr[i] = tmp;
      /* int small =i;
       for(int k = i*2+1;k<len;k = k*2+1){
           if(k<len && arr[k] < arr[small]){
               small = k;
           }
           if(k+1 < len && arr[k+1] <arr[small]){
               small = k+1;
           }
           if(small ==i){
               return;
           }
           swap(arr,i,small);
           sink(arr,small,len);
       }*/
    }
    /**
     * 交换元素
     * @param arr
     * @param a
     * @param b
     */
    public static void swap(int []arr,int a ,int b){
        int temp=arr[a];
        arr[a] = arr[b];
        arr[b] = temp;
    }
    public  void sort(int []arr){
        //1.构建大顶堆
        for(int i=arr.length/2-1;i>=0;i--){
            //从第一个非叶子结点从下至上，从右至左调整结构
            adjustHeap(arr,i,arr.length);
        }
        System.out.println(Arrays.toString(arr));
        //2.调整堆结构+交换堆顶元素与末尾元素
        for(int j=arr.length-1;j>0;j--){
            swap(arr,0,j);//将堆顶元素与末尾元素进行交换
            adjustHeap(arr,0,j);//重新对堆进行调整
        }

    }

    /**
     * 调整大顶堆（仅是调整过程，建立在大顶堆已构建的基础上）
     * @param arr
     * @param i
     * @param length
     */
    public  void adjustHeap(int []arr,int i,int length){
        int temp = arr[i];//先取出当前元素i
        for(int k=i*2+1;k<length;k=k*2+1){//从i结点的左子结点开始，也就是2i+1处开始
            if(k+1<length && arr[k]<arr[k+1]){//如果左子结点小于右子结点，k指向右子结点
                k++;
            }
            if(arr[k] > temp){//如果子节点大于父节点，将子节点值赋给父节点（不用进行交换）
                arr[i] = arr[k];
                i = k;
            }else{
                break;
            }
        }
        arr[i] = temp;//将temp值放到最终的位置
    }

}
