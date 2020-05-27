/**
 * Author:   claire
 * Date:    2020-05-27 - 15:26
 * Description:
 * History:
 * <author>          <time>                   <version>          <desc>
 * claire          2020-05-27 - 15:26          V1.3.6
 */
package com.basic.java8;

/**
 * 功能简述 <br/>
 * 〈〉
 *
 * @author claire
 * @date 2020-05-27 - 15:26
 * @since 1.3.6
 */
public class ThreadTest {
    public static final Integer CORES = Runtime.getRuntime().availableProcessors();

    class ThreadNew extends Thread {
        private int index;
        private String[] chars;
        private Integer[] result;

        public ThreadNew(int index, String[] chars, Integer[] result) {
            this.index = index;
            this.chars = chars;
            this.result = result;
        }

        @Override
        public void run() {
            int count = 0;
            for (int i = 0; i < chars.length; i++) {
                if (index + i * CORES < chars.length && chars[index + i * CORES].length() == 1) {
                    count++;
                }
            }
            result[index] = count;
        }
    }
}
