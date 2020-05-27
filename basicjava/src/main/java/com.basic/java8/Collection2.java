/**
 * Author:   claire
 * Date:    2020-05-26 - 16:53
 * Description: 集合接口2
 * History:
 * <author>          <time>                   <version>          <desc>
 * claire          2020-05-26 - 16:53          V1.3.6           集合接口2
 */
package com.basic.java8;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * 功能简述 <br/>
 * 〈集合接口2〉
 *
 * @author claire
 * @date 2020-05-26 - 16:53
 * @since 1.3.6
 */
public interface Collection2<T> extends Collection<T> {
    public default void forEachIf(Consumer<T> action, Predicate<T> filter) {
        forEach(item -> {
            if(filter.test(item)) {
                action.accept(item);
            }
        });
    }
}
