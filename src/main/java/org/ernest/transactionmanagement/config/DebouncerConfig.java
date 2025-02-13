package org.ernest.transactionmanagement.config;

import org.ernest.transactionmanagement.util.Debouncer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DebouncerConfig {
    @Bean
    public Debouncer debouncer() {
        return new Debouncer(500);
    }
}
