package org.ernest.transactionmanagement.service.Impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.hash.BloomFilter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.ernest.transactionmanagement.entity.Transaction;
import org.ernest.transactionmanagement.exception.BusenessException;
import org.ernest.transactionmanagement.repository.TransactionRespository;
import org.ernest.transactionmanagement.service.TransactionService;
import org.ernest.transactionmanagement.util.CacheUtil;
import org.ernest.transactionmanagement.util.Debouncer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.ernest.transactionmanagement.config.cacheConfig.Constants;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@Service
@Slf4j
public class TransacServiceImpl implements TransactionService {

    @Autowired
    private BloomFilter<String> bloomFilter;

    @Autowired
    private Debouncer debouncer;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    CacheUtil cacheUtil;

    @Autowired
    private TransactionRespository transactionRespository;

    private final ConcurrentHashMap<Long, ReentrantLock> locks = new ConcurrentHashMap<>();

    @Override
    @SneakyThrows
    @CachePut(value = Constants.CACHE_NAME_SINGLE, key = "#result.id")
    @CacheEvict(value = Constants.CACHE_NAME_COLLECTIVE, allEntries = true)
    public Transaction createTransaction(Transaction transaction) {
        String md5 = transaction.calculateMD5();
        //防抖
        if (debouncer.allow(md5) == false) {
            throw new BusenessException("Operation too frequent");
        }
        transactionRespository.insert(transaction);
        // 更新布隆过滤器
        bloomFilter.put(transaction.getId().toString());
        return transaction;
    }


    @Override
    @Caching(evict = {
            @CacheEvict(value = Constants.CACHE_NAME_SINGLE, key = "#id"),
            @CacheEvict(value = Constants.CACHE_NAME_COLLECTIVE, allEntries = true)
    })
    public void deleteTransaction(Long id) {
        transactionRespository.deleteById(id);
    }

    /*
    * 更新, 延时双删
    *
    * */
    @SneakyThrows
    @Override
    public Transaction updateTransaction(Long id, Transaction newTransaction) {
        Transaction oldTransaction = transactionRespository.selectById(id);
        if (oldTransaction == null) {
            throw new BusenessException("Transaction not found");
        }


            newTransaction.setId(oldTransaction.getId());
            transactionRespository.updateById(newTransaction);
            cacheManager.getCache(Constants.CACHE_NAME_SINGLE).evict(id);
            cacheManager.getCache(Constants.CACHE_NAME_COLLECTIVE).clear();

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Interrupted while waiting del cache id: {}", id);
            }
            //再删一遍, 减少不一致
            cacheManager.getCache(Constants.CACHE_NAME_SINGLE).evict(id);
            cacheManager.getCache(Constants.CACHE_NAME_COLLECTIVE).clear();
        return newTransaction;
    }

    @Override
    public Transaction getTransaction(Long id) {
        //防穿透
        if (!bloomFilter.mightContain(id.toString())) {
            return null;
        }

        if (cacheUtil.cacheContains(id, Constants.CACHE_NAME_SINGLE)) {
            return cacheUtil.getFromSingleCache(id);
        }

        ReentrantLock lock = locks.computeIfAbsent(id, k -> new ReentrantLock());
        // 在缓存上自旋几次, 防止同时查库
        int SpinTimes = 5;
        while (SpinTimes > 0) {
            try {
                // 拿到锁查库
                if (lock.tryLock(1, TimeUnit.SECONDS)) {
                    try {
                        if (cacheUtil.cacheContains(id, Constants.CACHE_NAME_SINGLE)) {
                            return cacheUtil.getFromSingleCache(id);
                        }
                        Transaction transaction = transactionRespository.selectById(id);
                        // 空也放, 防穿透
                        cacheUtil.addToCache(id, transaction);
                        return transaction;
                    } finally {
                        lock.unlock();
                    }
                } else {
                    SpinTimes--;
                    log.warn("Could not acquire lock for id: {}. Retrying... ({} retries left)", id, SpinTimes);
                    try {
                        // 等别的请求查完
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Interrupted while trying to acquire lock for id: {}, error:{}", id, e.getMessage(), e);
                throw new BusenessException("Interrupted while trying to acquire lock for id: " + id);
            }
        }
        log.error("Failed to acquire lock for id: {} after multiple attempts", id);
        throw new BusenessException("query failed");
    }

    @Override
    @Cacheable(value = Constants.CACHE_NAME_COLLECTIVE, key = "#page.current +  '_' + #page.size", unless = "#result == null")
    public Page<Transaction> getTransactions(Page<Transaction> page) {
        Page<Transaction> res = transactionRespository.selectPage(page);
        return res;
    }


}
