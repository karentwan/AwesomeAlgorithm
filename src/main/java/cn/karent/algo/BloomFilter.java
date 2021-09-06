package cn.karent.algo;

import java.util.*;

/***********************************************
 * description: 布隆过滤器
 *  定义一个大的bit数组, 然后定义一组hash函数, 每存一个值的时候,
 *  调用这一组hash函数, 每个哈希函数都会计算出来一个索引, 将这些索引的位置都设置为
 *  true. 判断一个数是否存在的时候也调用这一组hash函数, 然后判断每一个索引是否为true
 *  但凡有一个索引的位置为false, 则表示没有这个数.
 *  实际上布隆过滤器就是一个bit版的HashMap, 定义多个Hash函数就是为了解决Hash冲突
 *  缺点: 不能删除
 * @author wan
 * @date 2021.09.05
 ***********************************************/
public class BloomFilter {

    static class HashFunction {
        int seed;
        int size;

        public HashFunction(int seed, int size) {
            this.seed = seed;
            this.size = size;
        }

        public int hash(String value) {
            int ret = 0;
            for (int i = 0; i < value.length(); i++) {
                ret = ret * seed + value.charAt(i);
            }
            return ret & (size - 1);
        }

    }

    // 十亿个bit, 这个是直接指定的位的个数, 最好是8的倍数, 这样才刚好是整数
    // 个字节
    private final int DEFAULT_SIZE = 256 << 22;
    // 种子函数, 里面存放的是质数
    private int[] seeds = {3, 5, 7, 11, 13, 17, 19, 23};
    // 定义的bit数组, hash函数计算出索引后在这个数组里面设置
    private BitSet bitSet;
    // 一组hash函数
    private HashFunction[] functions;

    public BloomFilter() {
        bitSet = new BitSet(DEFAULT_SIZE);
        functions = new HashFunction[seeds.length];
        for (int i = 0; i < seeds.length; i++) {
            functions[i] = new HashFunction(seeds[i], DEFAULT_SIZE);
        }
    }
    // 添加进布隆过滤器
    public void add(String value) {
        for (HashFunction function : functions) {
            int hash = function.hash(value);
            bitSet.set(hash);
        }
    }

    public boolean contains(String value) {
        for (HashFunction function : functions) {
            int hash = function.hash(value);
            if( !bitSet.get(hash) )
                return false;
        }
        return true;
    }

    public static void main(String[] args) {
        BloomFilter bf = new BloomFilter();
        bf.add("baidu");
        System.out.println("baidu:" + bf.contains("baidu"));
        System.out.println("tencent:" + bf.contains("tencent"));
        bf.add("tencent");
        System.out.println("tencent:" + bf.contains("tencent"));
    }

}
