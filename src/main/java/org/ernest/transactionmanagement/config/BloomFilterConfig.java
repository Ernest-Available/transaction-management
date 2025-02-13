package org.ernest.transactionmanagement.config;

import com.google.common.base.Charsets;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BloomFilterConfig {

    @Bean
    public BloomFilter<String> bloomFilter() {
        // 预期插入的数量
        int expectedInsertions = 5000;
        // 期望的误报率
        double fpp = 0.01;
        return BloomFilter.create(Funnels.stringFunnel(Charsets.UTF_8), expectedInsertions, fpp);
    }
}
