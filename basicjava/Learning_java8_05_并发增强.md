## 05_并发增强
### 1.原子值
- updateAndGet的修改
- 提供LongAdder LongAccumulator来提升效率，竞争较高的情况，使用LongAdder替代乐观锁的AtomicInteger, increment add sum
- 上面类似的还有DoubleAdder DoubleAccumulator 
- StampedLock --实现乐观读

### 2. ConcurrentHashMap的改进
- 更新值，不能够只用简单的累加再put,可以使用replace 或者是AtomicInteger LongAdder作为value
- computeIfAbsent  computeIfPresent
-  批量数据操作
```text
search
reduce
forEach

searchKeys/ reduceKeys/ForEachKey
searchValues / reduceValues /ForEachValue
search / reduce /ForEach
searchEntries / reduceEntries /ForEachEntry

```
- set视图

### 3.并行数组操作
- Arrays.parallelSort
- parallelSetAll
- parallelPrefix

### 4.可以完成的Future

 