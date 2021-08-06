package com.li.gamesocket.service.push;

import com.li.gamesocket.service.command.MethodCtx;
import com.li.gamesocket.utils.CommandUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author li-yuanwen
 * 推送管理
 */
@Component
@Slf4j
public class PushManager {

    /** 代理对象 **/
    private final Map<String, Object> proxy = new HashMap<>();

    /** 获得推送代理对象 **/
    public <T> T getPushProxy(Class<T> clz) {
        String name = clz.getName();
        Object target = this.proxy.get(name);
        if (target != null) {
            return (T) target;
        }
        synchronized (this.proxy) {
            target = this.proxy.get(name);
            if (target != null) {
                return (T) target;
            }

            List<MethodCtx> methodCtx =
                    CommandUtils.analysisCommands(clz, false);

            target = Proxy.newProxyInstance(clz.getClassLoader()
                    , new Class[]{clz}
                    , new PushProxyInvoker( methodCtx));

            this.proxy.put(name, target);
        }
        return (T) target;
    }

}
