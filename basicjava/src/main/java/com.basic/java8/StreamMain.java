/**
 * Author:   claire
 * Date:    2020-05-26 - 18:22
 * Description:
 * History:
 * <author>          <time>                   <version>          <desc>
 * claire          2020-05-26 - 18:22          V1.3.6
 */
package com.basic.java8;

import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * 功能简述 <br/> 
 * 〈〉
 *
 * @author claire
 * @date 2020-05-26 - 18:22
 * @since 1.3.6
 */
public class StreamMain {

    public static void main(String[] args) {
        String str = "a,b,c,d,c";
        Stream<String> stringStream = Stream.of(str.split(","));
        String[] content = str.split(",");
        Stream<String> stringStream1 = Arrays.stream(content, 0, 2);
        Stream<String> stringStream2 = Stream.generate(() -> "test");
        Stream<Double> doubleStream = Stream.generate(Math::random);
        //Stream.iterate()
        Stream<String> stringStream3 = Pattern.compile(",").splitAsStream(str);
        Stream<String> stringStream4 = stringStream.filter("a"::equals);
        Stream<Integer> integerStream = stringStream4.map(String::length);
        Stream<String> stringStream5 = Stream.concat(stringStream1, stringStream2);
        Stream<String> stringStream6 = stringStream5.peek(System.out::println).limit(3);
        Stream<String> stringStream7 = stringStream6.distinct();
        Stream<String> stringStream8 = stringStream7.sorted(Comparator.comparing(String::length));

        long count = stringStream8.count();
        Optional<String> max = stringStream8.max(Comparator.comparing(String::length));
        Optional<String> min = stringStream8.min(Comparator.comparing(String::length));
        Optional<String> first = stringStream8.findFirst();
        Optional<String> any = stringStream8.parallel().findAny();
        boolean anyMatch = stringStream8.parallel().anyMatch("a"::equals);

        HashSet<Object> objectHashSet = stringStream8.collect(HashSet::new, HashSet::add, HashSet::addAll);
        Set<String> collect = stringStream8.collect(Collectors.toSet());
        HashSet<String> stringHashSet = stringStream8.collect(Collectors.toCollection(HashSet::new));
        TreeSet<Object> objectTreeSet = stringStream8.collect(Collectors.toCollection(TreeSet::new));

        String str1 = stringStream8.collect(Collectors.joining());
        String str2 = stringStream8.collect(Collectors.joining(","));

        Integer num1 = integerStream.collect(Collectors.summingInt(Integer::intValue));

        Map<Integer, Person> personMap = Stream.of(new Person()).collect(Collectors.toMap(Person::getId, Function.identity()));

        IntStream intStream = IntStream.of(1, 2, 3, 4, 5);
        //0 -99 的步长为1的列表
        IntStream range = IntStream.range(0, 100);
        //0-100 的步长为1的列表
        IntStream rangeClosed = IntStream.rangeClosed(0, 100);
        //字符串16进制编码
        IntStream intStream1 = str.codePoints();
        Stream<Integer> boxed = intStream1.boxed();
        //字符串字符列表
        IntStream chars = str.chars();

        Stream<String> unorderedStream = stringStream8.parallel().unordered();
        //内部元素无序分组，一个流操作时，并不会修改底层的集合（线程安全的）
        ConcurrentMap<String, List<Person>> listConcurrentMap = Stream.of(new Person()).parallel().collect(Collectors.groupingByConcurrent(Person::getName));

        int cores = Runtime.getRuntime().availableProcessors();
        Integer[] result = new Integer[cores];
        Thread[] threads = new Thread[cores];
        ThreadTest threadTest = new ThreadTest();
        for (int i = 0; i < cores; i++) {
            threads[i] = threadTest.new ThreadNew(i, content, result);
            threads[i].start();
        }

        for (int i = 0; i < cores; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
            }
        }
        int total = Stream.of(result).mapToInt(Integer::intValue).sum();

        Stream<String> stringStream9 = Stream.of(content).filter(d -> d.length() > 12).limit(5);

        int[] intArr = new int[5];
        IntStream intArr1 = IntStream.of(intArr);


        Stream<Long> longStream = LCGTest.lcgStream(25214903917L, 11, 1L << 48, System.currentTimeMillis()).limit(5);

        Stream<Character> characterStream = IntStream.range(0, content.length - 1).mapToObj(str::charAt);

        ArrayList<String> list = new ArrayList<>();
        ArrayList<String> stringArrayList = Stream.of(list).flatMap(v -> Stream.of(v.toArray(new String[0]))).collect(Collectors.toCollection(ArrayList::new));

        ArrayList<String> stringArrayList1 = Stream.of(list).reduce((a1, a2) -> {
            a1.addAll(a2);
            return a1;
        }).orElse(new ArrayList<>());

        ArrayList<String> stringArrayList2 = Stream.of(list).reduce(new ArrayList<String>(), (a1, a2) -> {
            a1.addAll(a2);
            return a1;
        });

        ArrayList<String> stringArrayList3 = Stream.of(list).reduce(new ArrayList<String>(), (x1, add) -> {
            x1.addAll(add);
            return x1;
        }, (y1, y2) -> {
            y1.addAll(y2);
            return y1;
        });


        OptionalDouble average = DoubleStream.of(0.4).average();
        DoubleSummaryStatistics doubleSummaryStatistics = DoubleStream.of(0.4).summaryStatistics();

        Long count3 = Stream.of(content).collect(Collectors.partitioningBy(a -> a.length() < 12, Collectors.counting())).get(true);
    }

}
