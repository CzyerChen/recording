/**
 * Author:   claire
 * Date:    2020-05-27 - 09:30
 * Description:
 * History:
 * <author>          <time>                   <version>          <desc>
 * claire          2020-05-27 - 09:30          V1.3.6
 */
package com.basic.java8;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * 功能简述 <br/> 
 * 〈〉
 *
 * @author claire
 * @date 2020-05-27 - 09:30
 * @since 1.3.6
 */
public class OptionalMain {

    public static void main(String[] args){
        String str ="content";
        Optional<String> stringOptional = Optional.of(str);
        stringOptional.ifPresent(String::length);

        List<String> list = new ArrayList<>();
        stringOptional.ifPresent(list::add);
        Optional<Boolean> optionalBoolean = stringOptional.map(list::add);
        Optional<Integer> optionalInteger = stringOptional.map(String::length);

        String result = stringOptional.orElse("");
        String result1 = stringOptional.orElseGet(() -> "333");
        String result3 = stringOptional.orElseThrow(NoSuchElementException::new);


     }
}
