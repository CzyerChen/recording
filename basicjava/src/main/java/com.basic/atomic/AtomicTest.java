package com.basic.atomic;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Desciption
 *
 * @author Claire.Chen
 * @create_time 2019 -02 - 28 15:27
 */
public class AtomicTest {

    private  static AtomicInteger atomicInt  = new AtomicInteger(10);
    private  static int[] arr = new int[]{1,33,512};
    private  static  AtomicIntegerArray atomicArr = new AtomicIntegerArray(arr);

    public static void main(String[] args){
        System.out.println(atomicInt);
        int addAndGet = atomicInt.addAndGet(11);
        System.out.println(atomicInt);

        System.out.println(atomicArr);
        atomicArr.addAndGet(0,100);
        System.out.println(atomicArr);

        atomicArr.getAndSet(1,0);
        System.out.println(atomicArr);

        ProfileEntry profile1 = new ProfileEntry("1","lily",30);
        ProfileEntry profile2 = new ProfileEntry("2","Joe",20);

        AtomicReference<ProfileEntry> reference = new AtomicReference<>();
        reference.set(profile1);

        System.out.println(reference.get().toString());
        reference.compareAndSet(profile1,profile2);

        System.out.println(reference.get().toString());

        ProfileEntry profile3 = new ProfileEntry("3","claire",50);
        AtomicIntegerFieldUpdater<ProfileEntry> updater = AtomicIntegerFieldUpdater.newUpdater(ProfileEntry.class,"age");

        System.out.println(updater.getAndIncrement(profile3));
        System.out.println(updater.get(profile3));

    }
}
