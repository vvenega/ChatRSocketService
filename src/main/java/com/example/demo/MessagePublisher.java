package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

import com.fasterxml.jackson.databind.ObjectMapper;

public class MessagePublisher {
	
	@Autowired
	private RedisTemplate<String,MessageDao> redisTemplate;

	@Autowired
	public ChannelTopic topic;
	
	public MessagePublisher() {}
	
	public MessagePublisher(RedisTemplate<String,MessageDao> redisTemplate, ChannelTopic topic) {
		this.redisTemplate = redisTemplate;
		this.topic = topic;
	}

	public void publish(MessageDao message) {
		
		try {
		ObjectMapper mapper = new ObjectMapper();
		String strMessage = mapper.writeValueAsString(message);
		RedisConfig config = new RedisConfig();
		
		String destChannel="mychannel_"+message.getReceiver();
		System.err.println("Sending message from channel:"+message.getChannel()
		+" to channel "+destChannel);
		config.topic=destChannel;
		
		//MessageRepository messageRepository= new MessageRepository(config.redisTemplate());
		
		//messageRepository.saveMessage(message);
		
		redisTemplate.convertAndSend(config.topic, strMessage);
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		

	}

}
