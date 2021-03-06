package com.example.demo;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

import com.fasterxml.jackson.databind.ObjectMapper;

public class MessageSubscriber implements MessageListener{
	
   MessageRepository messageRepository;
	
	
	public MessageSubscriber() {
		super();
	}
	
	@Override
	public void onMessage(Message message, byte[] pattern) {
		try {
		System.out.println("Received >> " + message +  ", " + Thread.currentThread().getName() );
		ObjectMapper mapper = new ObjectMapper();
		MessageDao messageDao = mapper.readValue(message.toString(), MessageDao.class);
		System.err.println("Subscriber:"+messageDao.getObjectid());
		System.err.println("Sender:"+messageDao.getSender());
		System.err.println("Receiver:"+messageDao.getReceiver());
		
		RedisConfig config = new RedisConfig();
		
		config.topic=messageDao.getChannel();
		
		messageRepository=new MessageRepository(config.redisTemplate());
		messageRepository.saveMessage(messageDao);
		
		

		}catch(Exception e) {
			System.err.println(e.getMessage());
		}
	}
	

}
