package org.ernest.transactionmanagement.service.Impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.hash.BloomFilter;
import lombok.SneakyThrows;
import org.ernest.transactionmanagement.config.cacheConfig.Constants;
import org.ernest.transactionmanagement.entity.Transaction;
import org.ernest.transactionmanagement.exception.BusenessException;
import org.ernest.transactionmanagement.repository.TransactionRespository;
import org.ernest.transactionmanagement.util.CacheUtil;
import org.ernest.transactionmanagement.util.Debouncer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.CacheManager;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransacServiceImplTest {

    @Mock
    private TransactionRespository transactionRespository;

    @Mock
    private BloomFilter<String> bloomFilter;

    @Mock
    private Debouncer debouncer;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private CacheUtil cacheUtil;

    @InjectMocks
    private TransacServiceImpl transacService;

    private Transaction sampleTransaction;

    @BeforeEach
    public void setUp() {
        sampleTransaction = new Transaction();
        sampleTransaction.setId(1L);
        sampleTransaction.setAmount(1000L);
        sampleTransaction.setType("PAYMENT");
        sampleTransaction.setPayer("Alice");
        sampleTransaction.setPayee("Bob");
        sampleTransaction.setDescription("Payment for services");
    }

    @Test
    public void testCreateTransaction_Success() {
        // Mock 防抖器允许操作
        when(debouncer.allow(anyString())).thenReturn(true);

        // Mock 插入操作
        when(transactionRespository.insert(sampleTransaction)).thenReturn(1);

        // 调用方法
        Transaction result = transacService.createTransaction(sampleTransaction);

        // 验证结果
        assertNotNull(result);
        assertEquals(sampleTransaction, result);

        // 验证布隆过滤器更新
        verify(bloomFilter, times(1)).put(sampleTransaction.getId().toString());
    }

    @Test
    public void testCreateTransaction_OperationTooFrequent() {
        // Mock 防抖器拒绝操作
        when(debouncer.allow(anyString())).thenReturn(false);

        // 调用方法并验证异常
        assertThrows(BusenessException.class, () -> transacService.createTransaction(sampleTransaction));
    }

    @Test
    public void testDeleteTransaction_Success() {
        // 调用方法
        transacService.deleteTransaction(1L);

        // 验证删除操作
        verify(transactionRespository, times(1)).deleteById(1L);
    }

    @Test
    public void testUpdateTransaction_Success() {
        // Mock 查询旧交易
        when(transactionRespository.selectById(1L)).thenReturn(sampleTransaction);

        // Mock 更新操作
        when(transactionRespository.updateById(any())).thenReturn(1);

        // Mock 缓存清理
        when(cacheManager.getCache(Constants.CACHE_NAME_SINGLE)).thenReturn(mock(org.springframework.cache.Cache.class));
        when(cacheManager.getCache(Constants.CACHE_NAME_COLLECTIVE)).thenReturn(mock(org.springframework.cache.Cache.class));

        // 调用方法
        Transaction updatedTransaction = new Transaction();
        updatedTransaction.setId(1L);
        updatedTransaction.setAmount(2000L);
        updatedTransaction.setType("REFUND");
        updatedTransaction.setPayer("Bob");
        updatedTransaction.setPayee("Alice");
        updatedTransaction.setDescription("Refund for services");

        Transaction result = transacService.updateTransaction(1L, updatedTransaction);

        // 验证结果
        assertNotNull(result);
        assertEquals(updatedTransaction, result);

        // 验证缓存清理
        verify(cacheManager.getCache(Constants.CACHE_NAME_SINGLE), times(2)).evict(1L);
        verify(cacheManager.getCache(Constants.CACHE_NAME_COLLECTIVE), times(2)).clear();
    }

    @Test
    public void testUpdateTransaction_TransactionNotFound() {
        // Mock 查询旧交易返回空
        when(transactionRespository.selectById(1L)).thenReturn(null);

        // 调用方法并验证异常
        assertThrows(BusenessException.class, () -> transacService.updateTransaction(1L, sampleTransaction));
    }

    @Test
    public void testGetTransaction_Success() {
        // Mock 布隆过滤器
        when(bloomFilter.mightContain("1")).thenReturn(true);

        // Mock 缓存
        when(cacheUtil.cacheContains(1L, Constants.CACHE_NAME_SINGLE)).thenReturn(true);
        when(cacheUtil.getFromSingleCache(1L)).thenReturn(sampleTransaction);

        // 调用方法
        Transaction result = transacService.getTransaction(1L);

        // 验证结果
        assertNotNull(result);
        assertEquals(sampleTransaction, result);
    }

    @Test
    public void testGetTransaction_NotFound() {
        // Mock 布隆过滤器
        when(bloomFilter.mightContain("1")).thenReturn(true);

        // Mock 缓存
        when(cacheUtil.cacheContains(1L, Constants.CACHE_NAME_SINGLE)).thenReturn(false);

        // Mock 查询数据库
        when(transactionRespository.selectById(1L)).thenReturn(null);

        // 调用方法
        Transaction result = transacService.getTransaction(1L);

        // 验证结果
        assertNull(result);
    }

    @Test
    public void testGetTransaction_QueryFailed() {
        when(bloomFilter.mightContain("1")).thenReturn(true);
        when(cacheUtil.cacheContains(1L, Constants.CACHE_NAME_SINGLE)).thenReturn(false);
        when(cacheUtil.cacheContains(1L, Constants.CACHE_NAME_SINGLE)).thenReturn(false);
        assertNull( transacService.getTransaction(1L));
    }

    @Test
    public void testGetTransaction_CacheHit() {
        // Mock 布隆过滤器
        when(bloomFilter.mightContain("1")).thenReturn(true);

        // Mock 缓存命中
        when(cacheUtil.cacheContains(1L, Constants.CACHE_NAME_SINGLE)).thenReturn(true);
        when(cacheUtil.getFromSingleCache(1L)).thenReturn(sampleTransaction);

        // 调用方法
        Transaction result = transacService.getTransaction(1L);

        // 验证结果
        assertNotNull(result);
        assertEquals(sampleTransaction, result);

        // 验证缓存被使用
        verify(cacheUtil, times(1)).getFromSingleCache(1L);
        verify(transactionRespository, never()).selectById(1L); // 确保没有访问数据库
    }

    @Test
    public void testGetTransaction_CacheMiss_DatabaseHit() {
        // Mock 布隆过滤器
        when(bloomFilter.mightContain("1")).thenReturn(true);

        // Mock 缓存未命中
        when(cacheUtil.cacheContains(1L, Constants.CACHE_NAME_SINGLE)).thenReturn(false);

        // Mock 数据库查询
        when(transactionRespository.selectById(1L)).thenReturn(sampleTransaction);

        // 调用方法
        Transaction result = transacService.getTransaction(1L);

        // 验证结果
        assertNotNull(result);
        assertEquals(sampleTransaction, result);

        // 验证缓存被更新
        verify(cacheUtil, times(1)).addToCache(1L, sampleTransaction);
    }

    @Test
    public void testGetTransaction_CacheMiss_DatabaseMiss() {
        // Mock 布隆过滤器
        when(bloomFilter.mightContain("1")).thenReturn(true);

        // Mock 缓存未命中
        when(cacheUtil.cacheContains(1L, Constants.CACHE_NAME_SINGLE)).thenReturn(false);

        // Mock 数据库查询返回空
        when(transactionRespository.selectById(1L)).thenReturn(null);

        // 调用方法
        Transaction result = transacService.getTransaction(1L);

        // 验证结果
        assertNull(result);

        // 验证缓存被更新（防穿透）
        verify(cacheUtil, times(1)).addToCache(1L, null);
    }


    @Test
    public void testGetTransaction_BloomFilterMiss() {
        // Mock 布隆过滤器未命中
        when(bloomFilter.mightContain("1")).thenReturn(false);

        // 调用方法
        Transaction result = transacService.getTransaction(1L);

        // 验证结果
        assertNull(result);

        // 验证缓存和数据库未被访问
        verify(cacheUtil, never()).getFromSingleCache(1L);
        verify(transactionRespository, never()).selectById(1L);
    }


    @Test
    public void testGetTransactions_Success() {
        // Mock 分页查询
        Page<Transaction> page = new Page<>(1, 10);
        when(transactionRespository.selectPage(page)).thenReturn(page);

        // 调用方法
        Page<Transaction> result = transacService.getTransactions(page);

        // 验证结果
        assertNotNull(result);
        assertEquals(page, result);
    }

}