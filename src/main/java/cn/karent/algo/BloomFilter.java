package cn.karent.algo;

import java.util.*;

/***********************************************
 * description: 布隆过滤器
 * @author wan
 * @date 2021.09.05
 ***********************************************/
public class BloomFilter {

    private int[] seeds = {3, 5, 7, 11, 13, 17, 19, 23};

    private int size = 8;

    private HashFunction[] functions;

    static class HashFunction {
        int size;
        BitSet set;
        int seed;
        public HashFunction(int size, int seed) {
            this.size = size;
            set = new BitSet(size);
            this.seed = seed;
        }

        public void set(String value) {
            int idx = hash(value);
            set.set(idx);
        }

        public boolean get(String value) {
            int idx = hash(value);
            return set.get(idx);
        }

        public int hash(String value) {
            int ret = 0;
            for (int i = 0; i < value.length(); i++) {
                ret = ret  * seed + value.charAt(i);
            }
            return ret & (size - 1);
        }
    }

    public BloomFilter() {
        functions = new HashFunction[seeds.length];
        for (int i = 0; i < seeds.length; i++) {
            functions[i] = new HashFunction(size, seeds[i]);
        }
    }

    public void add(String value) {
        for (int i = 0; i < seeds.length; i++) {
            functions[i].set(value);
        }
    }

    public boolean contains(String value) {
        boolean ret = true;
        for (int i = 0; i < seeds.length; i++) {
            ret = ret && functions[i].get(value);
        }
        return ret;
    }

    public static void main(String[] args) {
//        BitSet bs = new BitSet();
//        bs.set();
//        BloomFilter bf = new BloomFilter();
//        String s = "123132131";
//        bf.add(s);
//        System.out.println(bf.contains(s));
        BitSet bs = new BitSet(16);
        bs.set(18);
    }

}
