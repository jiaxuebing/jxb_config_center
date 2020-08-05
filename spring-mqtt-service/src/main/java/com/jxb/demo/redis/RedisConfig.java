package com.jxb.demo.redis;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;


@Configuration
public class RedisConfig {

    @Bean(name = "redisTemplate")
    public RedisTemplate getRedisTemplate(RedisConnectionFactory redisConnectionFactory){
        RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
        template.setConnectionFactory(redisConnectionFactory);
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        // key采用String的序列化方式
        template.setKeySerializer(stringRedisSerializer);
        // hash的key也采用String的序列化方式
        template.setHashKeySerializer(stringRedisSerializer);
        // value序列化方式采用jackson
        template.setValueSerializer(jackson2JsonRedisSerializer);
        // hash的value序列化方式采用jackson
        template.setHashValueSerializer(jackson2JsonRedisSerializer);
        template.afterPropertiesSet();
        return template;
    }



//    @Bean("jedisConnectionFactory")
//    public JedisConnectionFactory createJedisConnectionFactory(RedisStandaloneConfiguration redisStandaloneConfiguration,JedisClientConfiguration jedisClientConfiguration){
//        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(redisStandaloneConfiguration,jedisClientConfiguration);
//        return jedisConnectionFactory;
//    }
//
//    @Bean("redisStandaloneConfiguration")
//    public RedisStandaloneConfiguration createRedisStandaloneConfiguration(){
//        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
//        redisStandaloneConfiguration.setHostName(redisProperties.getHost());
//        redisStandaloneConfiguration.setPort(redisProperties.getPort());
//        return redisStandaloneConfiguration;
//    }
//
//    @Bean("jedisClientConfiguration")
//    public JedisClientConfiguration createJedisClientConfiguration(){
//        JedisClientConfiguration.DefaultJedisClientConfigurationBuilder jedisClientBuilder = (JedisClientConfiguration.DefaultJedisClientConfigurationBuilder)JedisClientConfiguration.builder();
//        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
//        jedisPoolConfig.setMaxTotal(redisProperties.getJedis().getPool().getMaxActive());
//        jedisPoolConfig.setMaxIdle(redisProperties.getJedis().getPool().getMaxIdle());
//        jedisPoolConfig.setMaxWaitMillis(redisProperties.getJedis().getPool().getMaxWait().toMillis());
//        jedisClientBuilder.poolConfig(jedisPoolConfig);
//        jedisClientBuilder.usePooling();
//        JedisClientConfiguration jedisClientConfiguration = jedisClientBuilder.build();
//        return jedisClientConfiguration;
//    }


}
