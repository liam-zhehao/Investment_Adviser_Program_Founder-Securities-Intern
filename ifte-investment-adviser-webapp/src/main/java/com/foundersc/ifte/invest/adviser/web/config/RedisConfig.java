package com.foundersc.ifte.invest.adviser.web.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.foundersc.ifte.invest.adviser.web.constants.RedisConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * redisTemplate配置
 */
@Slf4j
@Configuration
@EnableCaching
@PropertySource("classpath:application-${spring.profiles.active}.yml")
public class RedisConfig {

    @Autowired
    Environment env;

    @Bean("redisConnectionFactory")
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource.redis")
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setHostName(env.getProperty("spring.redis.host"));
        configuration.setPort(Integer.parseInt(env.getProperty("spring.redis.port")));
        configuration.setPassword(env.getProperty("spring.redis.password"));
        configuration.setDatabase(Integer.parseInt(env.getProperty("spring.redis.database")));
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(Integer.parseInt(env.getProperty("spring.redis.max-idle")));
        jedisPoolConfig.setMinIdle(Integer.parseInt(env.getProperty("spring.redis.min-idle")));
        jedisPoolConfig.setMaxTotal(Integer.parseInt(env.getProperty("spring.redis.max-active")));
        jedisPoolConfig.setMaxWaitMillis(Integer.parseInt(env.getProperty("spring.redis.max-wait")));
        jedisPoolConfig.setTestOnBorrow(Boolean.parseBoolean(env.getProperty("spring.redis.testOnBorrow")));
        JedisClientConfiguration.JedisClientConfigurationBuilder configurationBuilder = JedisClientConfiguration.builder();
        JedisClientConfiguration jedisClientConfiguration = configurationBuilder.usePooling().poolConfig(jedisPoolConfig).build();
        return new JedisConnectionFactory(configuration, jedisClientConfiguration);
    }

    @Bean("stringRedisTemplate")
    @Primary
    public StringRedisTemplate stringRedisTemplate(@Qualifier("redisConnectionFactory") RedisConnectionFactory redisConnectionFactory) {
        return new StringRedisTemplate(redisConnectionFactory);
    }

    @Bean("redisTemplate")
    public RedisTemplate redisTemplate(@Qualifier("redisConnectionFactory") RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate redisTemplate = new RedisTemplate();
        redisTemplate.setKeySerializer(defaultKeyRedisSerializer());
        redisTemplate.setHashKeySerializer(defaultValueRedisSerializer());
        redisTemplate.setValueSerializer(defaultValueRedisSerializer());
        redisTemplate.setHashValueSerializer(defaultValueRedisSerializer());
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        return redisTemplate;
    }

    /**
     * 默认key序列化器
     *
     * @return
     */
    private RedisSerializer defaultKeyRedisSerializer() {
        return new StringRedisSerializer();
    }

    /**
     * 默认value序列化器
     *
     * @return
     */
    private RedisSerializer defaultValueRedisSerializer() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.configure(MapperFeature.USE_ANNOTATIONS, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        // 此项必须配置，否则会报java.lang.ClassCastException: java.util.LinkedHashMap cannot be cast to XXX
        objectMapper.activateDefaultTyping(objectMapper.getPolymorphicTypeValidator(), ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        GenericJackson2JsonRedisSerializer jackson2JsonRedisSerializer = new GenericJackson2JsonRedisSerializer(objectMapper);
        return jackson2JsonRedisSerializer;
    }

    /**
     * @param redisConnectionFactory
     * @功能描述 redis作为缓存时配置缓存管理器CacheManager，主要配置序列化方式、自定义
     * <p>
     * 注意：配置缓存管理器CacheManager有两种方式：
     * 方式1：通过RedisCacheConfiguration.defaultCacheConfig()获取到默认的RedisCacheConfiguration对象，
     * 修改RedisCacheConfiguration对象的序列化方式等参数【这里就采用的这种方式】
     * 方式2：通过继承CachingConfigurerSupport类自定义缓存管理器，覆写各方法，参考：
     * https://blog.csdn.net/echizao1839/article/details/102660649
     * <p>
     * 切记：在缓存配置类中配置以后，yaml配置文件中关于缓存的redis配置就不会生效，如果需要相关配置需要通过@value去读取
     */
    @Bean("redisCacheManager")
    public CacheManager cacheManager(@Qualifier("redisConnectionFactory") RedisConnectionFactory redisConnectionFactory) {
        // 专门指定某些缓存空间的配置，如果过期时间【主要这里的key为缓存空间名称】
        Map<String, RedisCacheConfiguration> map = new HashMap<>();
        for (Map.Entry<String, Integer> entry : RedisConstants.getCacheName2Ttl().entrySet()) {
            // 指定特定缓存空间对应的过期时间
            RedisCacheConfiguration redisCacheConfiguration = redisCacheConfiguration();
            map.put(entry.getKey(), redisCacheConfiguration.entryTtl(Duration.ofMinutes(entry.getValue())));
        }
        return RedisCacheManager
                .builder(redisConnectionFactory)
                .cacheDefaults(redisCacheConfiguration())
                .withInitialCacheConfigurations(map)
                .build();
    }

    /**
     * redisCache配置
     *
     * @return
     */
    private RedisCacheConfiguration redisCacheConfiguration() {
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig();
        // 默认无过期时间
        redisCacheConfiguration = redisCacheConfiguration
                // 设置key采用String的序列化方式
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(defaultKeyRedisSerializer()))
                // 设置value序列化方式采用jackson方式序列化
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(defaultValueRedisSerializer()))
                // 当value为null时不进行缓存
                .disableCachingNullValues()
                // 配置缓存空间名称的前缀
                .prefixCacheNameWith(RedisConstants.PREFIX);
        return redisCacheConfiguration;
    }
}
