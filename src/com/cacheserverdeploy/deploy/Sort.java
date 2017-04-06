package com.cacheserverdeploy.deploy;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 * Created by yang on 2017/4/2.
 */
public class Sort {
    public static void sort(LinkedList<Particle> head, Comparator<Particle> c) {
        Object[] a = head.toArray();
        Arrays.sort(a, (Comparator) c);
        ListIterator<Particle> i = head.listIterator();
        for (Object e : a) {
            i.next();
            i.set((Particle) e);
        }
    }
}
