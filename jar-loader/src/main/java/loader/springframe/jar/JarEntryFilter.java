package loader.springframe.jar;

/**
 * Desciption
 *
 * @author Claire.Chen
 * @create_time 2019 -02 - 01 11:26
 */
interface JarEntryFilter {
    AsciiBytes apply(AsciiBytes name);
}