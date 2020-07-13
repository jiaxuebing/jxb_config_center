package com.jxb.demo.zookeeper;

import java.util.concurrent.CountDownLatch;

/**
 * @desc 基于zk实现分布式锁
 */
public class DistributeLock {

    //默认分布式锁节点
    private String lockNode="/zk_lock_node";

    //当前节点
    private String currentNode;

    //前一个节点
    private String preNode;



    private CountDownLatch countDownLatch = new CountDownLatch(1);



}
