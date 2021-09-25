package cn.karent.algo;

import java.util.*;

/***********************************************
 * description: 跳表的简单实现
 * @author wan
 * @date 2021.09.25
 ***********************************************/
public class SkipList {

    private final int MAX_LEVEL;   // 跳表的级数

    private static final float SKIP_LIST_P = 0.5f;

    private int levelCount = 1;

    private Node head;

    public SkipList(int maxLevel) {
        this.MAX_LEVEL = maxLevel;
        head = new Node();
    }

    class Node {
        int val = -1;
        Node[] forwards = new Node[MAX_LEVEL];
        int maxLevel;
    }

    // 查找
    public Node find(int val) {
        Node p = head;
        // level越大, 索引越高
        for (int level = levelCount-1; level >= 0; level--) {
            for (; p.forwards[level] != null && p.forwards[level].val < val;
                 p = p.forwards[level]);  // 寻找索引
        }
        // 找到的是待寻找节点的前一个节点
        if( p.forwards[0] != null && p.forwards[0].val == val) {
            return p;
        }
        return null;
    }

    // 插入
    public void insert(int val) {
        int level = randomLevel();
        Node node = new Node();
        node.maxLevel = level;
        node.val = val;
        // 找出要插入的位置
        Node[] update = new Node[level];
        Node p = head;
        for (int i = level-1; i >= 0; i--) {
            for (; p.forwards[i] != null && p.forwards[i].val < val;
                 p = p.forwards[i]);
            update[i] = p;
        }
        // 从索引开始插入
        for (int i = level-1; i >= 0; i--) {
            node.forwards[i] = update[i].forwards[i];
            update[i].forwards[i] = node;
        }
        // 更新level水平
        levelCount = Math.max(levelCount, level);
    }

    // 删除节点
    public void delete(int val) {
        Node p = head;
        // 找出要删除的索引
        Node[] delete = new Node[levelCount];
        for (int i = levelCount-1; i >= 0; i--) {
            for (; p.forwards[i] != null && p.forwards[i].val < val;
                 p = p.forwards[i]);
            delete[i] = p;
        }
        if( delete[0].forwards[0] != null && delete[0].forwards[0].val == val) {  // 找到要删除的节点
            for (int i = levelCount-1; i >= 0; i--) {
                if( delete[i].forwards[i] != null && delete[i].forwards[i].val == val) {
                    delete[i].forwards[i] = delete[i].forwards[i].forwards[i];
                }
            }
        }
    }

    /**
     * 判断当前要插入的节点要插入到几级索引当中，以抛硬币的方式来决定
     * 抛出正面就加一级索引, 直到抛出反面就结束
     * @return
     */
    private int randomLevel() {
        int level = 1;
        for (; level < MAX_LEVEL && Math.random() < SKIP_LIST_P;
            level++);
        return level;
    }

    private void printAll() {
        for (int i = levelCount-1; i >= 0; i--) {
            for (Node p = head.forwards[i]; p != null; p = p.forwards[i]) {
                System.out.print(p.val + " -> ");
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        SkipList list = new SkipList(16);
        list.insert(1);
        list.insert(2);
        list.insert(3);
        list.insert(4);
        list.insert(5);
        list.insert(7);
        list.insert(8);
        list.insert(9);
        list.insert(10);
        list.printAll();
        list.delete(7);
        System.out.println("====================> 删除一个节点 <==================");
        list.printAll();
        list.insert(7);
        System.out.println("====================> 删除一个节点 <==================");
        list.printAll();
//        System.out.println("pause");
    }

}
