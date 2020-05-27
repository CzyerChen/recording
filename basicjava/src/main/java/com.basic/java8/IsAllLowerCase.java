/**
 * Author:   claire
 * Date:    2020-05-26 - 17:53
 * Description:
 * History:
 * <author>          <time>                   <version>          <desc>
 * claire          2020-05-26 - 17:53          V1.3.6
 */
package com.basic.java8;

/**
 * 功能简述 <br/> 
 * 〈〉
 *
 * @author claire
 * @date 2020-05-26 - 17:53
 * @since 1.3.6
 */
public class IsAllLowerCase implements ValidationStrategy {
    @Override
    public boolean execute(String s) {
        return s.matches("[a-z]+");
    }
}
