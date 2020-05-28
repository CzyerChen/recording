/**
 * Author:   claire
 * Date:    2020-05-27 - 19:47
 * Description:
 * History:
 * <author>          <time>                   <version>          <desc>
 * claire          2020-05-27 - 19:47          V1.0.0
 */
package com.basic.java8;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * 功能简述 <br/> 
 * 〈〉
 *
 * @author claire
 * @date 2020-05-27 - 19:47
 */
public class TimeMain {

    public static void main(String[] args) {
        LocalDate now = LocalDate.now();
        LocalDate date1 = LocalDate.of(2020, 5, 25);
        LocalDate date2 = LocalDate.of(2020, Month.AUGUST, 4);
        LocalDate date3 = LocalDate.of(2020, 6, 1).with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm:ss");
        String format = formatter.format(LocalDateTime.now());

        LocalDate.now().with(Objects.requireNonNull(TimePracticeClass.next(w -> w.getDayOfWeek().getValue() < 6)));
    }
}
