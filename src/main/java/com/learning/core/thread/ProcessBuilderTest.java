package com.learning.core.thread; /*
 * Copyright [2015] [Jeff Lee]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.IOException;
import java.util.Scanner;

/**
 * Java进程调用CMD
 *  VM options => -Dfile.encoding="GBK"
 * @author BYSocket
 * @since 2016-01-18 16:08:00
 */
public class ProcessBuilderTest {
    public static void main(String[] args) throws IOException {
        ProcessBuilder pb = new ProcessBuilder("cmd","/c","ipconfig/all");
        Process p = pb.start();

        Scanner scanner = new Scanner(p.getInputStream());
        while (scanner.hasNext())
            System.out.println(scanner.next());
        scanner.close();
    }
}
