package loader.springframe.jar;

import java.nio.charset.StandardCharsets;

/**
 * Desciption
 *
 * @author Claire.Chen
 * @create_time 2019 -02 - 01 11:23
 */
final class AsciiBytes {
    private static final String EMPTY_STRING = "";
    private static final int[] EXCESS = new int[]{0, 4224, 150, 29892736};
    private final byte[] bytes;
    private final int offset;
    private final int length;
    private String string;
    private int hash;

    AsciiBytes(String string) {
        this(string.getBytes(StandardCharsets.UTF_8));
        this.string = string;
    }

    AsciiBytes(byte[] bytes) {
        this(bytes, 0, bytes.length);
    }

    AsciiBytes(byte[] bytes, int offset, int length) {
        if (offset >= 0 && length >= 0 && offset + length <= bytes.length) {
            this.bytes = bytes;
            this.offset = offset;
            this.length = length;
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    public int length() {
        return this.length;
    }

    public boolean startsWith(AsciiBytes prefix) {
        if (this == prefix) {
            return true;
        } else if (prefix.length > this.length) {
            return false;
        } else {
            for(int i = 0; i < prefix.length; ++i) {
                if (this.bytes[i + this.offset] != prefix.bytes[i + prefix.offset]) {
                    return false;
                }
            }

            return true;
        }
    }

    public boolean endsWith(AsciiBytes postfix) {
        if (this == postfix) {
            return true;
        } else if (postfix.length > this.length) {
            return false;
        } else {
            for(int i = 0; i < postfix.length; ++i) {
                if (this.bytes[this.offset + (this.length - 1) - i] != postfix.bytes[postfix.offset + (postfix.length - 1) - i]) {
                    return false;
                }
            }

            return true;
        }
    }

    public AsciiBytes substring(int beginIndex) {
        return this.substring(beginIndex, this.length);
    }

    public AsciiBytes substring(int beginIndex, int endIndex) {
        int length = endIndex - beginIndex;
        if (this.offset + length > this.bytes.length) {
            throw new IndexOutOfBoundsException();
        } else {
            return new AsciiBytes(this.bytes, this.offset + beginIndex, length);
        }
    }

    @Override
    public String toString() {
        if (this.string == null) {
            if (this.length == 0) {
                this.string = "";
            } else {
                this.string = new String(this.bytes, this.offset, this.length, StandardCharsets.UTF_8);
            }
        }

        return this.string;
    }

    public boolean matches(CharSequence name, char suffix) {
        int charIndex = 0;
        int nameLen = name.length();
        int totalLen = nameLen + (suffix == 0 ? 0 : 1);

        for(int i = this.offset; i < this.offset + this.length; ++i) {
            int b = this.bytes[i];
            if (b < 0) {
                b &= 127;
                int limit = this.getRemainingUtfBytes(b);

                for(int j = 0; j < limit; ++j) {
                    int var10000 = b << 6;
                    ++i;
                    b = var10000 + (this.bytes[i] & 255);
                }

                b -= EXCESS[limit];
            }

            char c = this.getChar(name, suffix, charIndex++);
            if (b <= 65535) {
                if (c != b) {
                    return false;
                }
            } else {
                if (c != (b >> 10) + 'ퟀ') {
                    return false;
                }

                c = this.getChar(name, suffix, charIndex++);
                if (c != (b & 1023) + '\udc00') {
                    return false;
                }
            }
        }

        return charIndex == totalLen;
    }

    private char getChar(CharSequence name, char suffix, int index) {
        if (index < name.length()) {
            return name.charAt(index);
        } else {
            return index == name.length() ? suffix : '\u0000';
        }
    }

    @Override
    public int hashCode() {
        int hash = this.hash;
        if (hash == 0 && this.bytes.length > 0) {
            for(int i = this.offset; i < this.offset + this.length; ++i) {
                int b = this.bytes[i];
                if (b < 0) {
                    b &= 127;
                    int limit = this.getRemainingUtfBytes(b);

                    for(int j = 0; j < limit; ++j) {
                        int var10000 = b << 6;
                        ++i;
                        b = var10000 + (this.bytes[i] & 255);
                    }

                    b -= EXCESS[limit];
                }

                if (b <= 65535) {
                    hash = 31 * hash + b;
                } else {
                    hash = 31 * hash + (b >> 10) + 'ퟀ';
                    hash = 31 * hash + (b & 1023) + '\udc00';
                }
            }

            this.hash = hash;
        }

        return hash;
    }

    private int getRemainingUtfBytes(int b) {
        return b < 96 ? 1 : (b < 112 ? 2 : 3);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } else if (this == obj) {
            return true;
        } else {
            if (obj.getClass() == AsciiBytes.class) {
                AsciiBytes other = (AsciiBytes)obj;
                if (this.length == other.length) {
                    for(int i = 0; i < this.length; ++i) {
                        if (this.bytes[this.offset + i] != other.bytes[other.offset + i]) {
                            return false;
                        }
                    }

                    return true;
                }
            }

            return false;
        }
    }

    static String toString(byte[] bytes) {
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public static int hashCode(CharSequence charSequence) {
        return charSequence instanceof StringSequence ? charSequence.hashCode() : charSequence.toString().hashCode();
    }

    public static int hashCode(int hash, char suffix) {
        return suffix == 0 ? hash : 31 * hash + suffix;
    }
}
