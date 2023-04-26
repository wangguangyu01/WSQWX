package com.tencent.wxcloudrun.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.ObjectUtils;

@Configuration
public class RedisConfig {


    /**
     * 主节点
     */
    @Value("${spring.redis.sentinel.master:#{null}}")
    private String master;


    @Value("${spring.redis.host:''}")
    private String host;

    @Value("${spring.redis.port:6379}")
    private int port = 6379;

    @Value("${security.shiro.globalSessionTimeout:1800}")
    private int expire = 0;

    @Value("${spring.redis.timeout:2000ms}")
    private String timeoutMs;

    @Value("${spring.redis.password:#{null}}")
    private String password;

    @Value("${spring.redis.sentinel.password:#{null}}")
    private String sentinelPassword;

    /**
     * 节点
     */
    @Value("${spring.redis.sentinel.nodes:#{null}}")
    private String nodes;

    @Value("${spring.redis.listenerKeyName: closeCase,returnDept}")
    private String listenerKeyName;

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        // 哨兵模式
        JedisConnectionFactory factory = null;
        if (ObjectUtils.isEmpty(master)) {
            RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
            redisStandaloneConfiguration.setPassword(password);
            redisStandaloneConfiguration.setHostName(host);
            redisStandaloneConfiguration.setPort(port);
            factory = new JedisConnectionFactory(redisStandaloneConfiguration);
        } else {
            RedisSentinelConfiguration redisConfiguration = new RedisSentinelConfiguration();
            redisConfiguration.setMaster(master);
            if (StringUtils.isNotBlank(nodes)) {
                String[] ipPortArr = StringUtils.split(nodes, ",");
                for (String ipPort : ipPortArr) {
                    String[] ipAndPort = StringUtils.split(ipPort, ":");
                    RedisNode redisNode = new RedisNode(ipAndPort[0], NumberUtils.toInt(ipAndPort[1]));
                    redisConfiguration.addSentinel(redisNode);
                }
            }
            redisConfiguration.setPassword(RedisPassword.of(sentinelPassword));
            // 哨兵模式
            factory = new JedisConnectionFactory(redisConfiguration);

        }

        return factory;
    }


    @Bean
    public RedisTemplate redisTemplate() {
        RedisTemplate redisTemplate = new RedisTemplate();
        redisTemplate.setConnectionFactory(jedisConnectionFactory());
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);
        //重点在这四行代码
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }



//    @Bean
//    public RedisMessageListenerContainer listenerContainer() {
//        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
//        container.setConnectionFactory(jedisConnectionFactory());
//        return container;
//    }
//
//
//    @Bean
//    public RedisKeyChangeListener redisKeyChangeListener() {
//        RedisKeyChangeListener redisKeyChangeListener = new RedisKeyChangeListener(listenerContainer(), listenerKeyName);
//        return redisKeyChangeListener;
//    }

}
