## 02_stream
### 1.从迭代器到stream操作
- stream与迭代器的区别：
1. stream不会自己存储元素，元素可能被存储在底层的集合中，或者根据需要产生
2. stream 操作度不会改变源对象
3. stream可能延迟执行

### 2. 创建stream
- Stream.of将一个数组编程一个流
```text
  public static<T> Stream<T> of(T... values) {
        return Arrays.stream(values);
    }
``` 

- Stream.generate()

- Stream.iterate()

- Stream<String> stringStream3 = Pattern.compile(",").splitAsStream(str);

### 3. filter map flatMap
-  filter 是一个T 到 boolean的函数

### 4. 提取子流和组合流
1. Stream limit裁剪n个元素的新流
2. Stream skip 丢弃前n个元素的新流
3. Stream.concat
4. Stream peek

### 5. 有状态的转换
1.前面介绍的流转换都是无状态的，不依赖于原始流，distinct却会从原始流中返回一个具有相同顺序、抑制了重复元素的新流
2.Stream distinct
3.Stream sorted Collection.sort方法是对原有数组进行排序，stream sorted是将一个新流进行排序

### 6. 简单的聚合方法
1. stream count
2. stream max
3. stream min
4. stream findFirst
5. stream findAny
6. stream anyMatch

### 7.Optional 类型
1.Optional<T>是对T的一个封装，引用更安全，不会出现null，但是要使用合适的方式，不然也无法获得安全和便利
```text
        String str ="content";
        Optional<String> stringOptional = Optional.of(str);
        stringOptional.ifPresent(String::length);

        List<String> list = new ArrayList<>();
        stringOptional.ifPresent(list::add);
        Optional<Boolean> optionalBoolean = stringOptional.map(list::add);
        Optional<Integer> optionalInteger = stringOptional.map(String::length);


```
2.使用Optional值
```text
        String result = stringOptional.orElse("");
        String result1 = stringOptional.orElseGet(() -> "333");
        String result3 = stringOptional.orElseThrow(NoSuchElementException::new);
```
3.创建可选值
```text
Optional.of()
Optional.empty()
```
4.使用flatMap来组合可选值函数
```text
Optional<U> test = s.f().flatMap(T::g)
```

### 8. 聚合操作 ： 通常是数字流的聚合，部分场景能够提高效率

### 9.收集结果
```text
        HashSet<Object> objectHashSet = stringStream8.collect(HashSet::new, HashSet::add, HashSet::addAll);
        Set<String> collect = stringStream8.collect(Collectors.toSet());
        以上两个操作是均等的
        public static <T>
                Collector<T, ?, Set<T>> toSet() {
            return new Collectors.CollectorImpl<>((Supplier<Set<T>>) HashSet::new, Set::add,
                    (left, right) -> { left.addAll(right); return left; },
                    CH_UNORDERED_ID);
        }
        
        HashSet<String> stringHashSet = stringStream8.collect(Collectors.toCollection(HashSet::new));
        TreeSet<Object> objectTreeSet = stringStream8.collect(Collectors.toCollection(TreeSet::new));
        String str1 = stringStream8.collect(Collectors.joining());
        String str2 = stringStream8.collect(Collectors.joining(","));
        //String.join   ----> StringJoiner
            public static String join(CharSequence delimiter,
                    Iterable<? extends CharSequence> elements) {
                Objects.requireNonNull(delimiter);
                Objects.requireNonNull(elements);
                StringJoiner joiner = new StringJoiner(delimiter);
                for (CharSequence cs: elements) {
                    joiner.add(cs);
                }
                return joiner.toString();
            }
        //Collectors.joining()  ---> StringJoiner
            public static Collector<CharSequence, ?, String> joining(CharSequence delimiter,
                                                                     CharSequence prefix,
                                                                     CharSequence suffix) {
                return new CollectorImpl<>(
                        () -> new StringJoiner(delimiter, prefix, suffix),
                        StringJoiner::add, StringJoiner::merge,
                        StringJoiner::toString, CH_NOID);
            }
        
        //计算结果
        Integer num1 = integerStream.collect(Collectors.summingInt(Integer::intValue));
        
        //forEach 是终结的算子，如果想要遍历后仍然使用流就是用peek
   
```

### 10. 将结果收集到MAP中
```text
        Map<Integer, Person> personMap = Stream.of(new Person()).collect(Collectors.toMap(Person::getId, Function.identity()));
        Map<Integer, Person> personMap = Stream.of(new Person()).collect(Collectors.toMap(Person::getId, a->a));
```
### 11. 分组和分片
1. groupingBy
2. partitioningBy,当结果是分为两组，一组为true，一组为false情况，partitioningBy会更高效
3. counting
4. summingInt,放在groupingBy内部
5. maxBy,每个分组中最大的
6. mapping 
7. summarizingInt 这与summingInt相比，包含了平均、最大、 最小多个指标
8 reducing


