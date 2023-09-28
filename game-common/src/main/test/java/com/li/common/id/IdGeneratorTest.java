package com.li.common.id;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

/**
 * @author: li-yuanwen
 */
public class IdGeneratorTest {


    public static final Logger log = LoggerFactory.getLogger(IdGeneratorTest.class);

    public static void main(String[] args) throws InterruptedException {
        //计时开始时间
        long start = System.currentTimeMillis();
        MultiSnowflakeIdGenerator idGenerator = new MultiSnowflakeIdGenerator(5820);
        //让100个线程同时进行
        final CountDownLatch latch = new CountDownLatch(100);
        //判断生成的20万条记录是否有重复记录
        final Map<Long, Integer> map = new ConcurrentHashMap<>();
        for (int i = 0; i < 100; i++) {
            //创建100个线程
            new Thread(() -> {
                for (int s = 0; s < 20000; s++) {
                    long snowID = idGenerator.nextId();
//                    System.out.println("生成雪花ID= " + snowID);
                    Integer put = map.put(snowID, 1);
                    if (put != null) {
                        throw new RuntimeException("主键重复");
                    }
                    int toWorkerId = MultiServerIdGenerator.toWorkerId(snowID);
                    if (toWorkerId != 5820) {
                        throw new RuntimeException("workerId error");
                    }
                }
                latch.countDown();
            }).start();
        }
        //让上面100个线程执行结束后，在走下面输出信息
        latch.await();
        System.out.println("生成20万条雪花ID总用时= " + (System.currentTimeMillis() - start));
    }
}
