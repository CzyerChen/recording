## 04_新的时间API
### 1.时间线
### 2.本地时间
- LocalDate -年月日
- 与UNIX和Date不同（从1900年开始的偏移，月从0开始）
```text
        LocalDate now = LocalDate.now();
        LocalDate date1 = LocalDate.of(2020, 5, 25);
        LocalDate date2 = LocalDate.of(2020, Month.AUGUST, 4);
```
```text
now 
of
plusDays
plusWeeks
plusMonths
plusYears
minusDays
minusWeeks
minusMonths
minusYears
plus
minus
withDayOfMonth
withDayOfYear
withMonth
withYear
getDayOfMonth
getDayOfWeek
getDayOfYear
getMonth
getMonthValue
getYear
Until
isBefore
isAfter
isLeapYear

```
### 3.日期校正器
- 求下一个周几
```text
        LocalDate date3 = LocalDate.of(2020, 6, 1).with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY));
```
```text
next
previous
nextOrSame
previousOrSame
dayOfWeekInMonth
lastInMonth
firstDayOfMonth
firstDayOfNextMonth
firstDayOfNextYear
....
```
### 4.本地时间
- LocalTime
- 操作和LocalDate非常相似

### 5.带时区的时间
- ZondId.of
- UTC -- 协调世界时
- ZoneDateTime通过时区和LocalDateTime转换，操作是很类似的

### 6.格式化和解析
- DateTimeFormatter
- 可以使用预定义或者自定义pattern的方式来格式化时间
```text
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm:ss");
        String format = formatter.format(LocalDateTime.now());
```

### 7.与遗留代码互操作
- 主要是与我们熟知的Date对象进行转换

### 问题
1. today.with(next(w -> {...})) Predicate实现
```text

    public static TemporalAdjuster next(Predicate<LocalDate> action) {
        return TemporalAdjusters.ofDateAdjuster(date -> {
            date = date.plusDays(1);
            boolean condition = action.test(date);
            while (condition) {
                date = date.plusDays(1);
                condition = action.test(date);
            }
            return date;
        });
    }

```