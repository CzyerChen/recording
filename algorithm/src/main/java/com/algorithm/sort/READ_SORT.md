### 冒泡排序
冒泡排序的思想：每次都是相邻的两个比较大小，根据递增递减自己调整比较的形式
```java
class Test{
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

```

### 选择排序
选择排序的思想：在开始序列中查找最小的，放在第一个，然后再在剩下的序列当中查找最小的，放在第二个
```java
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
```

### 最大堆排序
最大堆排序的思想：先将数组元素逐个插入建立最大堆，然后依次从最小堆拿出最小的元素，然后调整最小堆，直到拿完，分为建堆和删除最小元素两步
```java
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
```

### 最小堆排序
```java
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

```

### 归并排序
归并排序的思想：分为分-> 合两步，分是数组分到最小的组合，最后将两个组合分别合并起来
```java
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
```

### 快速排序
快速排序的思想：选择一个基准数，通过一趟排序将要排序的数据分割成独立的两部分；其中一部分的所有数据都比另外一部分的所有数据都要小。然后，再按此方法对这两部分数据分别进行快速排序，整个排序过程可以递归进行，以此达到整个数据变成有序序列。
```java
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
```

