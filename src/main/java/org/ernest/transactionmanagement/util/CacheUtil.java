package org.ernest.transactionmanagement.util;

import lombok.extern.slf4j.Slf4j;
import org.ernest.transactionmanagement.entity.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.util.Optional;
import org.ernest.transactionmanagement.config.cacheConfig.Constants;

@Component
@Slf4j
public class CacheUtil {

    @Autowired
    private CacheManager cacheManager;


    /**
     * 检查指定缓存是否包含指定 ID 的数据
     *
     * @param id
     * @param cacheName Contens
     * @return
     */
    public boolean cacheContains(Long id, String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            // 记录日志，通知开发者缓存不存在
            log.warn("Cache '{}' is not available in the cache manager.", cacheName);
            return false;
        }
        // 使用 Optional 来处理可能的空值
        Optional optionalValue = Optional.ofNullable(cache.get(id, Object.class));
        return optionalValue.isPresent();
    }

    /**
     * 从id缓存中获取数据
     *
     * @param id
     * @return
     */
    public Transaction getFromSingleCache(Long id) {
        Cache cache = cacheManager.getCache(Constants.CACHE_NAME_SINGLE);
        Transaction transaction = cache.get(id, Transaction.class);
        return transaction;
    }

    public void addToCache(Long id, Transaction transaction) {
        Cache cache = cacheManager.getCache(Constants.CACHE_NAME_SINGLE);
        cache.put(id, transaction);
    }
}
