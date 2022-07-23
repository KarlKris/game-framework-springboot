package concurrency;

import cn.hutool.core.util.RandomUtil;
import com.li.common.concurrency.AbstractRunnableSource;
import com.li.common.concurrency.MultiThreadRunnableLoopGroup;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author li-yuanwen
 * @date 2022/7/15
 */
public class MultiThreadRunnableLoopGroupTest {


    public static void main(String[] args) throws InterruptedException {
        MultiThreadRunnableLoopGroup group = new MultiThreadRunnableLoopGroup();

        int num = 100;
        TestSource[] sources = new TestSource[num];
        for (int i = 0; i < num; i++) {
            sources[i] = new TestSource(i + 1);
            group.register(sources[i]);
        }

        AtomicInteger id = new AtomicInteger();
        for (int i = 0; i < 10000; i++) {
            int j = RandomUtil.randomInt(num);
            TestSource source = sources[j];
            Runnable runnable = () -> System.out.println(Thread.currentThread().getName() + "-" + source.id + "-" + id.incrementAndGet());
            source.runnableLoop().submit(runnable);
        }

        Thread.sleep(30000);

        group.shutdownGracefully();

    }

    public static class TestSource extends AbstractRunnableSource {

        private final int id;

        TestSource(int id) {
            this.id = id;
        }

    }
}
