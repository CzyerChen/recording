/**
 * Author:   claire
 * Date:    2020-05-27 - 15:58
 * Description:
 * History:
 * <author>          <time>                   <version>          <desc>
 * claire          2020-05-27 - 15:58          V1.3.6
 */
package com.basic.java8;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 功能简述 <br/> 
 * 〈〉
 *
 * @author claire
 * @date 2020-05-27 - 15:58
 * @since 1.3.6
 */
public class LCGTest {

    public static Stream<Long> lcgStream(long a ,long c,long m,long seed){
        return Stream.iterate(seed, v->(a*v+c)%m);
    }

    public static<T> boolean isFinite(Stream<T> stream){
        System.out.println(Stream.of().spliterator().estimateSize());
        System.out.println(stream.spliterator().estimateSize());
        System.out.println(Long.MAX_VALUE);
        return false;
    }
}