### 12. 原始类型流
1. IntStream : short char byte boolean int  ,Instream.of()
2. DoubleStream : float double ,DoubleStream.of()
3. LongStream: long
```text
        IntStream intStream = IntStream.of(1, 2, 3, 4, 5);
        //0 -99 的步长为1的列表
        IntStream range = IntStream.range(0, 100);
        //0-100 的步长为1的列表
        IntStream rangeClosed = IntStream.rangeClosed(0, 100);
```
4. codePoints chars
```text
        //字符串16进制编码
        IntStream intStream1 = str.codePoints();
        //字符串字符列表
        IntStream chars = str.chars();
        //将原始流转化为对象流
        Stream<Integer> boxed = intStream1.boxed();
```

### 13. 并行流
```text
        Stream<String> unorderedStream = stringStream8.parallel().unordered();
        //内部元素无序分组，一个流操作时，并不会修改底层的集合（线程安全的）
        ConcurrentMap<String, List<Person>> listConcurrentMap = Stream.of(new Person()).parallel().collect(Collectors.groupingByConcurrent(Person::getName));
```
### 14. 函数式接口
1. Predicate
2. 下一章具体介绍



### 问题

1.编写一个for循环的并行版本，获取处理器的数量，创造出多个独立的线程，每个线程只处理列表的一个片段，然后将它们各自的结果汇总起来
```text
        int cores = Runtime.getRuntime().availableProcessors();
        Integer[] result = new Integer[cores];
        Thread[] threads = new Thread[cores];
        ThreadTest threadTest = new ThreadTest();
        for(int i =0; i<cores ; i++){
            threads[i] =  threadTest.new ThreadNew(i,content,result);
            threads[i].start();
        }

        for(int i =0; i<cores ; i++){
            try {
                threads[i].join();
            } catch (InterruptedException e) {
            }
        }
        int total = Stream.of(result).mapToInt(Integer::intValue).sum();

```
2.请想办法验证一下，对于获得前五个最长单词的代码，一旦找到第五个最长的单词后，就不会再调用filter方法了。（一个简单的方法是记录每次的方法调用）
```text
    Stream<String> stringStream9 = Stream.of(content).filter(d -> d.length() > 12).limit(5);

```

3.要统计长单词的数量，使用parallelStream与使用stream有什么区别？请具体测试一下。你可以在调用方法之前和之后调用System.nanoTime，并打印出他们之间的区别。如果你有速度较快的计算机，可以试着处理一个较大的文档（例如战争与和平的英文原著）。

4.假设你有一个数组int[] values={1,4,9,16}。那么Stream.of(values)的结果是什么？你如何获得一个int类型的流。
```text
        int[] intArr = new int[5];
        IntStream intArr1 = IntStream.of(intArr);
```
5.使用Stream.iterate来得到一个包含随机数字的无限流－不许调用Math.Random，只能直接实现一个线性同余生成器(LCG)。在这个生成器中，你可以从$x_0=seed$开始，然后根据合适的a,c和m值产生$x_{n+1}=(ax_n+c)%m$。你应该实现一个含有参数a,c,m和seed的方法，并返回一个
```text
        Stream<Long> longStream = LCGTest.lcgStream(25214903917L, 11, 1L << 48, System.currentTimeMillis()).limit(5);
```

6.第2.3节中的characterStream方法不是很好用，他需要先填充一个数组列表，然后再转变为一个流。试着编写一行基于流的代码。一个办法是构造一个从0开始到s.length()-1的整数流，然后使用s::charAt方法引用来映射它。
```text
        Stream<Character> characterStream = IntStream.range(0, content.length - 1).mapToObj(str::charAt);
```
7.假设你的老板让你编写一个方法,public static<T> boolean isFinite(Stream<T> stream),。为什么这不是一个好主意？不管怎样，先试着写一写。
- 判断是不是一个无限流
```text
       public static<T> boolean isFinite(Stream<T> stream){
        System.out.println(Stream.of().spliterator().estimateSize());
        System.out.println(stream.spliterator().estimateSize());
        System.out.println(Long.MAX_VALUE);
        return false;
    }

```
8.编写一个方法 ,public static <T> Stream<T> zip(Stream<T> first,Stream<T> second), ,依次调换流first和second中元素的位置，直到其中一个流结束为止。
9.将一个 Stream<ArrayList<T>>中的全部元素连接为一个ArrayList。试着用三种不同的聚合方式来实现。
```text
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

```
10.编写一个可以用于计算 Stream<Double>  平均值的聚合方法。为什么不能直接计算出总和再除以count()？
```text
    OptionalDouble average = DoubleStream.of(0.4).average();
    DoubleSummaryStatistics doubleSummaryStatistics = DoubleStream.of(0.4).summaryStatistics();
```
11.我们应该可以将流的结果并发收集到一个ArrayList中，而不是将多个ArrayList合并起来。由于对集合不相交部分的并发操作是线程安全的，所以我们假设这个ArrayList的初始大小即为流的大小。如何能做到这一点？

12.如第2.13节所示，通过更新一个 AtomicInteger 数组来计算一个并行 Stream<String>宏的所有短单词。使用原子操作方法getAndIncreament来安全的增加每个计数器的值。
parallel

13.重复上一个练习，这次使用collect方法、Collectors.groupingBy方法和Collectors.counting方法来过滤出短单词。巩固Collectors的分组方法等。
```text
        Long count3 = Stream.of(content).collect(Collectors.partitioningBy(a -> a.length() < 12, Collectors.counting())).get(true);
```