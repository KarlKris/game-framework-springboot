package com.li.common.resource.core;

import cn.hutool.core.thread.NamedThreadFactory;
import com.li.common.resource.storage.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.*;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * 资源自动reload
 * @author li-yuanwen
 * @date 2022/9/21
 */
@Slf4j
@Component
public class ResourceAutoReload implements ApplicationContextAware, Runnable {

    /** 自动reload资源开关 **/
    @Value("${resource.autoReload:false}")
    private boolean enable;

    @Resource
    private StorageManager storageManager;


    private ApplicationContext context;
    private WatchService watcher;
    private ExecutorService executorService;

    private volatile boolean stop = false;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    @PostConstruct
    private void initialize() throws IOException {
        if (!enable) {
            return;
        }
        // 初始化监听器
        initWatcher();
        // 启动监听器
        startWatcher();
    }

    @PreDestroy
    private void destroy() {
        stop = true;
        if (executorService != null) {
            executorService.shutdownNow();
        }
    }

    private void initWatcher() throws IOException {
        watcher = FileSystems.getDefault().newWatchService();
        String resourcePath = ResourceBeanPostFactory.RESOURCE_PATH;
        registerFile(context.getResource(resourcePath).getFile());
    }

    private void registerFile(File file) throws IOException {
        File[] files = file.listFiles();
        if (files == null) {
            return;
        }

        for (File f : files) {
            if (f.isDirectory()) {
                Paths.get(file.getPath()).register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);
                registerFile(f);
            }
        }
    }

    private void startWatcher() {
        executorService = Executors.newSingleThreadExecutor(new NamedThreadFactory("资源Reload线程-", false));
        executorService.submit(this);
    }

    private String getClassSimpleName(String filename) {
        int last = filename.lastIndexOf('.');
        return filename.substring(0, last);
    }

    @Override
    public void run() {
        while (!stop) {
            WatchKey watchKey = null;
            try {
                watchKey = watcher.take();

                List<WatchEvent<?>> watchEvents = watchKey.pollEvents();
                List<ResourceStorage<?, ?>> storages = new LinkedList<>();
                for (WatchEvent<?> event : watchEvents) {
                    WatchEvent<Path> e = (WatchEvent<Path>) event;
                    Path path = e.context();
                    String fileName = path.toFile().getName();
                    String classSimpleName = getClassSimpleName(fileName);
                    if (!StringUtils.hasLength(classSimpleName) || StringUtils.startsWithIgnoreCase(fileName, "~")) {
                        return;
                    }

                    log.error("更新的文件名是: {}", classSimpleName);
                    ResourceStorage<?, ?> storage = storageManager.getResourceStorage(classSimpleName);
                    if (storage == null) {
                        log.error("不是服务端表,忽略: {}", classSimpleName);
                    } else {
                        storage.load();
                        storages.add(storage);
                    }
                }
                // 校验
                boolean validate = true;
                for (ResourceStorage<?, ?> storage : storages) {
                    try {
                        storage.validate();
                    } catch (Exception e) {
                        validate = false;
                        log.error(e.getMessage(), e);
                    }
                }
                if (!validate) {
                    // 一个校验失败,全部还原
                    storages.forEach(ResourceStorage::validateFailure);
                    return;
                }
                for (ResourceStorage<?, ?> storage : storages) {
                    storage.validateSuccessfully();
                    log.error("更新成功,文件名是: {}", storage.getLocation());
                }

            } catch (Exception e) {
                log.error(e.getMessage(), e);
            } finally {
                if (watchKey != null) {
                    watchKey.reset();
                }
            }
        }
    }
}
