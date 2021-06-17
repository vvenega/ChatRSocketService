package com.example.demo;

import java.util.concurrent.Executors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericToStringSerializer;

@Configuration
@EnableRedisRepositories
@ComponentScan("com.javasampleapproach.redis.pubsub")
public class RedisConfig {
	
	String topic;
	public final static  String REDIS_SERVER="127.0.0.1";
	public final static int REDIS_PORT=6379;
	
	@Bean
	JedisConnectionFactory jedisConnectionFactory() {
	    
	    
	    RedisStandaloneConfiguration redisStandaloneConfiguration = 
	    		new RedisStandaloneConfiguration(REDIS_SERVER, REDIS_PORT);
	    //redisStandaloneConfiguration.setPassword(RedisPassword.of("yourRedisPasswordIfAny"));
	    JedisConnectionFactory jedisConFactory
	      = new JedisConnectionFactory(redisStandaloneConfiguration);
	    jedisConFactory.afterPropertiesSet();
	    return  jedisConFactory;
	    
	}
	

	@Bean
	public RedisTemplate<String,MessageDao> redisTemplate() {
		final RedisTemplate<String,MessageDao> template = new RedisTemplate<String,MessageDao>();
		template.setConnectionFactory(jedisConnectionFactory());
		template.setValueSerializer(new GenericToStringSerializer<MessageDao>(MessageDao.class));
		template.afterPropertiesSet();
		return template;
	}
	
	


	@Bean
	MessageListenerAdapter messageListener() {
		return new MessageListenerAdapter(new MessageSubscriber());
	}
	
	
	@Bean
	RedisMessageListenerContainer redisContainer() {
		final RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setConnectionFactory(jedisConnectionFactory());
		//container.addMessageListener(messageListener(), topic());
		container.setTaskExecutor(Executors.newFixedThreadPool(4));
		container.afterPropertiesSet();
		container.start();

		return container;
	}

	@Bean
	MessagePublisher redisPublisher() {
		return new MessagePublisher(redisTemplate(), topic());
	}

	@Bean
	ChannelTopic topic() {
		//return new ChannelTopic("pubsub:jsa-channel");
		return new ChannelTopic("pubsub:"+topic);
	}

}
