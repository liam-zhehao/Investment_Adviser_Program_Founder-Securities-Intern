package com.foundersc.ifte.invest.adviser.dubbo.cache;

import com.foundersc.ifte.invest.adviser.dubbo.util.CacheConstants;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author wangfuwei
 * @date 2023/8/28
 */
@Slf4j
@Configuration
@EnableCaching
public class CaffeineConfig {
    /**
     * caffeine本地缓存管理器
     *
     * @return
     */
    @Bean("caffeineCacheManager")
    @Primary
    public CacheManager caffeineCacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        List<CaffeineCache> caches = new ArrayList<>();
        caches.add(new CaffeineCache(CacheConstants.COMB_INFO, combInfoCache()));
        caches.add(new CaffeineCache(CacheConstants.COMB_POS, combPosCache()));
        cacheManager.setCaches(caches);
        return cacheManager;
    }

    /**
     * 组合信息本地缓存（15min过期时间）
     *
     * @return
     */
    private Cache combInfoCache() {
        return Caffeine.newBuilder().expireAfterWrite(15, TimeUnit.MINUTES).recordStats().build();
    }

    /**
     * 组合持仓本地缓存（15min过期时间）
     *
     * @return
     */
    private Cache combPosCache() {
        return Caffeine.newBuilder().expireAfterWrite(15, TimeUnit.MINUTES).recordStats().build();
    }
}
