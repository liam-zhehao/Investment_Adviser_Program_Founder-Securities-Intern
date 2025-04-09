package com.foundersc.ifte.invest.adviser.web.constants;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * redis的相关常量
 */
@Slf4j
public class RedisConstants {
    private RedisConstants() {
    }

    /**
     * 缓存空间对应的过期时间，单位：分钟
     */
    private final static Map<String, Integer> cacheName2Ttl = new ConcurrentHashMap<>();

    /**
     * 获取cacheName对应的过期时间，单位分钟
     *
     * @return
     */
    public static Map<String, Integer> getCacheName2Ttl() {
        if (!CollectionUtils.isEmpty(cacheName2Ttl)) {
            return cacheName2Ttl;
        }
        log.info("init cacheName2Ttl");
        initCacheName2Ttl();
        for (Map.Entry<String, Integer> entry : cacheName2Ttl.entrySet()) {
            log.info("init cacheNameTtl: {} {}min", entry.getKey(), entry.getValue());
        }
        return cacheName2Ttl;
    }

    /**
     * 初始化
     */
    private static void initCacheName2Ttl() {
        cacheName2Ttl.put(COMB_INFO, 10);
        cacheName2Ttl.put(COMB_TREND, 10);
        cacheName2Ttl.put(COMB_POS, 10);
        cacheName2Ttl.put(COMB_RULE, 10);
        cacheName2Ttl.put(KYC_PAPER, 1);
        cacheName2Ttl.put(KYC_TESTJOUR, 10);
        cacheName2Ttl.put(COMB_PURCHASE_COPYWRITING, 30);
        cacheName2Ttl.put(EPAPER, 10);
        cacheName2Ttl.put(COMB_REDEEM_COPYWRITING, 30);
        cacheName2Ttl.put(TARGET_CURR_LIST, 2);
        cacheName2Ttl.put(TARGET_HIS_LIST, 20);
        cacheName2Ttl.put(TARGET_KYC_INFO, 2);
    }

    /**
     * 前缀
     */
    public static final String PREFIX = "fin.invest.adviser.web:";

    /**
     * 分布式锁的前缀
     */
    public static final String REDIS_LOCK_PREFIX = PREFIX + "lock:";

    /**
     * 组合基本信息的缓存空间
     */
    public static final String COMB_INFO = "comb_info";

    /**
     * 当前目标盈列表
     */
    public static final String TARGET_CURR_LIST = "target_curr_list";

    /**
     * 历史目标盈列表
     */
    public static final String TARGET_HIS_LIST = "target_his_list";

    /**
     * KYC 信息展示
     */
    public static final String TARGET_KYC_INFO = "target_kyc_info";

    /**
     * 组合行情趋势图的缓存空间
     */
    public static final String COMB_TREND = "comb_trend";

    /**
     * 组合持仓详情的缓存空间
     */
    public static final String COMB_POS = "comb_pos";

    /**
     * 组合交易规则的缓存空间
     */
    public static final String COMB_RULE = "comb_rule";

    /**
     * kyc问卷信息缓存
     */
    public static final String KYC_PAPER = "kyc_paper";

    /**
     * kyc用户答题信息缓存
     */
    public static final String KYC_TESTJOUR = "kyc_testjour";

    /**
     * 转入文案信息
     */
    public static final String COMB_PURCHASE_COPYWRITING = "comb_pur_cw";

    /**
     * 电子协议
     */
    public static final String EPAPER = "comb_epaper";

    /**
     * 转出文案信息
     */
    public static final String COMB_REDEEM_COPYWRITING = "comb_red_cw";

    /**
     * 购买加锁的前缀
     */
    public static final String PURCHASE_LOCK_KEY_PREFIX = "purchase:";
}
