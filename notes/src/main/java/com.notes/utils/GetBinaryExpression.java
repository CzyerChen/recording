package com.notes.utils;

/**
 * Desciption
 *
 * @author Claire.Chen
 * @create_time 2019 -02 - 25 16:23
 */
public class GetBinaryExpression {
    final static char[] digits = {'0', '1'};

    public static void main(String[] args) {
       String a = toBinaryString(1,8);
       String b = toBinaryString(2,32);
       String c = toBinaryString(1|2,32);
        System.out.println("a="+a+",b="+b+",c="+c);

    }


    public static String toBinaryString(int val, int size) {
        return toUnsignedString0(val, 1, size);
    }

    private static String toUnsignedString0(int val, int shift, int size) {
        if (size > Integer.MAX_VALUE) {
            throw new ArrayIndexOutOfBoundsException("current size is " + size);
        }

        int chars = size;
        char[] buf = new char[chars];
        int charPos = formatUnsignedInt(val, shift, buf, 0, chars);
        for (int i = 0; i < charPos; i++) {
            buf[i] = '0';
        }

        return new String(buf);
    }

    static int formatUnsignedInt(int val, int shift, char[] buf, int offset, int len) {
        int charPos = len;
        int radix = 1 << shift;
        int mask = radix - 1;
        do {
            buf[offset + --charPos] = digits[val & mask];
            val >>>= shift;
        } while (val != 0 && charPos > 0);

        return charPos;
    }
}
