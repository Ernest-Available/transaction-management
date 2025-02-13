package org.ernest.transactionmanagement.config.cacheConfig;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManagerWithRandomTTL() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();


        Caffeine<Object, Object> collectiveCacheBuilder = Caffeine.newBuilder()
                // 设置最大容量为200个条目
                .maximumSize(200)
                .expireAfter(getExpiry());


        Caffeine<Object, Object> singleCacheBuilder = Caffeine.newBuilder()
                // 设置最大容量为1000个条目
                .maximumSize(1000)
                .expireAfter(getExpiry());


        CaffeineCache singleCache = new CaffeineCache(Constants.CACHE_NAME_SINGLE, singleCacheBuilder.build());
        CaffeineCache collectiveCache = new CaffeineCache(Constants.CACHE_NAME_COLLECTIVE, collectiveCacheBuilder.build());

        // 添加到缓存管理器
        cacheManager.setCaches(Arrays.asList(singleCache, collectiveCache));

        return cacheManager;
    }

    /**
     * 随即过期防雪崩
     *
     * @return
     */
    private Expiry<Object, Object> getExpiry() {
        return new Expiry<Object, Object>() {
            @Override
            public long expireAfterCreate(Object key, Object value, long currentTime) {
                // 设置随机过期时间，范围在30分钟到60分钟之间
                return TimeUnit.MINUTES.toNanos(ThreadLocalRandom.current().nextLong(30, 60));
            }

            @Override
            public long expireAfterUpdate(Object key, Object value, long currentTime, long currentDuration) {
                return currentDuration;
            }

            @Override
            public long expireAfterRead(Object key, Object value, long currentTime, long currentDuration) {
                return currentDuration;
            }


        };
    }
}


