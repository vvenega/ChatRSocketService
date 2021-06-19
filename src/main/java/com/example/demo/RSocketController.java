package com.example.demo;

import org.springframework.stereotype.Controller;

import io.reactivex.Observable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.rsocket.RSocketRequester;

import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;


@Controller
public class RSocketController {
	
	
	public static void main(String args[]) {
		
		MessageBean dao = new MessageBean();
		dao.setDate("");
		dao.setMessage("@protocol");
		dao.setNamereceiver("Andrea");
		dao.setNamesender("Victor");
		dao.setObjectid("123");
		dao.setReceiver("owner160");
		dao.setSender("vvenega");
		dao.setChannel("mychannel_vvenega");
		
		RedisMessageListenerContainer container=new RedisConfig().redisContainer();
        List<String> listeners=new ArrayList<String>();
		
        RSocketController controller =new RSocketController();
        controller.publish(dao, container, listeners);
		
		dao = new MessageBean();
		dao.setDate("");
		dao.setMessage("@protocol");
		dao.setNamereceiver("Victor");
		dao.setNamesender("Andrea");
		dao.setObjectid("123");
		dao.setReceiver("vvenega");
		dao.setSender("owner160");
		dao.setChannel("mychannel_owner160");
		
		controller.publish(dao, container, listeners);
		
		dao = new MessageBean();
		dao.setDate("");
		dao.setMessage("Hola");
		dao.setNamereceiver("Andrea");
		dao.setNamesender("Victor");
		dao.setObjectid("123");
		dao.setReceiver("owner160");
		dao.setSender("vvenega");
		dao.setChannel("mychannel_vvenega");
		
		controller.publish(dao, container, listeners);
		
		
		/*MessageRepository messageRepository;
		RedisConfig config = new RedisConfig();
		config.topic=dao.getChannel();
		
		String channel = "N/A";
        List<String> channelList = new ArrayList<String>();
        channelList.add(channel);
        RedisMessageListenerContainer container=new RedisConfig().redisContainer();
        List<String> listeners=new ArrayList<String>();
        
        RSocketController object =new RSocketController();

        object.publish(dao,container,listeners);*/
		
		/*messageRepository=new MessageRepository(config.redisTemplate());
		messageRepository.saveMessage(dao);
		messageRepository.getMessages("mychannel_owner160");*/
		//messageRepository.saveMessage(dao);
		
	}
	
	
	@Autowired
	ChatServiceDaoProxy proxy;
	
	 
	@MessageMapping("channel")
	Flux<MessageBean> channel(RSocketRequester requester,@Payload Flux<MessageBean> client) {
        System.err.println("Received channel request...");
        System.err.println("RSocketRequester..."+requester.toString());
        
        String channel = "N/A";
        List<String> channelList = new ArrayList<String>();
        channelList.add(channel);
        RedisMessageListenerContainer container=new RedisConfig().redisContainer();
        List<String> listeners=new ArrayList<String>();
        
        client.subscribe(message -> {
        	this.publish(message,container,listeners);
        	channelList.set(0, message.getChannel());
        });
        
       
        return Flux.interval(Duration.ofSeconds(3))
                .doOnCancel(() -> {
                	System.err.println("The client cancelled the channel.");
                	try {
                	container.destroy();
                	listeners.clear();
                	}catch(Exception e) {
                		e.printStackTrace();
                	}
                	
                })
                .map(index -> this.getMessages(channelList))
                .flatMapIterable(msgs -> msgs);
                
                

       

    }
	
	
	private static Observable<MessageBean> getMessageObservable(MessageBean msg) {
 
        return Observable.just(msg);
    }
	

 private List<MessageBean> getMessages( List<String> channelList){
	 List<MessageBean> lst = new ArrayList<MessageBean>();
	 try {
		 String channel =channelList.get(0);
		 //System.err.println(channel);
		 if(channel!=null && !channel.equals("N/A")) {
		 RedisConfig config = new RedisConfig();
		 MessageRepository repository = new MessageRepository(config.redisTemplate());
		 lst=repository.getMessages(channel);
		 }
		 
	 }catch(Exception e) {
		 e.printStackTrace();
		 lst = new ArrayList<MessageBean>();
	 }
	 
	 return lst;
	 
 }

	
   private void publish(MessageBean message,RedisMessageListenerContainer container,List<String> listeners) {
	   try {
		   
		   if(message!=null) {	   

		   RedisConfig config = new RedisConfig();
		   config.topic=message.getChannel();
		   
		   if(!listeners.contains(message.getChannel())) {

			   System.err.println("Subscribing to Topic with channel:"+message.getChannel());
    		   MessageSubscriber chatter = new MessageSubscriber();
    		   container.addMessageListener(chatter, config.topic());
			   
			   listeners.add(message.getChannel());
			   
		   }
		   
		   if(message!=null && !message.getMessage().equals("@protocol_connect")) {
		   String destChannel="mychannel_"+message.getReceiver();
		   System.err.println("idconversation_"+message.getIdconversation());
		   //RedisConfig config = new RedisConfig();
		   config.topic=destChannel;
		   MessagePublisher publisher = config.redisPublisher();

		   publisher.publish(MessageBuilder.toDao(message));
		   
		   
		   if(message!=null && !message.getMessage().startsWith("@protocol")) {
		   String name = message.getNamesender();
	        
	        if(name!=null) {
			name = name.replace("/", "-");
			name = name.replace("\\", "-");
			message.setNamesender(name);
	        }
	        
	        name = message.getNamereceiver();
	        if(name!=null) {
	    		name = name.replace("/", "-");
	    		name = name.replace("\\", "-");
	    		message.setNamereceiver(name);
	            }
			
			String msg = message.getMessage();
			msg = msg.replace("/", "-");
			msg = msg.replace("\\", "-");
			message.setMessage(msg);

			
	        proxy.broadcastMessage(message.getMessage(),message.getSender(),
	        		message.getReceiver(),message.getChannel(),message.getId(),
	        		message.getObjectid(),message.getNamesender(),message.getNamereceiver());
		   }
		   }
		   }else 
			   System.err.println("Message is Null!");
	        
		   
	   }catch(Exception e) {
		   e.printStackTrace();
	   }
   }


	

}
