package com.li.client.stat;

import com.li.network.message.SocketProtocol;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 效率统计器
 * @author li-yuanwen
 * @date 2022/5/7
 */
@Component
public class EfficiencyStatistic {

    /** 请求消息序号生成器 **/
    private final AtomicLong snGenerator = new AtomicLong(0);
    /** 协议统计 **/
    private final Map<SocketProtocol, ProtocolStat> statHolder = new HashMap<>(64);
    /** 已发送协议 **/
    private final Map<Long, SingleProtocolStat> sentProtocolHolder = new HashMap<>(16);

    public long nextSn() {
        return snGenerator.incrementAndGet();
    }

    public void requestSingleProtocol(long sn, SocketProtocol protocol) {
        sentProtocolHolder.put(sn, new SingleProtocolStat(protocol, sn));
    }

    public SingleProtocolStat finishSingleProtocol(long sn, SocketProtocol protocol) {
        SingleProtocolStat stat = sentProtocolHolder.remove(sn);
        if (stat != null) {
            long timeConsumed = stat.settle();
            ProtocolStat protocolStat = statHolder.computeIfAbsent(stat.protocol, ProtocolStat::new);
            if (stat.protocol == protocol) {
                protocolStat.success(timeConsumed);
            } else {
                protocolStat.fail(timeConsumed);
            }
        }
        return stat;
    }

    public final class SingleProtocolStat {

        /** 协议 **/
        private final SocketProtocol protocol;
        /** 协议发送时间 **/
        private final long time = System.currentTimeMillis();
        /** 请求消息序号 **/
        private final long sn;

        public SingleProtocolStat(SocketProtocol protocol, long sn) {
            this.protocol = protocol;
            this.sn = sn;
        }


        /** 协议结算，获取耗时 **/
        public long settle() {
            return System.currentTimeMillis() - time;
        }

        public SocketProtocol getProtocol() {
            return protocol;
        }
    }

    private final class ProtocolStat {
        /** 协议 **/
        private final SocketProtocol protocol;
        /** 请求次数 **/
        private int num;
        /** 成功次数 **/
        private int successCount;
        /** 总耗时 **/
        private long totalConsumed;

        public ProtocolStat(SocketProtocol protocol) {
            this.protocol = protocol;
        }

        void success(long timeConsumed) {
            this.num++;
            this.successCount++;
            this.totalConsumed += timeConsumed;
        }

        void fail(long timeConsumed) {
            this.num++;
            this.totalConsumed += timeConsumed;
        }
    }

}
