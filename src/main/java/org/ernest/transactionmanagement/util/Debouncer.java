package org.ernest.transactionmanagement.util;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.util.concurrent.TimeUnit;

public class Debouncer {

    // 使用 Caffeine 缓存存储每个 key 的最后一次操作时间
    private final Cache<String, Long> cache;

    /**
     * 构造函数
     *
     * @param debounceTime 防抖时间窗口（单位：毫秒）
     */
    public Debouncer(long debounceTime) {
        this.cache = Caffeine.newBuilder()
                .expireAfterWrite(debounceTime, TimeUnit.MILLISECONDS) // 设置缓存过期时间
                .build();
    }

    /**
     * 检查是否允许操作
     *
     * @param key 操作的唯一标识
     * @return true 允许操作，false 防抖生效
     */
    public boolean allow(String key) {
        long currentTime = System.currentTimeMillis();
        Long lastTime = cache.getIfPresent(key);

        if (lastTime == null || (currentTime - lastTime) < 0) {
            // 更新缓存并允许操作
            cache.put(key, currentTime);
            return true;
        } else {
            // 防抖生效，拒绝操作
            return false;
        }
    }

    /**
     * 清除某个 key 的防抖状态
     *
     * @param key 操作的唯一标识
     */
    public void clear(String key) {
        cache.invalidate(key);
    }

    /**
     * 清除所有防抖状态
     */
    public void clearAll() {
        cache.invalidateAll();
    }
}