/**
 * Author:   claire
 * Date:    2020-05-25 - 19:26
 * Description: learning lambda main class
 * History:
 * <author>          <time>                   <version>          <desc>
 * claire          2020-05-25 - 19:26          V1.0.0         learning lambda main class
 */
package com.basic.java8;

import com.sun.org.apache.regexp.internal.RE;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 功能简述 <br/>
 * 〈learning lambda main class 〉
 *
 * @author claire
 * @date 2020-05-25 - 19:26
 * @since 1.0.0
 */
public class LambdaMain {


    public static void main(String[] args) throws UnknownHostException {
        //IDEA配置java doc
        //InetAddress address = InetAddress.getByName("127.0.0.1");

        //尝试lambda书写方式
//        String content = "content";
//        FunctionLambdaClass.printContent(content, param -> System.out.println("结果：" + param));
//        //arrays sort
//        Integer[] arr = new Integer[4];
//        Arrays.sort(arr, (o1, o2) -> {
//            if (o1.equals(o2)) {
//                return 0;
//            } else if (o1 > o2) {
//                return 1;
//            } else {
//                return -1;
//            }
//        });

        //file listFiles
//        File file = new File("/tmp/Samples.txt");
//        if(file.exists()){
//            //代替FileFilter方法
//            File[] files1 = file.listFiles(File::isDirectory);
//            //方法引用
//            File[] files2 = file.listFiles(new FileFilter() {
//                @Override
//                public boolean accept(File pathname) {
//                    return pathname.isDirectory();
//                }
//            });
//        }

        //file list()
//        File file = new File("/tmp");
//        String[] files = file.list((dir, name) -> name.endsWith(".txt"));

        //files
//        File file = new File("/tmp");
//        File[] files = file.listFiles();
//        if(Objects.nonNull(files)) {
//            Arrays.sort(files);
//            Arrays.sort(files,(f1,f2)->f1.getPath().compareTo(f2.getPath()));
//            Arrays.sort(files,(f1,f2)->{
//               if(f1.isDirectory() && f2.isDirectory() || file.isFile() && f2.isFile()){
//                   return f1.getPath().compareTo(f2.getPath());
//               }else{
//                   if(f1.isDirectory()){
//                       return -1;
//                   }else{
//                       return 1;
//                   }
//               }
//            });
//        }


//        Thread thread = new Thread(() -> {
//            System.out.println("线程内部");
//        });
//        thread.start();

//        Thread thread = new Thread(FunctionLambdaClass.uncheck(() -> {
//            System.out.println("function inside");
//        }));
//        thread.start();

//        Runnable runnable = FunctionLambdaClass.andThen(() -> {
//             System.out.println("r1");
//        }, () -> {
//            System.out.println("r2");
//        });
//        Thread thread = new Thread(runnable);
//        thread.start();

//        String[] names = {"a","b","c"};
//        List<Runnable> list = new ArrayList<>();
//        for(String name:names){
//            list.add(() ->{System.out.println(name);});
//        }
//        for(Runnable r : list){
//            r.run();
//        }
        Validator validator = new Validator((String s) -> s.matches("[a-z]+"));
        boolean sss = validator.validate("sss");

    }
}
