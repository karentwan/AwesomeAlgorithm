package cn.karent.algo;

import java.util.*;

/***********************************************
 * description: 一致性Hash算法的简单实现
 * @author wan
 * @date 2021.10.03
 ***********************************************/
public class ConsistencyHash {

    private int virtualCopies;  // 物理节点的复制倍数

    private TreeMap<Long, String> virtualNodes = new TreeMap<>();  // 圆环, 用来存虚拟节点

    // 32位的Fowler-Noll-Vo哈希算法
    private Long FNVHash(String key) {
        final int p = 16777619;
        Long hash = 2166136261L;
        for (int idx = 0, num = key.length(); idx < num; idx++) {
            hash = (hash ^ key.charAt(idx)) * p;
        }
        hash += hash << 13;
        hash ^= hash >> 7;
        hash += hash << 3;
        hash ^= hash >> 17;
        hash += hash << 5;

        if( hash < 0) {
            hash = Math.abs(hash);
        }
        return hash;
    }
    // 获取虚拟ip
    private String getVirtualIP(String realIP, int cnt) {
        return realIP + "#virtualNode-" + cnt;
    }
    // 增加节点
    public void addPhysicalNode(String realIP) {
        // 每个真实的ip都复制virtualCopies份然后挂在圆环上
        for (int i = 0; i < virtualCopies; i++) {
            long hash = FNVHash(getVirtualIP(realIP, i));
            virtualNodes.put(hash, realIP);
        }
    }
    // 删除节点
    public void removePhysicalNode(String realIP) {
        for (int i = 0; i < virtualCopies; i++) {
            long hash = FNVHash(getVirtualIP(realIP, i));
            virtualNodes.remove(hash);
        }
    }
    // 将对象分配给虚拟节点
    public String assignObj2Node(String obj) {
        Long hash = FNVHash(obj);
        Map.Entry<Long, String> entry = virtualNodes.ceilingEntry(hash);  // 大于等于
        return entry == null ? virtualNodes.firstEntry().getValue() : entry.getValue();
    }

    public ConsistencyHash(int virtualCopies) {
        this.virtualCopies = virtualCopies;
    }

    public void dumpObjectNodeMap(String label, int min, int max) {
        Map<String, Integer> map = new HashMap<>();
        for (int object = min; object <= max; object++) {
            String nodeIP = assignObj2Node(String.valueOf(object));
            map.put(nodeIP, map.getOrDefault(nodeIP, 0) + 1);
        }
        System.out.println("========================>" + label + "<==============================");
        double total = max - min + 1;
        for (String key : map.keySet()) {
            Integer value = map.get(key);
            System.out.println("IP = " + key + "\tRATE = " + (int)(value * 100/ total) + "%");
        }
        System.out.println("========================> 结束 <==============================");

    }

    public static void main(String[] args) {
        int virtualCopies = 1048576;
        String[] ips = {
                "192.168.1.101",
                "192.168.1.102",
                "192.168.1.103",
                "192.168.1.104"
        };
        ConsistencyHash hash = new ConsistencyHash(virtualCopies);
        for (String ip : ips) {
            hash.addPhysicalNode(ip);
        }
        hash.dumpObjectNodeMap("初始",0, 65535);
        hash.removePhysicalNode(ips[0]);
        hash.dumpObjectNodeMap("删除", 0, 65535);
        hash.addPhysicalNode("192.168.1.105");
        hash.dumpObjectNodeMap("增加", 0, 65535);
    }

}
