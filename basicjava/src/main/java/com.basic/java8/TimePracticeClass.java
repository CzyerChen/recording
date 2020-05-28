/**
 * Author:   claire
 * Date:    2020-05-27 - 20:07
 * Description:
 * History:
 * <author>          <time>                   <version>          <desc>
 * claire          2020-05-27 - 20:07          V1.0.0
 */
package com.basic.java8;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.function.Predicate;

/**
 * 功能简述 <br/>
 * 〈〉
 *
 * @author claire
 * @date 2020-05-27 - 20:07
 */
public class TimePracticeClass {

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
}
